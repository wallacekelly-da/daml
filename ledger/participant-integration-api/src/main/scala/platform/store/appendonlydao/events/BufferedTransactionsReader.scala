package com.daml.platform.store.appendonlydao.events

import java.time.Instant

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.codahale.metrics.{Counter, Timer}
import com.daml.ledger.api.v1.active_contracts_service.GetActiveContractsResponse
import com.daml.ledger.api.v1.transaction.{TransactionTree, TreeEvent, Transaction => FlatTx}
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
import com.daml.platform.store.appendonlydao.events.BufferedTransactionsReader.Transaction
import com.daml.platform.store.appendonlydao.{TransactionsBuffer, events}
import com.daml.platform.store.cache.MutableCacheBackedContractStore.EventSequentialId
import com.daml.platform.store.dao.LedgerDaoTransactionsReader
import com.daml.platform.store.dao.events.ContractStateEvent
import com.google.protobuf.timestamp.Timestamp

import scala.concurrent.Future

// TDT Handle verbose lf decode
class BufferedTransactionsReader(
    protected val delegate: TransactionsReader,
    val bufferedTransactions: TransactionsBuffer,
    metrics: Metrics,
) extends LedgerDaoTransactionsReader
    with DelegateTransactionsReader {

  private val logger = ContextualizedLogger.get(getClass)

  private val outputStreamBufferSize = 128

  override def getFlatTransactions(
      startExclusive: Offset,
      endInclusive: Offset,
      filter: FilterRelation,
      verbose: Boolean,
  )(implicit loggingContext: LoggingContext): Source[(Offset, GetTransactionsResponse), NotUsed] =
    getTransactions(startExclusive, endInclusive, filter, verbose)(
      toApiTx = toFlatTx,
      toApiResponse = (tx: FlatTx) => GetTransactionsResponse(Seq(tx)),
      fetchTransactions = delegate.getFlatTransactions(_, _, _, _)(loggingContext),
      sourceTimer = metrics.daml.index.getFlatTransactionsSource,
      resolvedFromBufferCounter = metrics.daml.index.flatTransactionEventsResolvedFromBuffer,
      totalRetrievedCounter = metrics.daml.index.totalFlatTransactionsRetrieved,
      bufferSizeCounter = metrics.daml.index.flatTransactionsBufferSize,
    )

  override def getTransactionTrees(
      startExclusive: Offset,
      endInclusive: Offset,
      requestingParties: Set[Party],
      verbose: Boolean,
  )(implicit
      loggingContext: LoggingContext
  ): Source[(Offset, GetTransactionTreesResponse), NotUsed] =
    getTransactions(startExclusive, endInclusive, requestingParties, verbose)(
      toApiTx = toTxTree,
      toApiResponse = (tx: TransactionTree) => GetTransactionTreesResponse(Seq(tx)),
      fetchTransactions = delegate.getTransactionTrees(_, _, _, _)(loggingContext),
      sourceTimer = metrics.daml.index.getTransactionTreesSource,
      resolvedFromBufferCounter = metrics.daml.index.transactionTreeEventsResolvedFromBuffer,
      totalRetrievedCounter = metrics.daml.index.totalTransactionTreesRetrieved,
      bufferSizeCounter = metrics.daml.index.transactionTreesBufferSize,
    )

  private def getTransactions[FILTER, API_TX, API_RESPONSE](
      startExclusive: Offset,
      endInclusive: Offset,
      filter: FILTER,
      verbose: Boolean,
  )(
      toApiTx: (Transaction, FILTER, Boolean) => Option[API_TX],
      toApiResponse: API_TX => API_RESPONSE,
      fetchTransactions: (
          Offset,
          Offset,
          FILTER,
          Boolean,
      ) => Source[(Offset, API_RESPONSE), NotUsed],
      sourceTimer: Timer,
      resolvedFromBufferCounter: Counter,
      totalRetrievedCounter: Counter,
      bufferSizeCounter: Counter,
  )(implicit loggingContext: LoggingContext): Source[(Offset, API_RESPONSE), NotUsed] = {
    val transactionsSource = Timed.source(
      sourceTimer, {
        bufferedTransactions.getTransactions(startExclusive, endInclusive) match {
          case ((bufferedStartExclusive, bufferedEndInclusive), bufferedSource) =>
            if (bufferedStartExclusive > startExclusive) {
              val bufferedTxs = bufferedSource
                .map { case (offset, tx) =>
                  offset -> toApiTx(tx, filter, verbose)
                }
                .collect { case (offset, Some(tx)) =>
                  resolvedFromBufferCounter.inc()
                  offset -> toApiResponse(tx)
                }

              logger.info(
                s"Buffered $startExclusive -> $bufferedStartExclusive -> $bufferedEndInclusive -> $endInclusive"
              )

              val transactionsAfterBuffer =
                if (bufferedEndInclusive < endInclusive)
                  fetchTransactions(bufferedEndInclusive, endInclusive, filter, verbose)
                else Source.empty

              fetchTransactions(bufferedEndInclusive, endInclusive, filter, verbose)
                .concat(bufferedTxs)
                .concat(transactionsAfterBuffer)
                .map(tx => {
                  totalRetrievedCounter.inc()
                  tx
                })
            } else {
              val transactionsAfterBuffer =
                if (bufferedEndInclusive < endInclusive)
                  fetchTransactions(bufferedEndInclusive, endInclusive, filter, verbose)
                else Source.empty

              bufferedSource
                .map { case (offset, tx) =>
                  offset -> toApiTx(tx, filter, verbose)
                }
                .collect { case (offset, Some(tx)) =>
                  resolvedFromBufferCounter.inc()
                  offset -> toApiResponse(tx)
                }
                .concat(transactionsAfterBuffer)
                .map(tx => {
                  totalRetrievedCounter.inc()
                  tx
                })
            }
        }
      },
    )

    instrumentedBufferedSource(
      original = transactionsSource,
      counter = bufferSizeCounter,
      size = outputStreamBufferSize,
    )
  }

  // TDT return only witnesses from within the requestors
  private def flatTxPredicate(
      event: BufferedTransactionsReader.Event,
      filter: FilterRelation,
  ): Boolean =
    if (filter.size == 1) {
      val (party, templateIds) = filter.iterator.next()
      if (templateIds.isEmpty)
        event.flatEventWitnesses.contains(party)
      else
        // Single-party request, restricted to a set of template identifiers
        event.flatEventWitnesses.contains(party) && templateIds.contains(event.templateId)
    } else {
      // Multi-party requests
      // If no party requests specific template identifiers
      val parties = filter.keySet
      if (filter.forall(_._2.isEmpty))
        event.flatEventWitnesses.intersect(parties.map(_.toString)).nonEmpty
      else {
        // If all parties request the same template identifier
        val templateIds = filter.valuesIterator.flatten.toSet
        if (filter.valuesIterator.forall(_ == templateIds)) {
          event.flatEventWitnesses.intersect(parties.map(_.toString)).nonEmpty &&
          templateIds.contains(event.templateId)
        } else {
          // If there are different template identifier but there are no wildcard parties
          val partiesAndTemplateIds = Relation.flatten(filter).toSet
          val wildcardParties = filter.filter(_._2.isEmpty).keySet
          if (wildcardParties.isEmpty) {
            partiesAndTemplateIds.exists { case (party, identifier) =>
              event.flatEventWitnesses.contains(party) && identifier == event.templateId
            }
          } else {
            // If there are wildcard parties and different template identifiers
            partiesAndTemplateIds.exists { case (party, identifier) =>
              event.flatEventWitnesses.contains(party) && identifier == event.templateId
            } || event.flatEventWitnesses.intersect(wildcardParties.map(_.toString)).nonEmpty
          }
        }
      }
    }

  private def permanent(events: Seq[BufferedTransactionsReader.Event]): Set[ContractId] =
    events.foldLeft(Set.empty[ContractId]) {
      case (contractIds, event: BufferedTransactionsReader.CreatedEvent) =>
        contractIds + event.contractId
      case (contractIds, event) if !contractIds.contains(event.contractId) =>
        contractIds + event.contractId
      case (contractIds, event) => contractIds - event.contractId
    }

  private def toFlatEvent(
      event: BufferedTransactionsReader.Event,
      verbose: Boolean,
  ): Option[com.daml.ledger.api.v1.event.Event] =
    event match {
      case createdEvent: BufferedTransactionsReader.CreatedEvent =>
        Some(
          com.daml.ledger.api.v1.event.Event(
            event = com.daml.ledger.api.v1.event.Event.Event.Created(
              value = com.daml.ledger.api.v1.event.CreatedEvent(
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
        )
      case exercisedEvent: BufferedTransactionsReader.ExercisedEvent if exercisedEvent.consuming =>
        Some(
          com.daml.ledger.api.v1.event.Event(
            event = com.daml.ledger.api.v1.event.Event.Event.Archived(
              value = com.daml.ledger.api.v1.event.ArchivedEvent(
                eventId = exercisedEvent.eventId.toLedgerString,
                contractId = exercisedEvent.contractId.coid,
                templateId = Some(LfEngineToApi.toApiIdentifier(exercisedEvent.templateId)),
                witnessParties = exercisedEvent.flatEventWitnesses.toSeq,
              )
            )
          )
        )
      case _ => None
    }

  private def toFlatTx(
      tx: Transaction,
      filter: FilterRelation,
      verbose: Boolean,
  ): Option[FlatTx] = {
    val aux = tx.events
      .filter(flatTxPredicate(_, filter))
    val nonTransientIds = permanent(aux)
    val events = aux
      .filter(ev => nonTransientIds(ev.contractId))

    events.headOption.flatMap { first =>
      if (first.commandId.nonEmpty || events.nonEmpty) {
        val flatEvents = events.map(toFlatEvent(_, verbose)).collect { case Some(ev) =>
          ev
        }
        if (flatEvents.isEmpty)
          None
        else
          Some(
            FlatTx(
              transactionId = first.transactionId,
              commandId = first.commandId.getOrElse(""),
              workflowId = first.workflowId.getOrElse(""),
              effectiveAt = Some(instantToTimestamp(first.ledgerEffectiveTime)),
              events = flatEvents,
              offset = ApiOffset.toApiString(tx.offset),
              traceContext = None,
            )
          )
      } else None
    }
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
    def flatEventWitnesses: Set[String]
    def templateId: Identifier
    def contractId: ContractId
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
