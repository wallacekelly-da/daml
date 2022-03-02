// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.ledger.sandbox.bridge.validate

import com.daml.ledger.api.domain.LedgerOffset
import com.daml.ledger.api.domain.LedgerOffset._
import com.daml.ledger.offset.Offset
import com.daml.ledger.participant.state.index.v2.IndexService
import com.daml.ledger.sandbox.bridge.BridgeMetrics
import com.daml.ledger.sandbox.bridge.validate.ConflictCheckingLedgerBridge._
import com.daml.metrics.Timed
import com.daml.platform.ApiOffset
import com.daml.platform.ApiOffset.toApiString

import java.util.concurrent.atomic.AtomicReference
import scala.concurrent.ExecutionContext

/** Tags the prepared submission with the current ledger end as available on the Ledger API. */
private[validate] class TagWithLedgerEndImpl(
    indexService: IndexService,
    bridgeMetrics: BridgeMetrics,
)(implicit executionContext: ExecutionContext)
    extends TagWithLedgerEnd {
  private val ledgerEnd =
    new AtomicReference[LedgerOffset.Absolute](LedgerOffset.Absolute(toApiString(ApiOffset.begin)))

  override def apply(
      preparedSubmission: Validation[PreparedSubmission]
  ): Validation[(Offset, PreparedSubmission)] = preparedSubmission match {
    case Left(rejection) => Left(rejection)
    case Right(preparedSubmission) =>
      val ledgerEnd = getLedgerEnd(preparedSubmission)
      Right(ApiOffset.assertFromString(ledgerEnd.value) -> preparedSubmission)
  }

  private def getLedgerEnd(preparedSubmission: PreparedSubmission) = {
    Timed
      .future(
        bridgeMetrics.Stages.TagWithLedgerEnd.timer,
        indexService
          .currentLedgerEnd()(preparedSubmission.submission.loggingContext),
      )
      .foreach { newLedgerEnd =>
        val _ = ledgerEnd.updateAndGet { actual =>
          if (Ordering[LedgerOffset.Absolute].compare(newLedgerEnd, actual) > 0) newLedgerEnd
          else actual
        }
      }

    ledgerEnd.get()
  }
}
