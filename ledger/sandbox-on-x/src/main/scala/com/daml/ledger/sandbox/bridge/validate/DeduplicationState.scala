// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.ledger.sandbox.bridge.validate

import com.daml.ledger.participant.state.v2.ChangeId
import com.daml.ledger.sandbox.bridge.BridgeMetrics
import com.daml.ledger.sandbox.bridge.validate.DeduplicationState.DeduplicationQueue
import com.daml.lf.data.Time
import com.daml.metrics.Timed

import java.time.Duration
import scala.collection.immutable.VectorMap

case class DeduplicationState private (
    private[validate] val deduplicationQueue: DeduplicationQueue,
    private val maxDeduplicationDuration: Duration,
    private val bridgeMetrics: BridgeMetrics,
) {
  private val deduplicateTimer = bridgeMetrics.registry.timer("bridge_deduplicate_total")
  private val goThrough = bridgeMetrics.registry.timer("bridge_deduplicate_state_after_evictions")
  private val isDuplicateDuration = bridgeMetrics.registry.timer("bridge_deduplicate_is_duplicate")

  def deduplicate(
      changeId: ChangeId,
      commandDeduplicationDuration: Duration,
      recordTime: Time.Timestamp,
  ): (DeduplicationState, Boolean) =
    Timed.value(
      deduplicateTimer, {
        bridgeMetrics.SequencerState.deduplicationQueueLength.update(deduplicationQueue.size)
        if (commandDeduplicationDuration.compareTo(maxDeduplicationDuration) > 0)
          throw new RuntimeException(
            s"Cannot deduplicate for a period ($commandDeduplicationDuration) longer than the max deduplication duration ($maxDeduplicationDuration)."
          )
        else {
          val expiredTimestamp = expiredThreshold(maxDeduplicationDuration, recordTime)

          val queueAfterEvictions =
            Timed.value(goThrough, deduplicationQueue.dropWhile(_._2 <= expiredTimestamp))

          val isDuplicateChangeId = Timed.value(
            isDuplicateDuration,
            queueAfterEvictions
              .get(changeId)
              .exists(_ >= expiredThreshold(commandDeduplicationDuration, recordTime)),
          )

          if (isDuplicateChangeId)
            copy(deduplicationQueue = queueAfterEvictions) -> true
          else
            copy(deduplicationQueue = queueAfterEvictions.updated(changeId, recordTime)) -> false
        }
      },
    )

  private def expiredThreshold(
      deduplicationDuration: Duration,
      now: Time.Timestamp,
  ): Time.Timestamp =
    now.subtract(deduplicationDuration)
}

object DeduplicationState {
  private[sandbox] type DeduplicationQueue = VectorMap[ChangeId, Time.Timestamp]

  private[validate] def empty(
      deduplicationDuration: Duration,
      bridgeMetrics: BridgeMetrics,
  ): DeduplicationState =
    DeduplicationState(
      deduplicationQueue = VectorMap.empty,
      maxDeduplicationDuration = deduplicationDuration,
      bridgeMetrics = bridgeMetrics,
    )
}
