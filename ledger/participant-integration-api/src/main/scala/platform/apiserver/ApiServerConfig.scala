// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.platform.apiserver

import com.daml.ledger.api.tls.TlsConfiguration
import com.daml.platform.apiserver.SeedService.Seeding
import com.daml.platform.configuration.{CommandConfiguration, InitialLedgerConfiguration, PartyConfiguration}
import com.daml.platform.services.time.TimeProviderType
import com.daml.platform.store.DbSupport.DbConfig
import com.daml.platform.usermanagement.UserManagementConfig
import com.daml.ports.Port

import java.nio.file.Path
import scala.concurrent.duration.{Duration, FiniteDuration}

case class ApiServerConfig(
    port: Port,
    address: Option[String], // This defaults to "localhost" when set to `None`.
    tls: Option[TlsConfiguration],
    maxInboundMessageSize: Int,
    initialLedgerConfiguration: Option[InitialLedgerConfiguration],
    configurationLoadTimeout: Duration,
    portFile: Option[Path],
    seeding: Seeding,
    managementServiceTimeout: FiniteDuration,
    userManagement: UserManagementConfig,
    authentication: AuthServiceConfig,
    party: PartyConfiguration,
    command: CommandConfiguration,
    timeProviderType: TimeProviderType,
    database: DbConfig,
)
