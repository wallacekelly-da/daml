// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.platform.indexer

import com.daml.lf.data.Ref
import com.daml.platform.indexer.IndexerConfig._
import com.daml.platform.indexer.ha.HaConfig
import com.daml.platform.store.backend.DataSourceStorageBackend
import com.daml.platform.store.backend.postgresql.PostgresDataSourceConfig

import scala.concurrent.duration.{DurationInt, FiniteDuration}

case class IndexerConfig(
    participantId: Ref.ParticipantId,
    startupMode: IndexerStartupMode,
    restartDelay: FiniteDuration = DefaultRestartDelay,
    maxInputBufferSize: Int = DefaultMaxInputBufferSize,
    inputMappingParallelism: Int = DefaultInputMappingParallelism,
    batchingParallelism: Int = DefaultBatchingParallelism,
    ingestionParallelism: Int = DefaultIngestionParallelism,
    submissionBatchSize: Long = DefaultSubmissionBatchSize,
    tailingRateLimitPerSecond: Int = DefaultTailingRateLimitPerSecond,
    batchWithinMillis: Long = DefaultBatchWithinMillis,
    enableCompression: Boolean = DefaultEnableCompression,
    haConfig: HaConfig = HaConfig(),
    dataSourceConfig: DataSourceStorageBackend.DataSourceConfig
)

object IndexerConfig {

  def createDefault(jdbcUrl: String) = DataSourceStorageBackend.DataSourceConfig(
    jdbcUrl = jdbcUrl,
    // PostgresSQL specific configurations
    // Setting aggressive keep-alive defaults to aid prompt release of the locks on the server side.
    // For reference https://www.postgresql.org/docs/13/runtime-config-connection.html#RUNTIME-CONFIG-CONNECTION-SETTINGS
    postgresConfig = PostgresDataSourceConfig(
      synchronousCommit = Some(PostgresDataSourceConfig.SynchronousCommitValue.Off),
      tcpKeepalivesIdle = Some(10),
      tcpKeepalivesInterval = Some(1),
      tcpKeepalivesCount = Some(5),
    )
  )

  val DefaultUpdatePreparationParallelism = 2
  val DefaultRestartDelay: FiniteDuration = 10.seconds
  val DefaultMaxInputBufferSize: Int = 50
  val DefaultInputMappingParallelism: Int = 16
  val DefaultBatchingParallelism: Int = 4
  val DefaultIngestionParallelism: Int = 16
  val DefaultSubmissionBatchSize: Long = 50L
  val DefaultTailingRateLimitPerSecond: Int = 20
  val DefaultBatchWithinMillis: Long = 50L
  val DefaultEnableCompression: Boolean = false

}
