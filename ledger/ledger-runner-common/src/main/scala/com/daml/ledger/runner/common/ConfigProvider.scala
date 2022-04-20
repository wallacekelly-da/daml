// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.ledger.runner.common

import com.daml.ledger.configuration.Configuration
import com.daml.platform.apiserver.{ApiServerConfig, TimeServiceBackend}
import com.daml.platform.configuration.{IndexConfiguration, InitialLedgerConfiguration}
import com.daml.platform.services.time.TimeProviderType
import scopt.OptionParser

import java.time.{Duration, Instant}
import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

trait ConfigProvider[ExtraConfig] {
  val defaultExtraConfig: ExtraConfig

  def extraConfigParser(parser: OptionParser[CliConfig[ExtraConfig]]): Unit

  def toParticipantConfig(cliConfig: CliConfig[ExtraConfig])(
      config: CliParticipantConfig
  ): ParticipantConfig = ParticipantConfig(
    mode = config.mode,
    participantId = config.participantId,
    shardName = config.shardName,
    address = config.address,
    port = config.port,
    portFile = config.portFile,
    serverJdbcUrl = config.serverJdbcUrl,
    managementServiceTimeout = config.managementServiceTimeout,
    indexerConfig = config.indexerConfig,
    apiServerDatabaseConnectionPoolSize = config.apiServerDatabaseConnectionPoolSize,
    apiServerDatabaseConnectionTimeout = config.apiServerDatabaseConnectionTimeout,
    indexConfiguration = IndexConfiguration(
      acsContractFetchingParallelism = cliConfig.acsContractFetchingParallelism,
      acsGlobalParallelism = cliConfig.acsGlobalParallelism,
      acsIdFetchingParallelism = cliConfig.acsIdFetchingParallelism,
      acsIdPageSize = cliConfig.acsIdPageSize,
      enableInMemoryFanOutForLedgerApi = cliConfig.enableInMemoryFanOutForLedgerApi,
      eventsPageSize = cliConfig.eventsPageSize,
      eventsProcessingParallelism = cliConfig.eventsProcessingParallelism,
      maxContractStateCacheSize = config.maxContractStateCacheSize,
      maxContractKeyStateCacheSize = config.maxContractKeyStateCacheSize,
      maxTransactionsInMemoryFanOutBufferSize = config.maxTransactionsInMemoryFanOutBufferSize,
      archiveFiles = IndexConfiguration.DefaultArchiveFiles,
    ),
    lfValueTranslationContractCache = cliConfig.lfValueTranslationContractCache,
    lfValueTranslationEventCache = cliConfig.lfValueTranslationEventCache,
    maxDeduplicationDuration = cliConfig.maxDeduplicationDuration,
    tlsConfig = cliConfig.tlsConfig,
    userManagementConfig = cliConfig.userManagementConfig,
    seeding = cliConfig.seeding,
    maxInboundMessageSize = cliConfig.maxInboundMessageSize,
    configurationLoadTimeout = cliConfig.configurationLoadTimeout,
  )

  def toInternalConfig(config: CliConfig[ExtraConfig]): Config = {
    Config(
      engineConfig = config.engineConfig,
      authService = config.authService,
      configurationLoadTimeout = config.configurationLoadTimeout,
      commandConfig = config.commandConfig,
      ledgerId = config.ledgerId,
      metricsReporter = config.metricsReporter,
      metricsReportingInterval = config.metricsReportingInterval,
      participants = config.participants.map(toParticipantConfig(config)),
      stateValueCache = config.stateValueCache,
      timeProviderType = config.timeProviderType,
    )
  }

  def apiServerConfig(
      participantConfig: ParticipantConfig
  ): ApiServerConfig =
    ApiServerConfig(
      participantId = participantConfig.participantId,
      port = participantConfig.port,
      address = participantConfig.address,
      jdbcUrl = participantConfig.serverJdbcUrl,
      databaseConnectionPoolSize = participantConfig.apiServerDatabaseConnectionPoolSize,
      databaseConnectionTimeout = FiniteDuration(
        participantConfig.apiServerDatabaseConnectionTimeout.toMillis,
        TimeUnit.MILLISECONDS,
      ),
      tlsConfig = participantConfig.tlsConfig,
      maxInboundMessageSize = participantConfig.maxInboundMessageSize,
      initialLedgerConfiguration = Some(initialLedgerConfig(participantConfig)),
      configurationLoadTimeout = participantConfig.configurationLoadTimeout,
      portFile = participantConfig.portFile,
      seeding = participantConfig.seeding,
      managementServiceTimeout = participantConfig.managementServiceTimeout,
      userManagementConfig = participantConfig.userManagementConfig,
    )

  def initialLedgerConfig(config: ParticipantConfig): InitialLedgerConfiguration = {
    InitialLedgerConfiguration(
      configuration = Configuration.reasonableInitialConfiguration.copy(maxDeduplicationDuration =
        config.maxDeduplicationDuration.getOrElse(
          Configuration.reasonableInitialConfiguration.maxDeduplicationDuration
        )
      ),
      // If a new index database is added to an already existing ledger,
      // a zero delay will likely produce a "configuration rejected" ledger entry,
      // because at startup the indexer hasn't ingested any configuration change yet.
      // Override this setting for distributed ledgers where you want to avoid these superfluous entries.
      delayBeforeSubmitting = Duration.ZERO,
    )
  }

  def timeServiceBackend(config: Config): Option[TimeServiceBackend] =
    config.timeProviderType match {
      case TimeProviderType.Static => Some(TimeServiceBackend.simple(Instant.EPOCH))
      case TimeProviderType.WallClock => None
    }
}

object ConfigProvider {
  class ForUnit extends ConfigProvider[Unit] {
    override val defaultExtraConfig: Unit = ()

    override def extraConfigParser(parser: OptionParser[CliConfig[Unit]]): Unit = ()
  }

  object ForUnit extends ForUnit
}
