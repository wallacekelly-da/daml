// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.ledger.sandbox.bridge.validate

import akka.NotUsed
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.Flow
import com.daml.api.util.TimeProvider
import com.daml.error.{ContextualizedErrorLogger, DamlContextualizedErrorLogger}
import com.daml.ledger.configuration.Configuration
import com.daml.ledger.offset.Offset
import com.daml.ledger.participant.state.index.v2.IndexService
import com.daml.ledger.participant.state.v2.Update
import com.daml.ledger.sandbox.bridge.validate.ConflictCheckingLedgerBridge._
import com.daml.ledger.sandbox.bridge.{BridgeMetrics, LedgerBridge}
import com.daml.ledger.sandbox.domain._
import com.daml.lf.data.Ref
import com.daml.lf.transaction.{Transaction => LfTransaction}
import com.daml.logging.{ContextualizedLogger, LoggingContext}
import com.daml.platform.server.api.validation.ErrorFactories
import com.daml.platform.store.appendonlydao.events._

import java.time.Duration
import scala.concurrent.{ExecutionContext, Future}

private[validate] class ConflictCheckingLedgerBridge(
    bridgeMetrics: BridgeMetrics,
    prepareSubmission: PrepareSubmission,
    tagWithLedgerEnd: TagWithLedgerEnd,
    conflictCheckWithCommitted: ConflictCheckWithCommitted,
    sequence: Sequence,
    servicesThreadPoolSize: Int,
) extends LedgerBridge {
  def flow: Flow[Submission, (Offset, Update), NotUsed] =
    Flow[Submission]
      .buffered(128)(bridgeMetrics.Stages.PrepareSubmission)
      .mapAsyncUnordered(servicesThreadPoolSize)(prepareSubmission)
      .buffered(128)(bridgeMetrics.Stages.TagWithLedgerEnd)
      .map(tagWithLedgerEnd)
      .buffered(128)(bridgeMetrics.Stages.ConflictCheckWithCommitted)
      .mapAsync(servicesThreadPoolSize)(conflictCheckWithCommitted)
      .buffered(128)(bridgeMetrics.Stages.Sequence)
      .statefulMapConcat(sequence)

  private implicit class FlowWithBuffers[T, R](flow: Flow[T, R, NotUsed]) {
    def buffered(
        bufferLength: Int
    )(stageMetrics: bridgeMetrics.Stages.StageMetrics): Flow[T, R, NotUsed] =
      flow
        .map { in =>
          stageMetrics.bufferBefore.inc()
          in
        }
        .buffer(bufferLength, OverflowStrategy.backpressure)
        .map { in =>
          stageMetrics.bufferBefore.dec()
          in
        }
  }
}

private[bridge] object ConflictCheckingLedgerBridge {
  private[validate] type Validation[T] = Either[Rejection, T]
  private[validate] type AsyncValidation[T] = Future[Validation[T]]
  private[validate] type KeyInputs = Map[Key, LfTransaction.KeyInput]
  private[validate] type UpdatedKeys = Map[Key, Option[ContractId]]

  // Conflict checking stages
  private[validate] type PrepareSubmission = Submission => AsyncValidation[PreparedSubmission]
  private[validate] type TagWithLedgerEnd =
    Validation[PreparedSubmission] => Validation[(Offset, PreparedSubmission)]
  private[validate] type ConflictCheckWithCommitted =
    Validation[(Offset, PreparedSubmission)] => AsyncValidation[(Offset, PreparedSubmission)]
  private[validate] type Sequence =
    () => Validation[(Offset, PreparedSubmission)] => Iterable[(Offset, Update)]

  def apply(
      participantId: Ref.ParticipantId,
      indexService: IndexService,
      timeProvider: TimeProvider,
      initialLedgerEnd: Offset,
      initialAllocatedParties: Set[Ref.Party],
      initialLedgerConfiguration: Option[Configuration],
      bridgeMetrics: BridgeMetrics,
      errorFactories: ErrorFactories,
      validatePartyAllocation: Boolean,
      servicesThreadPoolSize: Int,
      maxDeduplicationDuration: Duration,
  )(implicit
      servicesExecutionContext: ExecutionContext
  ): ConflictCheckingLedgerBridge =
    new ConflictCheckingLedgerBridge(
      bridgeMetrics = bridgeMetrics,
      prepareSubmission = new PrepareSubmissionImpl(bridgeMetrics),
      tagWithLedgerEnd = new TagWithLedgerEndImpl(indexService, bridgeMetrics),
      conflictCheckWithCommitted =
        new ConflictCheckWithCommittedImpl(indexService, bridgeMetrics, errorFactories),
      sequence = new SequenceImpl(
        participantId = participantId,
        timeProvider = timeProvider,
        initialLedgerEnd = initialLedgerEnd,
        initialAllocatedParties = initialAllocatedParties,
        initialLedgerConfiguration = initialLedgerConfiguration,
        validatePartyAllocation = validatePartyAllocation,
        bridgeMetrics = bridgeMetrics,
        errorFactories = errorFactories,
        maxDeduplicationDuration = maxDeduplicationDuration,
      ),
      servicesThreadPoolSize = servicesThreadPoolSize,
    )

  private[validate] def withErrorLogger[T](submissionId: Option[String])(
      f: ContextualizedErrorLogger => T
  )(implicit loggingContext: LoggingContext, logger: ContextualizedLogger) =
    f(new DamlContextualizedErrorLogger(logger, loggingContext, submissionId))
}
