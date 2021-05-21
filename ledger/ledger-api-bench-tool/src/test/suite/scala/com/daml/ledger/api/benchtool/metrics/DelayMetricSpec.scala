// Copyright (c) 2021 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.ledger.api.benchtool

import com.daml.ledger.api.benchtool.metrics.Metric.DelayMetric
import com.google.protobuf.timestamp.Timestamp
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.time.{Clock, Duration, Instant, ZoneId}
import scala.util.Random

import scala.language.existentials

class DelayMetricSpec extends AnyWordSpec with Matchers {
  DelayMetric.getClass.getSimpleName should {
    "correctly handle initial state" in {
      val metric: DelayMetric[String] = anEmptyDelayMetric(Clock.systemUTC())

      val (_, periodicValue) = metric.periodicValue()
      val finalValue = metric.finalValue(aPositiveDouble())

      periodicValue shouldBe DelayMetric.Value(None)
      finalValue shouldBe DelayMetric.Value(None)
    }

    "compute values after processing elements" in {
      val totalDurationSeconds: Double = aPositiveDouble()
      val elem1: String = aString()
      val elem2: String = aString()
      val testNow = Clock.systemUTC().instant()
      val recordTime1 = testNow.minusSeconds(aPositiveLong())
      val recordTime2 = testNow.minusSeconds(aPositiveLong())
      val recordTime3 = testNow.minusSeconds(aPositiveLong())
      val delay1 = secondsBetween(recordTime1, testNow)
      val delay2 = secondsBetween(recordTime2, testNow)
      val delay3 = secondsBetween(recordTime3, testNow)
      def testRecordTimeFunction: String => List[Timestamp] = recordTimeFunctionFromMap(
        Map(
          elem1 -> List(recordTime1, recordTime2),
          elem2 -> List(recordTime3),
        )
      )
      val clock = Clock.fixed(testNow, ZoneId.of("UTC"))
      val metric: DelayMetric[String] =
        DelayMetric.empty[String](
          recordTimeFunction = testRecordTimeFunction,
          objectives = List.empty,
          clock = clock,
        )

      val (newMetric, periodicValue) = metric
        .onNext(elem1)
        .onNext(elem2)
        .periodicValue()
      val finalValue = newMetric.finalValue(totalDurationSeconds)

      val expectedMean = (delay1 + delay2 + delay3) / 3
      periodicValue shouldBe DelayMetric.Value(Some(expectedMean))
      finalValue shouldBe DelayMetric.Value(None)
    }

    "correctly handle periods with no elements" in {
      val totalDurationSeconds: Double = aPositiveDouble()
      val elem1: String = aString()
      val elem2: String = aString()
      val testNow = Clock.systemUTC().instant()
      val recordTime1 = testNow.minusSeconds(aPositiveLong())
      val recordTime2 = testNow.minusSeconds(aPositiveLong())
      val recordTime3 = testNow.minusSeconds(aPositiveLong())
      def testRecordTimeFunction: String => List[Timestamp] = recordTimeFunctionFromMap(
        Map(
          elem1 -> List(recordTime1, recordTime2),
          elem2 -> List(recordTime3),
        )
      )
      val clock = Clock.fixed(testNow, ZoneId.of("UTC"))
      val metric: DelayMetric[String] =
        DelayMetric.empty[String](
          recordTimeFunction = testRecordTimeFunction,
          objectives = List.empty,
          clock = clock,
        )

      val (newMetric, periodicValue) = metric
        .onNext(elem1)
        .onNext(elem2)
        .periodicValue()
        ._1
        .periodicValue()
      val finalValue = newMetric.finalValue(totalDurationSeconds)

      periodicValue shouldBe DelayMetric.Value(None)
      finalValue shouldBe DelayMetric.Value(None)
    }

    "correctly handle multiple periods with elements" in {
      val totalDurationSeconds: Double = aPositiveDouble()
      val elem1: String = aString()
      val elem2: String = aString()
      val elem3: String = aString()
      val testNow = Clock.systemUTC().instant()
      val recordTime1 = testNow.minusSeconds(aPositiveLong())
      val recordTime2 = testNow.minusSeconds(aPositiveLong())
      val recordTime3 = testNow.minusSeconds(aPositiveLong())
      val recordTime4 = testNow.minusSeconds(aPositiveLong())
      val recordTime5 = testNow.minusSeconds(aPositiveLong())
      val delay4 = secondsBetween(recordTime4, testNow)
      val delay5 = secondsBetween(recordTime5, testNow)
      def testRecordTimeFunction: String => List[Timestamp] = recordTimeFunctionFromMap(
        Map(
          elem1 -> List(recordTime1, recordTime2),
          elem2 -> List(recordTime3),
          elem3 -> List(recordTime4, recordTime5),
        )
      )
      val clock = Clock.fixed(testNow, ZoneId.of("UTC"))
      val metric: DelayMetric[String] =
        DelayMetric.empty[String](
          recordTimeFunction = testRecordTimeFunction,
          objectives = List.empty,
          clock = clock,
        )

      val (newMetric, periodicValue) = metric
        .onNext(elem1)
        .onNext(elem2)
        .periodicValue()
        ._1
        .periodicValue()
        ._1
        .onNext(elem3)
        .periodicValue()
      val finalValue = newMetric.finalValue(totalDurationSeconds)

      val expectedMean = (delay4 + delay5) / 2
      periodicValue shouldBe DelayMetric.Value(Some(expectedMean))
      finalValue shouldBe DelayMetric.Value(None)
    }

    "compute violated max delay SLO with the most extreme value" in {
      val maxAllowedDelaySeconds: Long = aPositiveLong(1000)
      val elem1: String = aString()
      val elem2: String = aString()
      val elem3: String = aString()
      val elem4: String = aString()
      val testNow = Clock.systemUTC().instant()

      // first period
      val recordTime1 =
        testNow.minusSeconds(maxAllowedDelaySeconds - aPositiveLong(100)) // allowed record time

      // second period
      val recordTime2A =
        testNow.minusSeconds(maxAllowedDelaySeconds + aPositiveLong(100)) // not allowed record time
      val recordTime2B =
        testNow.minusSeconds(maxAllowedDelaySeconds + aPositiveLong(100)) // not allowed record time
      val delay2A = durationBetween(recordTime2A, testNow)
      val delay2B = durationBetween(recordTime2B, testNow)
      val meanInPeriod2 = delay2A.plus(delay2B).dividedBy(2).getSeconds

      // third period - a period with record times higher than anywhere else,
      // the mean delay from this period should be provided by the metric as the most violating value
      val recordTime3A = testNow.minusSeconds(
        maxAllowedDelaySeconds + 1000 + aPositiveLong(100)
      ) // not allowed record time
      val recordTime3B = testNow.minusSeconds(
        maxAllowedDelaySeconds + 1000 + aPositiveLong(100)
      ) // not allowed record time
      val delay3A = durationBetween(recordTime3A, testNow)
      val delay3B = durationBetween(recordTime3B, testNow)
      val meanInPeriod3 = delay3A.plus(delay3B).dividedBy(2).getSeconds

      // fourth period
      val recordTime4 =
        testNow.minusSeconds(maxAllowedDelaySeconds + aPositiveLong(100)) // not allowed record time
      val delay4 = durationBetween(recordTime4, testNow)
      val meanInPeriod4 = delay4.getSeconds

      val maxDelay = List(meanInPeriod2, meanInPeriod3, meanInPeriod4).max

      def testRecordTimeFunction: String => List[Timestamp] = recordTimeFunctionFromMap(
        Map(
          elem1 -> List(recordTime1),
          elem2 -> List(recordTime2A, recordTime2B),
          elem3 -> List(recordTime3A, recordTime3B),
          elem4 -> List(recordTime4),
        )
      )
      val expectedViolatedObjective = DelayMetric.DelayObjective.MaxDelay(maxAllowedDelaySeconds)
      val clock = Clock.fixed(testNow, ZoneId.of("UTC"))
      val metric: DelayMetric[String] =
        DelayMetric.empty[String](
          recordTimeFunction = testRecordTimeFunction,
          objectives = List(expectedViolatedObjective),
          clock = clock,
        )

      val violatedObjectives =
        metric
          .onNext(elem1)
          .periodicValue()
          ._1
          .onNext(elem2)
          .periodicValue()
          ._1
          .onNext(elem3)
          .periodicValue()
          ._1
          .onNext(elem4)
          .periodicValue()
          ._1
          .violatedObjectives

      violatedObjectives shouldBe Map(
        expectedViolatedObjective -> DelayMetric.Value(Some(maxDelay))
      )
    }
  }

  private def recordTimeFunctionFromMap(
      map: Map[String, List[Instant]]
  )(str: String): List[Timestamp] =
    map
      .map { case (k, v) => k -> v.map(instantToTimestamp) }
      .getOrElse(str, throw new RuntimeException(s"Unexpected record function argument: $str"))

  private def instantToTimestamp(instant: Instant): Timestamp =
    Timestamp.of(instant.getEpochSecond, instant.getNano)

  private def durationBetween(first: Instant, second: Instant): Duration =
    Duration.between(first, second)

  private def secondsBetween(first: Instant, second: Instant): Long =
    Duration.between(first, second).getSeconds

  private def dummyRecordTimesFunction(str: String): List[Timestamp] =
    str.map(_ => Timestamp.of(aPositiveLong(), 0)).toList

  private def anEmptyDelayMetric(clock: Clock): DelayMetric[String] =
    DelayMetric.empty[String](dummyRecordTimesFunction, List.empty, clock)

  private def aString(): String = Random.nextString(Random.nextInt(50))
  private def aPositiveInt(limit: Int = 100000): Int = Random.nextInt(limit)
  private def aPositiveLong(limit: Int = 100000): Long = aPositiveInt(limit).toLong
  private def aPositiveDouble(): Double = Random.nextDouble() * aPositiveInt()
}
