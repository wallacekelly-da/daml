package com.daml.platform.store.appendonlydao.events

import java.time.Instant

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.daml.ledger.api.v1.active_contracts_service.GetActiveContractsResponse
import com.daml.ledger.api.v1.transaction.{TransactionTree, TreeEvent}
import com.daml.ledger.api.v1.transaction_service.{
  GetFlatTransactionResponse,
  GetTransactionResponse,
  GetTransactionTreesResponse,
  GetTransactionsResponse,
}
import com.daml.ledger.participant.state.v1.{Offset, TransactionId}
import com.daml.lf.data.Ref.IdString
import com.daml.lf.ledger.EventId
import com.daml.lf.value.{Value => LfValue}
import com.daml.logging.{ContextualizedLogger, LoggingContext}
import com.daml.metrics.{Metrics, Timed}
import com.daml.platform.ApiOffset
import com.daml.platform.api.v1.event.EventOps.TreeEventOps
import com.daml.platform.indexer.parallel.PerfSupport.instrumentedBufferedSource
import com.daml.platform.participant.util.LfEngineToApi
import com.daml.platform.store.appendonlydao.events
import com.daml.platform.store.appendonlydao.events.BufferedTransactionsReader.Transaction
import com.daml.platform.store.cache.MutableCacheBackedContractStore.EventSequentialId
import com.daml.platform.store.dao.LedgerDaoTransactionsReader
import com.daml.platform.store.dao.events.ContractStateEvent
import com.daml.scalautil.Statement.discard
import com.google.protobuf.timestamp.Timestamp

import scala.collection.mutable
import scala.concurrent.Future

// TDT Handle verbose lf decode
class BufferedTransactionsReader(
    protected val delegate: TransactionsReader,
    val bufferedTransactions: BufferedTransactions,
    metrics: Metrics,
) extends LedgerDaoTransactionsReader
    with DelegateTransactionsReader {

  private val logger = ContextualizedLogger.get(getClass)

  private val outputStreamBufferSize = 128

  override def getTransactionTrees(
      startExclusive: Offset,
      endInclusive: Offset,
      requestingParties: Set[Party],
      verbose: Boolean,
  )(implicit
      loggingContext: LoggingContext
  ): Source[(Offset, GetTransactionTreesResponse), NotUsed] = {
    val transactionTreesSource = Timed.source(
      metrics.daml.index.getTransactionsSource, {
        bufferedTransactions.getTransactions(startExclusive, endInclusive) match {
          case ((bufferedStartExclusive, bufferedEndInclusive), bufferedSource) =>
            if (bufferedStartExclusive > startExclusive) {
              val bufferedTxs = bufferedSource
                .map { case (offset, tx) =>
                  offset -> toTxTree(tx, requestingParties, verbose)
                }
                .collect { case (offset, Some(tree)) =>
                  metrics.daml.index.transactionEventsResolvedFromBuffer.inc()
                  offset -> GetTransactionTreesResponse(Seq(tree))
                }

              logger.info(
                s"Buffered $startExclusive -> $bufferedStartExclusive -> $bufferedEndInclusive -> $endInclusive"
              )

              val transactionsAfterBuffer =
                if (bufferedEndInclusive < endInclusive)
                  delegate
                    .getTransactionTrees(
                      bufferedEndInclusive,
                      endInclusive,
                      requestingParties,
                      verbose,
                    )
                else Source.empty

              delegate
                .getTransactionTrees(
                  startExclusive,
                  bufferedStartExclusive,
                  requestingParties,
                  verbose,
                )
                .concat(bufferedTxs)
                .concat(transactionsAfterBuffer)
                .map(tx => {
                  metrics.daml.index.totalTransactionsRetrieved.inc()
                  tx
                })
            } else {
              val transactionsAfterBuffer =
                if (bufferedEndInclusive < endInclusive)
                  delegate
                    .getTransactionTrees(
                      bufferedEndInclusive,
                      endInclusive,
                      requestingParties,
                      verbose,
                    )
                else Source.empty

              bufferedSource
                .map { case (offset, tx) =>
                  offset -> toTxTree(tx, requestingParties, verbose)
                }
                .collect { case (offset, Some(tree)) =>
                  metrics.daml.index.transactionEventsResolvedFromBuffer.inc()
                  offset -> GetTransactionTreesResponse(Seq(tree))
                }
                .concat(transactionsAfterBuffer)
                .map(tx => {
                  metrics.daml.index.totalTransactionsRetrieved.inc()
                  tx
                })
            }
        }
      },
    )

    instrumentedBufferedSource(
      original = transactionTreesSource,
      counter = metrics.daml.index.transactionTreesBufferSize,
      size = outputStreamBufferSize,
    )
  }

  private def toTxTree(
      tx: Transaction,
      requestingParties: Set[Party],
      verbose: Boolean,
  ): Option[TransactionTree] = {
    val treeEvents = tx.events
      .collect {
        // TDT handle multi-party submissions
        case createdEvent: BufferedTransactionsReader.CreatedEvent
            if createdEvent.treeEventWitnesses
              .intersect(requestingParties.asInstanceOf[Set[String]])
              .nonEmpty =>
          TreeEvent(
            TreeEvent.Kind.Created(
              com.daml.ledger.api.v1.event.CreatedEvent(
                eventId = createdEvent.eventId.toLedgerString,
                contractId = createdEvent.contractId.coid,
                templateId = Some(LfEngineToApi.toApiIdentifier(createdEvent.templateId)),
                contractKey = createdEvent.contractKey
                  .map(LfEngineToApi.lfVersionedValueToApiValue(verbose, _))
                  .map(_.getOrElse(throw new RuntimeException("Could not convert to API value"))),
                createArguments = Some(
                  LfEngineToApi
                    .lfVersionedValueToApiRecord(verbose = verbose, createdEvent.createArgument)
                    .getOrElse(throw new RuntimeException("Could not convert to API value"))
                ),
                witnessParties = createdEvent.treeEventWitnesses.toSeq,
                signatories = createdEvent.createSignatories.toSeq,
                observers = createdEvent.createObservers.toSeq,
                agreementText = createdEvent.createAgreementText.orElse(Some("")),
              )
            )
          )
        case exercisedEvent: BufferedTransactionsReader.ExercisedEvent
            if exercisedEvent.treeEventWitnesses
              .intersect(requestingParties.asInstanceOf[Set[String]])
              .nonEmpty =>
          TreeEvent(
            TreeEvent.Kind.Exercised(
              com.daml.ledger.api.v1.event.ExercisedEvent(
                eventId = exercisedEvent.eventId.toLedgerString,
                contractId = exercisedEvent.contractId.coid,
                templateId = Some(LfEngineToApi.toApiIdentifier(exercisedEvent.templateId)),
                choice = exercisedEvent.choice,
                choiceArgument = Some(
                  LfEngineToApi
                    .lfVersionedValueToApiValue(verbose = verbose, exercisedEvent.exerciseArgument)
                    .getOrElse(throw new RuntimeException("Could not convert to API value"))
                ),
                actingParties = exercisedEvent.actingParties.toSeq,
                consuming = exercisedEvent.consuming,
                witnessParties = exercisedEvent.treeEventWitnesses.toSeq,
                childEventIds = exercisedEvent.children,
                exerciseResult = exercisedEvent.exerciseResult.map(
                  LfEngineToApi
                    .lfVersionedValueToApiValue(verbose, _)
                    .getOrElse(throw new RuntimeException("Could not convert to API value"))
                ),
              )
            )
          )
      }

    if (treeEvents.isEmpty)
      Option.empty
    else {

      val visible = treeEvents.map(_.eventId)
      val visibleSet = visible.toSet
      val eventsById = treeEvents.iterator
        .map(e => e.eventId -> e.filterChildEventIds(visibleSet))
        .toMap

      // All event identifiers that appear as a child of another item in this response
      val children = eventsById.valuesIterator.flatMap(_.childEventIds).toSet

      // The roots for this request are all visible items
      // that are not a child of some other visible item
      val rootEventIds = visible.filterNot(children)

      Some(
        TransactionTree(
          transactionId = tx.transactionId,
          commandId = tx.commandId.getOrElse(""), // TDT use submitters predicate to set commandId
          workflowId = tx.workflowId.getOrElse(""),
          effectiveAt = Some(instantToTimestamp(tx.effectiveAt)),
          offset = ApiOffset.toApiString(tx.offset),
          eventsById = eventsById,
          rootEventIds = rootEventIds,
          traceContext = None,
        )
      )
    }
  }

  private def instantToTimestamp(t: Instant): Timestamp =
    Timestamp(seconds = t.getEpochSecond, nanos = t.getNano)
}

class BufferedTransactions(maxTransactions: Int, metrics: Metrics) {
  private val buffer = mutable.Queue.empty[(Offset, Transaction)]

  def push(offset: Offset, transaction: Transaction): Unit = buffer.synchronized {
    Timed.value(
      metrics.daml.index.bufferPushTransaction, {
        discard {
          {
            if (buffer.size == maxTransactions) {
              metrics.daml.index.bufferSize.dec()
              discard(buffer.dequeue())
              buffer
            } else buffer
          }.enqueue {
            metrics.daml.index.bufferSize.inc()
            offset -> transaction
          }
        }
      },
    )
  }

  def getTransactions(
      startExclusive: Offset,
      endInclusive: Offset,
  ): ((Offset, Offset), Source[(Offset, Transaction), NotUsed]) =
    buffer.synchronized {
      buffer.headOption
        .map {
          case (bufferStartExclusive, _) if bufferStartExclusive >= endInclusive =>
            (startExclusive, startExclusive) -> Source.empty
          case _ =>
            var bufferedStartExclusive = startExclusive
            var bufferedEndInclusive = startExclusive

            Timed.value(
              metrics.daml.index.bufferGetTransactions, {
                val slice = buffer
                  .dropWhile { case (offset, _) =>
                    offset <= startExclusive && {
                      bufferedStartExclusive = offset
                      bufferedEndInclusive = offset
                      true
                    }
                  }
                  .takeWhile { case (offset, _) =>
                    offset <= endInclusive && {
                      bufferedEndInclusive = offset
                      true
                    }
                  }

                val source = Source.fromIterator(() => slice.iterator)
                (bufferedStartExclusive, bufferedEndInclusive) -> source
              },
            )
        }
        .getOrElse((startExclusive, startExclusive) -> Source.empty)
    }
}

object BufferedTransactionsReader {
  sealed trait TransactionEvent extends Product with Serializable

  final case class Transaction(
      transactionId: String,
      commandId: Option[String],
      workflowId: Option[String],
      effectiveAt: Instant,
      offset: Offset,
      lastEventSequentialId: EventSequentialId,
      events: Seq[Event],
  ) extends TransactionEvent

  final case class LedgerEndMarker(
      eventOffset: Offset,
      eventSequentialId: EventSequentialId,
  ) extends TransactionEvent

  sealed trait Event extends Product with Serializable {
    def eventOffset: Offset
    def eventSequentialId: EventSequentialId
    def transactionId: String
    def eventId: EventId
    def commandId: Option[String]
    def workflowId: Option[String]
    def ledgerEffectiveTime: Instant
  }

  final case class ExercisedEvent(
      eventOffset: Offset,
      transactionId: String,
      nodeIndex: Int,
      eventSequentialId: Long,
      eventId: EventId,
      contractId: ContractId,
      ledgerEffectiveTime: Instant,
      templateId: Identifier,
      commandId: Option[String],
      workflowId: Option[String],
      contractKey: Option[LfValue.VersionedValue[events.ContractId]],
      treeEventWitnesses: Set[String],
      flatEventWitnesses: Set[String],
      choice: String,
      actingParties: Set[IdString.Party],
      children: Seq[String],
      exerciseArgument: LfValue.VersionedValue[ContractId],
      exerciseResult: Option[LfValue.VersionedValue[ContractId]],
      consuming: Boolean,
  ) extends Event

  final case class CreatedEvent(
      eventOffset: Offset,
      transactionId: String,
      nodeIndex: Int,
      eventSequentialId: Long,
      eventId: EventId,
      contractId: ContractId,
      ledgerEffectiveTime: Instant,
      templateId: Identifier,
      commandId: Option[String],
      workflowId: Option[String],
      contractKey: Option[LfValue.VersionedValue[events.ContractId]],
      treeEventWitnesses: Set[String],
      flatEventWitnesses: Set[String],
      createArgument: LfValue.VersionedValue[events.ContractId],
      createSignatories: Set[String],
      createObservers: Set[String],
      createAgreementText: Option[String],
  ) extends Event
}

trait DelegateTransactionsReader extends LedgerDaoTransactionsReader {
  protected def delegate: TransactionsReader

  override def getFlatTransactions(
      startExclusive: Offset,
      endInclusive: Offset,
      filter: FilterRelation,
      verbose: Boolean,
  )(implicit loggingContext: LoggingContext): Source[(Offset, GetTransactionsResponse), NotUsed] =
    delegate.getFlatTransactions(startExclusive, endInclusive, filter, verbose)

  override def lookupFlatTransactionById(
      transactionId: TransactionId,
      requestingParties: Set[Party],
  )(implicit loggingContext: LoggingContext): Future[Option[GetFlatTransactionResponse]] =
    delegate.lookupFlatTransactionById(transactionId, requestingParties)

  override def lookupTransactionTreeById(
      transactionId: TransactionId,
      requestingParties: Set[Party],
  )(implicit loggingContext: LoggingContext): Future[Option[GetTransactionResponse]] =
    delegate.lookupTransactionTreeById(transactionId, requestingParties)

  override def getActiveContracts(activeAt: Offset, filter: FilterRelation, verbose: Boolean)(
      implicit loggingContext: LoggingContext
  ): Source[GetActiveContractsResponse, NotUsed] =
    delegate.getActiveContracts(activeAt, filter, verbose)

  override def getContractStateEvents(startExclusive: (Offset, Long), endInclusive: (Offset, Long))(
      implicit loggingContext: LoggingContext
  ): Source[((Offset, Long), ContractStateEvent), NotUsed] =
    delegate.getContractStateEvents(startExclusive, endInclusive)

  override def getTransactionEvents(
      startExclusive: (Offset, Long),
      endInclusive: (Offset, Long),
  )(implicit
      loggingContext: LoggingContext
  ): Source[((Offset, Long), BufferedTransactionsReader.TransactionEvent), NotUsed] =
    delegate.getTransactionEvents(startExclusive, endInclusive)
}
