// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.metrics

import com.daml.metrics.MetricHandle.TimerM

import com.codahale.metrics.MetricRegistry.MetricSupplier
import com.codahale.metrics.{Counter, Gauge, MetricRegistry, Timer}

final class CacheMetrics(
    registry: MetricRegistry,
    prefix: MetricName,
) {
  val hitCount: Counter = registry.counter(prefix :+ "hits")
  val missCount: Counter = registry.counter(prefix :+ "misses")
  val loadSuccessCount: Counter = registry.counter(prefix :+ "load_successes")
  val loadFailureCount: Counter = registry.counter(prefix :+ "load_failures")
  val totalLoadTime: Timer = registry.timer(prefix :+ "load_total_time")
  val evictionCount: Counter = registry.counter(prefix :+ "evictions")
  val evictionWeight: Counter = registry.counter(prefix :+ "evicted_weight")

  @MetricDoc.Tag(
    summary = "Timer from CacheMetrics.scala",
    description = """Description for timer from CacheMetrics.scala.""",
  )
  val totalLoadTimeTagged: TimerM =
    TimerM(prefix :+ "load_total_time_tagged", registry.timer(prefix :+ "load_total_time_tagged"))

  def registerSizeGauge(sizeGauge: Gauge[Long]): Unit =
    register(prefix :+ "size", () => sizeGauge)
  def registerWeightGauge(weightGauge: Gauge[Long]): Unit =
    register(prefix :+ "weight", () => weightGauge)

  private def register(name: MetricName, gaugeSupplier: MetricSupplier[Gauge[_]]): Unit =
    registerGauge(name, gaugeSupplier, registry)
}
