// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.ledger.runner.common

import com.daml.ledger.configuration.Configuration
import com.daml.platform.apiserver.{ApiServerConfig, TimeServiceBackend}
import com.daml.platform.configuration.{
  IndexConfiguration,
  InitialLedgerConfiguration,
  PartyConfiguration,
}
import com.daml.platform.services.time.TimeProviderType
import io.grpc.ServerInterceptor
import scopt.OptionParser

import java.time.{Duration, Instant}
import java.util.concurrent.TimeUnit
import scala.annotation.unused
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
    indexerConfig = config.indexerConfig,
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
    apiServerConfig = ApiServerConfig(
      port = config.port,
      address = config.address,
      jdbcUrl = config.serverJdbcUrl,
      databaseConnectionPoolSize = config.apiServerDatabaseConnectionPoolSize,
      databaseConnectionTimeout = FiniteDuration(
        config.apiServerDatabaseConnectionTimeout.toMillis,
        TimeUnit.MILLISECONDS,
      ),
      tlsConfig = cliConfig.tlsConfig,
      maxInboundMessageSize = cliConfig.maxInboundMessageSize,
      initialLedgerConfiguration = Some(initialLedgerConfig(cliConfig.maxDeduplicationDuration)),
      configurationLoadTimeout = cliConfig.configurationLoadTimeout,
      portFile = config.portFile,
      seeding = cliConfig.seeding,
      managementServiceTimeout = config.managementServiceTimeout,
      userManagementConfig = cliConfig.userManagementConfig,
      authService = cliConfig.authService,
      commandConfig = cliConfig.commandConfig,
      partyConfig = PartyConfiguration.default,
      timeProviderType = cliConfig.timeProviderType,
    ),
  )

  def partyConfig(@unused extra: ExtraConfig): PartyConfiguration = PartyConfiguration.default

  def toInternalConfig(config: CliConfig[ExtraConfig]): Config = {
    Config(
      engineConfig = config.engineConfig,
      ledgerId = config.ledgerId,
      metricsReporter = config.metricsReporter,
      metricsReportingInterval = config.metricsReportingInterval,
      participants = config.participants.map(toParticipantConfig(config)),
    )
  }

  def initialLedgerConfig(
      maxDeduplicationDuration: Option[Duration]
  ): InitialLedgerConfiguration = {
    InitialLedgerConfiguration(
      configuration = Configuration.reasonableInitialConfiguration.copy(maxDeduplicationDuration =
        maxDeduplicationDuration.getOrElse(
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

  def timeServiceBackend(config: ApiServerConfig): Option[TimeServiceBackend] =
    config.timeProviderType match {
      case TimeProviderType.Static => Some(TimeServiceBackend.simple(Instant.EPOCH))
      case TimeProviderType.WallClock => None
    }

  def interceptors: List[ServerInterceptor] = List.empty

}

object ConfigProvider {
  class ForUnit extends ConfigProvider[Unit] {
    override val defaultExtraConfig: Unit = ()

    override def extraConfigParser(parser: OptionParser[CliConfig[Unit]]): Unit = ()
  }

  object ForUnit extends ForUnit
}
