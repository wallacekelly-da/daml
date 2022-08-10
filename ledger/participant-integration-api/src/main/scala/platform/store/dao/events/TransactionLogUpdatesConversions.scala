// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.platform.store.dao.events

import com.daml.api.util.TimestampConversion.fromInstant
import com.daml.ledger.api.v1.transaction.{
  TransactionTree,
  TreeEvent,
  Transaction => FlatTransaction,
}
import com.daml.ledger.api.v1.transaction_service.{
  GetFlatTransactionResponse,
  GetTransactionResponse,
  GetTransactionTreesResponse,
  GetTransactionsResponse,
}
import com.daml.ledger.api.v1.{event => apiEvent}
import com.daml.ledger.offset.Offset
import com.daml.ledger.participant.state.v2.Update
import com.daml.lf.data.Ref
import com.daml.lf.data.Ref.{Identifier, LedgerString, Party}
import com.daml.lf.ledger.EventId
import com.daml.lf.transaction.{Node, NodeId}
import com.daml.lf.transaction.Transaction.ChildrenRecursion
import com.daml.lf.value.Value.ContractId
import com.daml.logging.LoggingContext
import com.daml.platform.api.v1.event.EventOps.TreeEventOps
import com.daml.platform.participant.util.LfEngineToApi
import com.daml.platform.{ApiOffset, FilterRelation, Value}
import com.google.protobuf.timestamp.Timestamp

import scala.concurrent.{ExecutionContext, Future}

private[events] object TransactionLogUpdatesConversions {
  object ToFlatTransaction {
    def filter(
        // LLP TODO: Why wildcardParties AND requestingParties ???
        wildcardParties: Set[Party],
        templateSpecificParties: Map[Identifier, Set[Party]],
        requestingParties: Set[Party],
    ): (Offset, Update) => Option[
      (Offset, Update.TransactionAccepted)
    ] = {
      case (offset, transaction: Update.TransactionAccepted) =>
        val filteredFlatEvents =
          flatTransactionNodes(transaction)
            .filter(flatTransactionPredicate(wildcardParties, templateSpecificParties))

        val commandId = getCommandId(transaction, requestingParties)
        val nonTransient = removeTransient(filteredFlatEvents)
        // Allows emitting flat transactions with no events, a use-case needed
        // for the functioning of Daml triggers.
        // (more details in https://github.com/digital-asset/daml/issues/6975)
        Option.when(nonTransient.nonEmpty || commandId.nonEmpty)(offset -> transaction)
      case _ => None
    }

    def toGetTransactionsResponse(
        filter: FilterRelation,
        verbose: Boolean,
        lfValueTranslation: LfValueTranslation,
    )(implicit
        loggingContext: LoggingContext,
        executionContext: ExecutionContext,
    ): (Offset, Update.TransactionAccepted) => Future[GetTransactionsResponse] =
      (offset, transaction) =>
        toFlatTransaction(offset, transaction, filter, verbose, lfValueTranslation)
          .map(transaction => GetTransactionsResponse(Seq(transaction)))

    def toGetFlatTransactionResponse(
        requestingParties: Set[Party],
        lfValueTranslation: LfValueTranslation,
    )(implicit
        loggingContext: LoggingContext,
        executionContext: ExecutionContext,
    ): (Offset, Update.TransactionAccepted) => Future[Option[GetFlatTransactionResponse]] =
      (offset, transaction) =>
        filter(requestingParties, Map.empty, requestingParties)(offset, transaction)
          .map { case (offset, transactionAccepted) =>
            toFlatTransaction(
              offset = offset,
              transactionAccepted = transactionAccepted,
              filter = requestingParties.map(_ -> Set.empty[Ref.Identifier]).toMap,
              verbose = true,
              lfValueTranslation = lfValueTranslation,
            )
          }
          .map(_.map(flatTransaction => Some(GetFlatTransactionResponse(Some(flatTransaction)))))
          .getOrElse(Future.successful(None))

    private def toFlatTransaction(
        offset: Offset,
        transactionAccepted: Update.TransactionAccepted,
        filter: FilterRelation,
        verbose: Boolean,
        lfValueTranslation: LfValueTranslation,
    )(implicit
        loggingContext: LoggingContext,
        executionContext: ExecutionContext,
    ): Future[FlatTransaction] =
      Future.delegate {
        val requestingParties = filter.keySet
        Future
          .traverse(flatTransactionNodes(transactionAccepted))(event =>
            toFlatEvent(event, requestingParties, verbose, lfValueTranslation)
          )
          .map(flatEvents =>
            FlatTransaction(
              transactionId = transactionAccepted.transactionId,
              commandId = getCommandId(transactionAccepted, requestingParties),
              workflowId = transactionAccepted.transactionMeta.workflowId.getOrElse(""),
              effectiveAt =
                // TODO LLP: Check
                Some(timestampToTimestamp(transactionAccepted.transactionMeta.ledgerEffectiveTime)),
              events = flatEvents,
              offset = ApiOffset.toApiString(offset),
            )
          )
      }

    private def removeTransient(aux: Vector[Node.Action]): Vector[Node.Action] = {
      val permanent = aux.foldLeft(Set.empty[ContractId]) {
        case (contractIds, event: Node.Create) if !contractIds(event.coid) =>
          contractIds + event.coid
        case (contractIds, event: Node.Exercise) if event.consuming =>
          contractIds - event.targetCoid
        case (contractIds, _) =>
          val prettyCids = contractIds.iterator.map(_.coid).mkString(", ")
          throw new RuntimeException(s"Unexpected non-consuming event for contractIds $prettyCids")
      }

      aux.filter {
        case ev: Node.Create => permanent(ev.coid)
        case ev: Node.Exercise => permanent(ev.targetCoid)
        case otherNode => throw new RuntimeException(s"Unsupported node $otherNode")
      }
    }

    private def flatTransactionPredicate(
        wildcardParties: Set[Party],
        templateSpecificParties: Map[Identifier, Set[Party]],
    )(event: Node.Action) = {
      val stakeholders = event match {
        case create: Node.Create => create.stakeholders
        case exercise: Node.Exercise if exercise.consuming => exercise.stakeholders
        case other =>
          throw new IllegalArgumentException(s"Unexpected node type for flatTransaction $other")
      }

      val stakeholdersMatchingParties =
        stakeholders.exists(wildcardParties)

      stakeholdersMatchingParties || templateSpecificParties
        .get(event.templateId)
        .exists(_.exists(stakeholders))
    }

    private def toFlatEvent(
        transactionId: LedgerString,
        event: Node.Action,
        nodeId: NodeId,
        requestingParties: Set[Party],
        verbose: Boolean,
        lfValueTranslation: LfValueTranslation,
    )(implicit
        loggingContext: LoggingContext,
        executionContext: ExecutionContext,
    ): Future[apiEvent.Event] =
      event match {
        case createdEvent: Node.Create =>
          createdToApiCreatedEvent(
            transactionId,
            requestingParties,
            verbose,
            lfValueTranslation,
            nodeId,
            createdEvent,
            _.stakeholders,
          ).map(apiCreatedEvent => apiEvent.Event(apiEvent.Event.Event.Created(apiCreatedEvent)))

        case exercisedEvent: Node.Exercise if exercisedEvent.consuming =>
          Future.successful(
            exercisedToFlatEvent(requestingParties, transactionId, exercisedEvent, nodeId)
          )

        case _ => Future.failed(new RuntimeException("Not a flat transaction event"))
      }

    private def exercisedToFlatEvent(
        requestingParties: Set[Party],
        transactionId: LedgerString,
        exercisedEvent: Node.Exercise,
        nodeId: NodeId,
    ): apiEvent.Event =
      apiEvent.Event(
        apiEvent.Event.Event.Archived(
          apiEvent.ArchivedEvent(
            eventId = EventId(transactionId, nodeId).toLedgerString,
            contractId = exercisedEvent.targetCoid.coid,
            templateId = Some(LfEngineToApi.toApiIdentifier(exercisedEvent.templateId)),
            witnessParties = requestingParties.iterator.filter(exercisedEvent.stakeholders).toSeq,
          )
        )
      )
  }

  object ToTransactionTree {
    def filter(
        requestingParties: Set[Party]
    ): (Offset, Update) => Option[(Offset, Update.TransactionAccepted)] = {
      case (offset, transaction: Update.TransactionAccepted) =>
        val filteredForVisibility =
          transaction.events.filter(transactionTreePredicate(requestingParties))

        Option.when(filteredForVisibility.nonEmpty)(
          offset -> transaction.copy(events = filteredForVisibility)
        )
      case _ => None
    }

    def toGetTransactionResponse(
        update: Update,
        requestingParties: Set[Party],
        lfValueTranslation: LfValueTranslation,
    )(implicit
        loggingContext: LoggingContext,
        executionContext: ExecutionContext,
    ): Future[Option[GetTransactionResponse]] =
      filter(requestingParties)(transactionLogUpdate)
        .map(tx =>
          toTransactionTree(
            transactionAccepted = tx,
            requestingParties,
            verbose = true,
            lfValueTranslation = lfValueTranslation,
          )
        )
        .map(_.map(transactionTree => Some(GetTransactionResponse(Some(transactionTree)))))
        .getOrElse(Future.successful(None))

    def toGetTransactionTreesResponse(
        requestingParties: Set[Party],
        verbose: Boolean,
        lfValueTranslation: LfValueTranslation,
    )(implicit
        loggingContext: LoggingContext,
        executionContext: ExecutionContext,
    ): Update.TransactionAccepted => Future[GetTransactionTreesResponse] =
      toTransactionTree(_, requestingParties, verbose, lfValueTranslation)
        .map(txTree => GetTransactionTreesResponse(Seq(txTree)))

    private def toTransactionTree(
        transactionAccepted: Update.TransactionAccepted,
        requestingParties: Set[Party],
        verbose: Boolean,
        lfValueTranslation: LfValueTranslation,
    )(implicit
        loggingContext: LoggingContext,
        executionContext: ExecutionContext,
    ): Future[TransactionTree] =
      Future.delegate {
        Future
          .traverse(transactionAccepted.events)(event =>
            toTransactionTreeEvent(requestingParties, verbose, lfValueTranslation)(event)
          )
          .map { treeEvents =>
            val visible = treeEvents.map(_.eventId)
            val visibleOrder = visible.view.zipWithIndex.toMap
            val eventsById = treeEvents.iterator
              .map(e =>
                e.eventId -> e
                  .filterChildEventIds(visibleOrder.contains)
                  // childEventIds need to be returned in the event order in the original transaction.
                  // Unfortunately, we did not store them ordered in the past so we have to sort it to recover this order.
                  // The order is determined by the order of the events, which follows the event order of the original transaction.
                  .sortChildEventIdsBy(visibleOrder)
              )
              .toMap

            // All event identifiers that appear as a child of another item in this response
            val children = eventsById.valuesIterator.flatMap(_.childEventIds).toSet

            // The roots for this request are all visible items
            // that are not a child of some other visible item
            val rootEventIds = visible.filterNot(children)

            TransactionTree(
              transactionId = transactionAccepted.transactionId,
              commandId = getCommandId(transactionAccepted.events, requestingParties),
              workflowId = transactionAccepted.workflowId,
              effectiveAt = Some(timestampToTimestamp(transactionAccepted.effectiveAt)),
              offset = ApiOffset.toApiString(transactionAccepted.offset),
              eventsById = eventsById,
              rootEventIds = rootEventIds,
            )
          }
      }

    private def toTransactionTreeEvent(
        requestingParties: Set[Party],
        verbose: Boolean,
        lfValueTranslation: LfValueTranslation,
    )(event: Node.Action)(implicit
        loggingContext: LoggingContext,
        executionContext: ExecutionContext,
    ): Future[TreeEvent] =
      event match {
        case createdEvent: Node.Create =>
          createdToApiCreatedEvent(
            requestingParties,
            verbose,
            lfValueTranslation,
            createdEvent,
            _.treeEventWitnesses,
          ).map(apiCreatedEvent => TreeEvent(TreeEvent.Kind.Created(apiCreatedEvent)))

        case exercisedEvent: Node.Exercise =>
          exercisedToTransactionTreeEvent(
            requestingParties,
            verbose,
            lfValueTranslation,
            exercisedEvent,
          )
      }

    private def exercisedToTransactionTreeEvent(
        requestingParties: Set[Party],
        verbose: Boolean,
        lfValueTranslation: LfValueTranslation,
        exercisedEvent: Node.Exercise,
    )(implicit
        loggingContext: LoggingContext,
        executionContext: ExecutionContext,
    ) = {
      val choiceArgumentEnricher = (value: Value) =>
        lfValueTranslation.enricher
          .enrichChoiceArgument(
            exercisedEvent.templateId,
            exercisedEvent.interfaceId,
            exercisedEvent.choiceId,
            value.unversioned,
          )

      val eventualChoiceArgument = lfValueTranslation.toApiValue(
        exercisedEvent.versionedChosenValue,
        verbose,
        "exercise argument",
        choiceArgumentEnricher,
      )

      val eventualExerciseResult = exercisedEvent.versionedExerciseResult
        .map { exerciseResult =>
          val choiceResultEnricher = (value: Value) =>
            lfValueTranslation.enricher.enrichChoiceResult(
              exercisedEvent.templateId,
              exercisedEvent.interfaceId,
              exercisedEvent.choiceId,
              value.unversioned,
            )

          lfValueTranslation
            .toApiValue(
              value = exerciseResult,
              verbose = verbose,
              attribute = "exercise result",
              enrich = choiceResultEnricher,
            )
            .map(Some(_))
        }
        .getOrElse(Future.successful(None))

      for {
        choiceArgument <- eventualChoiceArgument
        maybeExerciseResult <- eventualExerciseResult
      } yield TreeEvent(
        TreeEvent.Kind.Exercised(
          apiEvent.ExercisedEvent(
            eventId = exercisedEvent.eventId.toLedgerString,
            contractId = exercisedEvent.targetCoid,
            templateId = Some(LfEngineToApi.toApiIdentifier(exercisedEvent.templateId)),
            interfaceId = exercisedEvent.interfaceId.map(LfEngineToApi.toApiIdentifier),
            choice = exercisedEvent.choiceId,
            choiceArgument = Some(choiceArgument),
            actingParties = exercisedEvent.actingParties.toSeq,
            consuming = exercisedEvent.consuming,
            witnessParties = requestingParties.view.filter(exercisedEvent.informeesOfNode).toSeq,
            childEventIds = exercisedEvent.children,
            exerciseResult = maybeExerciseResult,
          )
        )
      )
    }

    private def transactionTreePredicate(
        requestingParties: Set[Party]
    ): Node.Action => Boolean = {
      case create: Node.Create => requestingParties.exists(create.informeesOfNode)
      case exercised: Node.Exercise => requestingParties.exists(exercised.informeesOfNode)
      case _ => false
    }
  }

  private def createdToApiCreatedEvent(
      transactionId: LedgerString,
      requestingParties: Set[Party],
      verbose: Boolean,
      lfValueTranslation: LfValueTranslation,
      nodeId: NodeId,
      createdEvent: Node.Create,
      createdWitnesses: Node.Create => Set[Party],
  )(implicit
      loggingContext: LoggingContext,
      executionContext: ExecutionContext,
  ): Future[apiEvent.CreatedEvent] = {
    val eventualContractKey = createdEvent.versionedKey
      .map { contractKey =>
        val contractKeyEnricher = (value: Value) =>
          lfValueTranslation.enricher.enrichContractKey(
            createdEvent.templateId,
            value.unversioned,
          )

        lfValueTranslation
          .toApiValue(
            value = contractKey.map(_.key), // TODO LLP ???
            verbose = verbose,
            attribute = "create key",
            enrich = contractKeyEnricher,
          )
          .map(Some(_))
      }
      .getOrElse(Future.successful(None))

    val contractEnricher = (value: Value) =>
      lfValueTranslation.enricher.enrichContract(createdEvent.templateId, value.unversioned)

    val eventualCreateArguments = lfValueTranslation.toApiRecord(
      value = createdEvent.versionedArg,
      verbose = verbose,
      attribute = "create argument",
      enrich = contractEnricher,
    )

    for {
      maybeContractKey <- eventualContractKey
      createArguments <- eventualCreateArguments
    } yield apiEvent.CreatedEvent(
      eventId = EventId(transactionId, nodeId).toLedgerString,
      contractId = createdEvent.coid.coid,
      templateId = Some(LfEngineToApi.toApiIdentifier(createdEvent.templateId)),
      contractKey = maybeContractKey,
      createArguments = Some(createArguments),
      witnessParties = requestingParties.view.filter(createdWitnesses(createdEvent)).toSeq,
      signatories = createdEvent.signatories.toSeq,
      observers = (createdEvent.stakeholders -- createdEvent.signatories).toSeq, // TODO LLP: ???
      agreementText = Some(createdEvent.agreementText), // TODO LLP: Some("")?,
    )
  }

  private def timestampToTimestamp(t: com.daml.lf.data.Time.Timestamp): Timestamp =
    fromInstant(t.toInstant)

  private def getCommandId(
      transaction: Update.TransactionAccepted,
      requestingParties: Set[Party],
  ): String =
    transaction.optCompletionInfo
      .collect {
        case completionInfo if completionInfo.actAs.exists(requestingParties) =>
          completionInfo.commandId
      }
      .getOrElse("")

  private def flatTransactionNodes(in: Update.TransactionAccepted): Vector[Node.Action] =
    in.transaction
      .foldInExecutionOrder(Vector.empty[Node])(
        exerciseBegin = (acc, _, node) => (acc :+ node, ChildrenRecursion.DoRecurse),
        // Rollback nodes are not included in the indexer
        rollbackBegin = (acc, _, _) => (acc, ChildrenRecursion.DoNotRecurse),
        leaf = (acc, _, node) => acc :+ node,
        exerciseEnd = (acc, _, _) => acc,
        rollbackEnd = (acc, _, _) => acc,
      )
      .collect {
        case createdEvent: Node.Create => createdEvent
        case exercisedEvent: Node.Exercise if exercisedEvent.consuming => exercisedEvent
      }
}
