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

  def toInternalConfig(config: CliParticipantConfig): ParticipantConfig = ParticipantConfig(
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
    maxContractStateCacheSize = config.maxContractStateCacheSize,
    maxContractKeyStateCacheSize = config.maxContractKeyStateCacheSize,
    maxTransactionsInMemoryFanOutBufferSize = config.maxTransactionsInMemoryFanOutBufferSize,
  )

  def toInternalConfig(config: CliConfig[ExtraConfig]): Config = {
    Config(
      engineConfig = config.engineConfig,
      authService = config.authService,
      acsContractFetchingParallelism = config.acsContractFetchingParallelism,
      acsGlobalParallelism = config.acsGlobalParallelism,
      acsIdFetchingParallelism = config.acsIdFetchingParallelism,
      acsIdPageSize = config.acsIdPageSize,
      acsIdQueueLimit = config.acsIdQueueLimit,
      configurationLoadTimeout = config.configurationLoadTimeout,
      commandConfig = config.commandConfig,
      enableInMemoryFanOutForLedgerApi = config.enableInMemoryFanOutForLedgerApi,
      eventsPageSize = config.eventsPageSize,
      eventsProcessingParallelism = config.eventsProcessingParallelism,
      ledgerId = config.ledgerId,
      lfValueTranslationContractCache = config.lfValueTranslationContractCache,
      lfValueTranslationEventCache = config.lfValueTranslationEventCache,
      maxDeduplicationDuration = config.maxDeduplicationDuration,
      maxInboundMessageSize = config.maxInboundMessageSize,
      metricsReporter = config.metricsReporter,
      metricsReportingInterval = config.metricsReportingInterval,
      participants = config.participants.map(toInternalConfig),
      seeding = config.seeding,
      stateValueCache = config.stateValueCache,
      timeProviderType = config.timeProviderType,
      tlsConfig = config.tlsConfig,
      userManagementConfig = config.userManagementConfig,
    )
  }

  def apiServerConfig(
      participantConfig: ParticipantConfig,
      config: Config,
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
      tlsConfig = config.tlsConfig,
      maxInboundMessageSize = config.maxInboundMessageSize,
      initialLedgerConfiguration = Some(initialLedgerConfig(config)),
      configurationLoadTimeout = config.configurationLoadTimeout,
      indexConfiguration = IndexConfiguration(
        eventsPageSize = config.eventsPageSize,
        eventsProcessingParallelism = config.eventsProcessingParallelism,
        acsIdPageSize = config.acsIdPageSize,
        acsIdFetchingParallelism = config.acsIdFetchingParallelism,
        acsContractFetchingParallelism = config.acsContractFetchingParallelism,
        acsGlobalParallelism = config.acsGlobalParallelism,
        maxContractStateCacheSize = participantConfig.maxContractStateCacheSize,
        maxContractKeyStateCacheSize = participantConfig.maxContractKeyStateCacheSize,
        maxTransactionsInMemoryFanOutBufferSize =
          participantConfig.maxTransactionsInMemoryFanOutBufferSize,
        enableInMemoryFanOutForLedgerApi = config.enableInMemoryFanOutForLedgerApi,
        archiveFiles = Nil,
      ),
      portFile = participantConfig.portFile,
      seeding = config.seeding,
      managementServiceTimeout = participantConfig.managementServiceTimeout,
      userManagementConfig = config.userManagementConfig,
    )

  def initialLedgerConfig(config: Config): InitialLedgerConfiguration = {
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
