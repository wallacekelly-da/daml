// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.metrics

import scala.jdk.CollectionConverters._
import com.codahale.metrics.{MetricRegistry, Snapshot, Timer}
import io.prometheus.client.Collector
import io.prometheus.client.Collector.MetricFamilySamples
import io.prometheus.client.dropwizard.DropwizardExportsAccess

import java.util
import java.util.concurrent.TimeUnit
import scala.util.{Failure, Success, Try}

final class ExtendedDropwizardExports(metricRegistry: MetricRegistry)
    extends DropwizardExportsAccess(metricRegistry) {

  override def collect(): util.List[MetricFamilySamples] = {
    Try {
      val mfSamplesMap = collection.mutable.Map.empty[String, MetricFamilySamples]
      //TODO: implement other metric types the same way
      val timers = metricRegistry.getTimers().asScala
      timers.foreach { case (name, timer) =>
        val newSamples: MetricFamilySamples = splitted(name) match {
          case (baseName, Some(applicationId)) =>
            fromTimer(
              name = baseName,
              timer = timer,
              labels = Map("application_id" -> applicationId),
            )
          case (baseName, None) =>
            fromTimer(
              name = baseName,
              timer = timer,
              labels = Map.empty,
            )
        }
        addToMap(mfSamplesMap, newSamples)
      }

      mfSamplesMap.values.toList.asJava
    } match {
      case Success(res) =>
        res
      case Failure(ex) =>
        println(s"Metric collection failed. Details: ${ex.getMessage}")
        throw ex
    }

  }

  private def helpMessage(metricName: String) =
    s"Generated from Dropwizard metric import (metric=$metricName) "

  private def splitted(name: String): (String, Option[String]) =
    MetricName.split(name)

  private def mergeSamples(
      samplesA: MetricFamilySamples,
      samplesB: MetricFamilySamples,
  ): MetricFamilySamples = {
    val allSamples = samplesA.samples.asScala ++ samplesB.samples.asScala
    new MetricFamilySamples(
      samplesA.name,
      samplesA.`type`,
      samplesA.help,
      allSamples.asJava,
    )
  }

  private def addToMap(
      samplesMap: collection.mutable.Map[String, MetricFamilySamples],
      newSamples: MetricFamilySamples,
  ): Unit = {
    val mergedSamples: MetricFamilySamples = samplesMap.get(newSamples.name) match {
      case Some(currentSamples) =>
        mergeSamples(currentSamples, newSamples)
      case None =>
        newSamples
    }
    samplesMap += newSamples.name -> mergedSamples
  }

  private def fromTimer(
      name: String,
      timer: Timer,
      labels: Map[String, String],
  ): MetricFamilySamples =
    fromSnapshotAndCountWithLabels(
      name,
      timer.getSnapshot,
      timer.getCount,
      1.0 / TimeUnit.SECONDS.toNanos(1L),
      helpMessage(name),
      labels,
    )

  private def fromSnapshotAndCountWithLabels(
      dropwizardName: String,
      snapshot: Snapshot,
      count: Long,
      factor: Double,
      helpMessage: String,
      labels: Map[String, String],
  ): MetricFamilySamples = {
    val labelNames = labels.keys.toList
    val labelNamesJava = labelNames.asJava
    val labelValues = labels.values.toList
    val labelValuesJava = labelValues.asJava
    val samples = List(
      sampleBuilder.createSample(
        dropwizardName,
        "",
        (List("quantile") ++ labelNames).asJava,
        (List("0.5") ++ labelValues).asJava,
        snapshot.getMedian * factor,
      ),
      sampleBuilder.createSample(
        dropwizardName,
        "",
        (List("quantile") ++ labelNames).asJava,
        (List("0.75") ++ labelValues).asJava,
        snapshot.get75thPercentile(),
      ),
      sampleBuilder.createSample(
        dropwizardName,
        "",
        (List("quantile") ++ labelNames).asJava,
        (List("0.95") ++ labelValues).asJava,
        snapshot.get95thPercentile(),
      ),
      sampleBuilder.createSample(
        dropwizardName,
        "",
        (List("quantile") ++ labelNames).asJava,
        (List("0.98") ++ labelValues).asJava,
        snapshot.get98thPercentile(),
      ),
      sampleBuilder.createSample(
        dropwizardName,
        "",
        (List("quantile") ++ labelNames).asJava,
        (List("0.99") ++ labelValues).asJava,
        snapshot.get99thPercentile(),
      ),
      sampleBuilder.createSample(
        dropwizardName,
        "",
        (List("quantile") ++ labelNames).asJava,
        (List("0.999") ++ labelValues).asJava,
        snapshot.get999thPercentile(),
      ),
      sampleBuilder.createSample(
        dropwizardName,
        "_count",
        labelNames.asJava,
        labelValues.asJava,
        count.toDouble,
      ),
      sampleBuilder
        .createSample(
          dropwizardName,
          "_min",
          labelNamesJava,
          labelValuesJava,
          snapshot.getMin * factor,
        ),
      sampleBuilder.createSample(
        dropwizardName,
        "_mean",
        labelNamesJava,
        labelValuesJava,
        snapshot.getMean * factor,
      ),
      sampleBuilder.createSample(
        dropwizardName,
        "_max",
        labelNamesJava,
        labelValuesJava,
        snapshot.getMax * factor,
      ),
    )

    new MetricFamilySamples(
      samples.head.name, // TODO: make this nicer, it's required to take a name from samples because sampleBuilder sanitize names
      Collector.Type.SUMMARY,
      helpMessage,
      samples.asJava,
    )
  }
}
