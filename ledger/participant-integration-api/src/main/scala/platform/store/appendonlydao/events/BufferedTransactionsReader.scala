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
import com.daml.logging.LoggingContext
import com.daml.platform.ApiOffset
import com.daml.platform.api.v1.event.EventOps.TreeEventOps
import com.daml.platform.participant.util.LfEngineToApi
import com.daml.platform.store.appendonlydao.events
import com.daml.platform.store.appendonlydao.events.BufferedTransactionsReader.Transaction
import com.daml.platform.store.cache.MutableCacheBackedContractStore.EventSequentialId
import com.daml.platform.store.dao.LedgerDaoTransactionsReader
import com.daml.platform.store.dao.events.ContractStateEvent
import com.daml.scalautil.Statement.discard
import com.google.protobuf.timestamp.Timestamp

import scala.concurrent.Future

// TDT Handle verbose lf decode
class BufferedTransactionsReader(
    protected val delegate: TransactionsReader,
    bufferedTransactions: BufferedTransactions,
) extends LedgerDaoTransactionsReader
    with DelegateTransactionsReader {

  private def toTxTree(tx: Transaction, requestingParties: Set[Party]): TransactionTree = {
    val treeEvents = tx.events
      .collect {
        // TDT handle multi-party submissions
        case createdEvent: BufferedTransactionsReader.CreatedEvent
            if requestingParties == createdEvent.treeEventWitnesses =>
          TreeEvent(
            TreeEvent.Kind.Created(
              com.daml.ledger.api.v1.event.CreatedEvent(
                eventId = createdEvent.eventId.toLedgerString,
                contractId = createdEvent.contractId.toString,
                templateId = Some(LfEngineToApi.toApiIdentifier(createdEvent.templateId)),
                contractKey = createdEvent.contractKey
                  .map(LfEngineToApi.lfVersionedValueToApiValue(false, _))
                  .map(_.getOrElse(throw new RuntimeException("Could not convert to API value"))),
                createArguments = Some(
                  LfEngineToApi
                    .lfVersionedValueToApiRecord(verbose = false, createdEvent.createArgument)
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
            if requestingParties == exercisedEvent.treeEventWitnesses =>
          TreeEvent(
            TreeEvent.Kind.Exercised(
              com.daml.ledger.api.v1.event.ExercisedEvent(
                eventId = exercisedEvent.eventId.toLedgerString,
                contractId = exercisedEvent.contractId.toString,
                templateId = Some(LfEngineToApi.toApiIdentifier(exercisedEvent.templateId)),
                choice = exercisedEvent.choice,
                choiceArgument = Some(
                  LfEngineToApi
                    .lfVersionedValueToApiValue(verbose = false, exercisedEvent.exerciseArgument)
                    .getOrElse(throw new RuntimeException("Could not convert to API value"))
                ),
                actingParties = exercisedEvent.actingParties.toSeq,
                consuming = false,
                witnessParties = exercisedEvent.treeEventWitnesses.toSeq,
                childEventIds = exercisedEvent.children,
                exerciseResult = exercisedEvent.exerciseResult.map(
                  LfEngineToApi
                    .lfVersionedValueToApiValue(false, _)
                    .getOrElse(throw new RuntimeException("Could not convert to API value"))
                ),
              )
            )
          )
      }

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
  }

  override def getTransactionTrees(
      startExclusive: Offset,
      endInclusive: Offset,
      requestingParties: Set[Party],
      verbose: Boolean,
  )(implicit
      loggingContext: LoggingContext
  ): Source[(Offset, GetTransactionTreesResponse), NotUsed] =
    bufferedTransactions.getTransactions(startExclusive, endInclusive) match {
      case (bufferedStartExclusive, bufferedSource) =>
        if (bufferedStartExclusive > startExclusive)
          delegate
            .getTransactionTrees(startExclusive, bufferedStartExclusive, requestingParties, verbose)
            .concat(bufferedSource.map { case (offset, tx) =>
              offset -> GetTransactionTreesResponse(Seq(toTxTree(tx, requestingParties)))
            })
        else
          bufferedSource.map { case (offset, tx) =>
            offset -> GetTransactionTreesResponse(Seq(toTxTree(tx, requestingParties)))
          }
    }

  private def instantToTimestamp(t: Instant): Timestamp =
    Timestamp(seconds = t.getEpochSecond, nanos = t.getNano)
}

class BufferedTransactions(maxTransactions: Int) {
  private val buffer = scala.collection.concurrent.TrieMap.empty[Offset, Transaction]

  def push(offset: Offset, transaction: Transaction): Unit = buffer.synchronized {
    discard {
      {
        if (buffer.size == maxTransactions)
          buffer.drop(1)
        else buffer
      } += (offset -> transaction)
    }
  }

  def getTransactions(
      startExclusive: Offset,
      endInclusive: Offset,
  ): (Offset, Source[(Offset, Transaction), NotUsed]) =
    buffer.lastOption
      .map {
        case (bufferEndInclusive, _) if bufferEndInclusive < endInclusive =>
          // TDT extract into proper Error
          throw new RuntimeException(
            s"Requested endInclusive $endInclusive supersedes bufferedEndInclusive $bufferEndInclusive"
          )
        case _ =>
          buffer.head match {
            case (bufferStartExclusive, _) if bufferStartExclusive >= endInclusive =>
              startExclusive -> Source.empty
            case _ =>
              var bufferedStartExclusive = startExclusive

              val source = Source.fromIterator(() =>
                buffer
                  .dropWhile { case (offset, _) =>
                    offset > startExclusive && {
                      bufferedStartExclusive = offset
                      true
                    }
                  }
                  .takeWhile(_._1 <= endInclusive)
                  .toIterator
              )
              bufferedStartExclusive -> source
          }
      }
      .getOrElse(startExclusive -> Source.empty)
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
