// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.ledger.runner.common

import com.daml.caching
import com.daml.ledger.api.auth.AuthService
import com.daml.ledger.api.tls.TlsConfiguration
import com.daml.lf.engine.EngineConfig
import com.daml.metrics.MetricsReporter
import com.daml.platform.apiserver.SeedService.Seeding
import com.daml.platform.configuration.CommandConfiguration
import com.daml.platform.services.time.TimeProviderType
import com.daml.platform.usermanagement.UserManagementConfig

import java.time.Duration

final case class Config(
    engineConfig: EngineConfig,
    authService: AuthService,
    configurationLoadTimeout: Duration,
    commandConfig: CommandConfiguration,
    ledgerId: String,
    lfValueTranslationContractCache: caching.SizedCache.Configuration,
    lfValueTranslationEventCache: caching.SizedCache.Configuration,
    maxDeduplicationDuration: Option[Duration],
    maxInboundMessageSize: Int,
    metricsReporter: Option[MetricsReporter],
    metricsReportingInterval: Duration,
    participants: Seq[ParticipantConfig],
    seeding: Seeding,
    stateValueCache: caching.WeightedCache.Configuration,
    timeProviderType: TimeProviderType,
    tlsConfig: Option[TlsConfiguration],
    userManagementConfig: UserManagementConfig,
)
