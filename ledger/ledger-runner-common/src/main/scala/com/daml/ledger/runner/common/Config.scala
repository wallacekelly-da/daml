// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.ledger.runner.common

import com.daml.caching
import com.daml.ledger.api.auth.AuthService
import com.daml.lf.engine.EngineConfig
import com.daml.metrics.MetricsReporter
import com.daml.platform.configuration.CommandConfiguration
import com.daml.platform.services.time.TimeProviderType

import java.time.Duration

final case class Config(
    engineConfig: EngineConfig,
    authService: AuthService,
    configurationLoadTimeout: Duration,
    commandConfig: CommandConfiguration,
    ledgerId: String,
    metricsReporter: Option[MetricsReporter],
    metricsReportingInterval: Duration,
    participants: Seq[ParticipantConfig],
    stateValueCache: caching.WeightedCache.Configuration,
    timeProviderType: TimeProviderType,
)
