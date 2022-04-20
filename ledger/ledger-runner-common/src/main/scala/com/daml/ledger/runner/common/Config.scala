// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.ledger.runner.common

import com.daml.lf.engine.EngineConfig
import com.daml.metrics.MetricsReporter

import java.time.Duration

final case class Config(
    ledgerId: String,
    engineConfig: EngineConfig,
    metricsReporter: Option[MetricsReporter],
    metricsReportingInterval: Duration,
    participants: Seq[ParticipantConfig],
)
