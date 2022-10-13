// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.metrics

import java.util.concurrent.TimeUnit

import com.codahale.{metrics => codahale}
import com.daml.metrics.MetricHandle.Timer.TimerStop

import scala.concurrent.{ExecutionContext, Future}

sealed trait MetricHandle {
  def name: String
  def metricType: String // type string used for documentation purposes
}

object MetricHandle {

  trait Factory {

    def prefix: MetricName

    def timer(name: MetricName): Timer

    def gauge[T](name: MetricName, initial: T): Gauge[T]

    def gaugeWithSupplier[T](
        name: MetricName,
        gaugeSupplier: () => () => T,
    ): Unit

    def meter(name: MetricName): Meter

    def counter(name: MetricName): Counter

    def histogram(name: MetricName): Histogram

  }
  trait DropwizardFactory extends Factory {

    def prefix: MetricName

    def registry: codahale.MetricRegistry

    def timer(name: MetricName): Timer = DropwizardTimer(name, registry.timer(name))

    def gauge[T](name: MetricName, initial: T): Gauge[T] =
      synchronized {
        registry.remove(name)
        val registeredGauge = registry.register(name, Gauges.VarGauge(initial))
        DropwizardGauge(name, registeredGauge)
      }

    def gaugeWithSupplier[T](
        name: MetricName,
        gaugeSupplier: () => () => T,
    ): Unit =
      synchronized {
        registry.remove(name)
        val _ = registry.gauge(
          name,
          () => {
            val valueGetter = gaugeSupplier()
            new codahale.Gauge[T] { override def getValue: T = valueGetter() }
          },
        )
        ()
      }

    def meter(name: MetricName): Meter = {
      // This is idempotent
      DropwizardMeter(name, registry.meter(name))
    }

    def counter(name: MetricName): Counter = {
      // This is idempotent
      DropwizardCounter(name, registry.counter(name))
    }

    def histogram(name: MetricName): Histogram = {
      DropwizardHistogram(name, registry.histogram(name))
    }

  }

  trait FactoryWithDBMetrics extends MetricHandle.DropwizardFactory {
    def createDbMetrics(name: String): DatabaseMetrics =
      new DatabaseMetrics(prefix, name, registry)
  }

  sealed trait Timer extends MetricHandle {

    def metricType: String = "Timer"

    def update(duration: Long, unit: TimeUnit): Unit

    def time[T](call: => T): T

    def startAsync(): TimerStop

    def timeFuture[T](call: => Future[T]): Future[T] = {
      val stop = startAsync()
      val result = call
      result.onComplete(_ => stop())(ExecutionContext.parasitic)
      result
    }
  }

  object Timer {
    type TimerStop = () => Unit
  }

  sealed case class DropwizardTimer(name: String, metric: codahale.Timer) extends Timer {

    def update(duration: Long, unit: TimeUnit): Unit = metric.update(duration, unit)
    override def time[T](call: => T): T = metric.time(() => call)
    override def startAsync(): TimerStop = {
      val ctx = metric.time()
      () => {
        ctx.stop()
        ()
      }
    }
  }
  sealed trait Gauge[T] extends MetricHandle {
    def metricType: String = "Gauge"

    def updateValue(newValue: T): Unit

    def getValue: T
  }

  sealed case class DropwizardGauge[T](name: String, metric: Gauges.VarGauge[T]) extends Gauge[T] {
    def updateValue(newValue: T): Unit = metric.updateValue(newValue)
    override def getValue: T = metric.getValue
  }

  sealed trait Meter extends MetricHandle {
    def metricType: String = "Meter"

    def mark(): Unit = mark(1)
    def mark(value: Long): Unit

  }
  sealed case class DropwizardMeter(name: String, metric: codahale.Meter) extends Meter {

    def mark(value: Long): Unit = metric.mark(value)

  }

  sealed trait Counter extends MetricHandle {

    override def metricType: String = "Counter"
    def inc(): Unit
    def inc(n: Long): Unit
    def dec(): Unit
    def dec(n: Long): Unit
    def getCount: Long
  }
  sealed case class DropwizardCounter(name: String, metric: codahale.Counter) extends Counter {

    override def inc(): Unit = metric.inc
    override def inc(n: Long): Unit = metric.inc(n)
    override def dec(): Unit = metric.dec
    override def dec(n: Long): Unit = metric.dec(n)

    override def getCount: Long = metric.getCount
  }

  sealed trait Histogram extends MetricHandle {

    def metricType: String = "Histogram"
    def update(value: Long): Unit
    def update(value: Int): Unit

  }

  sealed case class DropwizardHistogram(name: String, metric: codahale.Histogram)
      extends MetricHandle
      with Histogram {
    override def update(value: Long): Unit = metric.update(value)
    override def update(value: Int): Unit = metric.update(value)
  }

}
