// Copyright (c) 2023 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.metrics

import com.daml.metrics.api.MetricDoc.MetricQualification.{Debug, Latency}
import com.daml.metrics.api.MetricHandle.{Counter, Meter, MetricsFactory, Timer}
import com.daml.metrics.api.{MetricDoc, MetricName}

import scala.annotation.nowarn

class CommandMetrics(prefix: MetricName, @nowarn @deprecated factory: MetricsFactory) {

  @MetricDoc.Tag(
    summary = "The time to validate a Daml command.",
    description = """The time to validate a submitted Daml command before is fed to the
                    |interpreter.""",
    qualification = Debug,
  )
  @nowarn
  val validation: Timer = factory.timer(prefix :+ "validation")

  @MetricDoc.Tag(
    summary = "The time to fully process a Daml command.",
    description = """The time to validate and interpret a command before it is handed over to the
                    |synchronization services to be finalized (either committed or rejected).""",
    qualification = Latency,
  )
  @nowarn
  val submissions: Timer = factory.timer(prefix :+ "submissions")

  @MetricDoc.Tag(
    summary =
      "The number of the Daml commands that are currently being handled by the ledger api server.",
    description = """The number of the Daml commands that are currently being handled by the ledger
                    |api server (including validation, interpretation, and handing the transaction
                    |over to the synchronization services).""",
    qualification = Debug,
  )
  @nowarn
  val submissionsRunning: Counter = factory.counter(prefix :+ "submissions_running")

  @MetricDoc.Tag(
    summary = "The number of Daml commands that failed in interpretation.",
    description = """The number of Daml commands that have been rejected by the interpreter
                    |(e.g. badly authorized action).""",
    qualification = Debug,
  )
  @nowarn
  val failedCommandInterpretations: Meter =
    factory.meter(prefix :+ "failed_command_interpretations")

  @MetricDoc.Tag(
    summary = "The number of the delayed Daml commands.",
    description = """The number of Daml commands that have been delayed internally because they
                    |have been evaluated to require the ledger time further in the future than the
                    |expected latency.""",
    qualification = Debug,
  )
  @nowarn
  val delayedSubmissions: Meter = factory.meter(prefix :+ "delayed_submissions")

  @MetricDoc.Tag(
    summary = "The total number of the valid Daml commands.",
    description = """The total number of the Daml commands that have passed validation and were
                    |sent to interpretation in this ledger api server process.""",
    qualification = Debug,
  )
  @nowarn
  val validSubmissions: Meter = factory.meter(prefix :+ "valid_submissions")

  @MetricDoc.Tag(
    summary = "The number of the currently pending Daml commands.",
    description = "The number of the currently pending Daml Commands in the Command Service.",
    qualification = Debug,
  )
  @nowarn
  val inputBufferLength: Counter = factory.counter(prefix :+ "input_buffer_length")

  @MetricDoc.Tag(
    summary = "The capacity of the Daml commands queue.",
    description = """The maximum number of elements that can be kept in the queue of Daml commands
                    |in the Command Service.""",
    qualification = Debug,
  )
  @nowarn
  val inputBufferCapacity: Counter = factory.counter(prefix :+ "input_buffer_capacity")

  @MetricDoc.Tag(
    summary = "The queuing delay for the Daml command queue.",
    description = "The queuing delay for the pending Daml commands in the Command Service.",
    qualification = Debug,
  )
  @nowarn
  val inputBufferDelay: Timer = factory.timer(prefix :+ "input_buffer_delay")

  @MetricDoc.Tag(
    summary = "The number of the Daml commands awaiting completion.",
    description =
      "The number of the currently Daml commands awaiting completion in the Command Service.",
    qualification = Debug,
  )
  @nowarn
  val maxInFlightLength: Counter = factory.counter(prefix :+ "max_in_flight_length")

  @MetricDoc.Tag(
    summary = "The maximum number of Daml commands that can await completion.",
    description =
      "The maximum number of Daml commands that can await completion in the Command Service.",
    qualification = Debug,
  )
  @nowarn
  val maxInFlightCapacity: Counter = factory.counter(prefix :+ "max_in_flight_capacity")
}
