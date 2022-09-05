// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.metrics

import com.daml.metrics.MetricHandle.TimerM

import com.codahale.metrics.{MetricRegistry, Timer}

class DatabaseMetrics private[metrics] (
    registry: MetricRegistry,
    prefix: MetricName,
    val name: String,
) {
  protected val dbPrefix: MetricName = prefix :+ name

  val waitTimer: Timer = registry.timer(dbPrefix :+ "wait")
  val executionTimer: Timer = registry.timer(dbPrefix :+ "exec")
  val translationTimer: Timer = registry.timer(dbPrefix :+ "translation")
  val compressionTimer: Timer = registry.timer(dbPrefix :+ "compression")
  val commitTimer: Timer = registry.timer(dbPrefix :+ "commit")
  val queryTimer: Timer = registry.timer(dbPrefix :+ "query")
}

class DatabaseMetricsForDocs private[metrics] (
    prefix: MetricName,
    val name: String,
) {
  protected val dbPrefix: MetricName = prefix :+ name

  @MetricDoc.Tag(
    summary = "Wait timer from DatabaseMetrics",
    description = """Description for wait timer from DatabaseMetrics.""",
  )
  val waitTimerForDocs: TimerM = TimerM(dbPrefix :+ "wait", null)

  // val executionTimer: Timer = registry.timer(dbPrefix :+ "exec")
  // val translationTimer: Timer = registry.timer(dbPrefix :+ "translation")
  // val compressionTimer: Timer = registry.timer(dbPrefix :+ "compression")
  // val commitTimer: Timer = registry.timer(dbPrefix :+ "commit")
  // val queryTimer: Timer = registry.timer(dbPrefix :+ "query")
}

object DatabaseMetrics {

  def ForTesting(metricsName: String): DatabaseMetrics =
    new DatabaseMetrics(
      registry = new MetricRegistry(),
      prefix = MetricName("ForTesting"),
      name = metricsName,
    )
}
