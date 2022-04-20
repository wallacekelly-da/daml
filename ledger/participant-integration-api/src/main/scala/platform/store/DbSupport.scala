// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.platform.store

import com.daml.ledger.api.health.ReportsHealth
import com.daml.ledger.resources.{ResourceContext, ResourceOwner}
import com.daml.logging.LoggingContext
import com.daml.metrics.Metrics
import com.daml.platform.configuration.ServerRole
import com.daml.platform.store.appendonlydao.DbDispatcher
import com.daml.platform.store.backend.{DataSourceStorageBackend, StorageBackendFactory}
import com.daml.resources.{PureResource, Resource}

import scala.concurrent.duration.FiniteDuration

case class DbSupport(
    dbDispatcher: DbDispatcher with ReportsHealth,
    storageBackendFactory: StorageBackendFactory,
)

object DbSupport {
  def owner(
      dataSourceConfig: DataSourceStorageBackend.DataSourceConfig,
      serverRole: ServerRole,
      connectionPoolSize: Int,
      connectionTimeout: FiniteDuration,
      metrics: Metrics,
  )(implicit loggingContext: LoggingContext): ResourceOwner[DbSupport] = {
    val dbType = DbType.jdbcType(dataSourceConfig.jdbcUrl)
    val storageBackendFactory = StorageBackendFactory.of(dbType)
    DbDispatcher
      .owner(
        dataSource = storageBackendFactory.createDataSourceStorageBackend
          .createDataSource(dataSourceConfig),
        serverRole = serverRole,
        connectionPoolSize = connectionPoolSize,
        connectionTimeout = connectionTimeout,
        metrics = metrics,
      )
      .map(dbDispatcher =>
        DbSupport(
          dbDispatcher = dbDispatcher,
          storageBackendFactory = storageBackendFactory,
        )
      )
  }

  def migratedOwner(
      serverRole: ServerRole,
      dataSourceConfig: DataSourceStorageBackend.DataSourceConfig,
      connectionPoolSize: Int,
      connectionTimeout: FiniteDuration,
      metrics: Metrics,
  )(implicit loggingContext: LoggingContext): ResourceOwner[DbSupport] = {
    val migrationOwner = new ResourceOwner[Unit] {
      override def acquire()(implicit context: ResourceContext): Resource[ResourceContext, Unit] =
        PureResource(new FlywayMigrations(dataSourceConfig).migrate())
    }

    for {
      _ <- migrationOwner
      dbSupport <- owner(
        serverRole = serverRole,
        connectionPoolSize = connectionPoolSize,
        connectionTimeout = connectionTimeout,
        metrics = metrics,
        dataSourceConfig = dataSourceConfig,
      )
    } yield dbSupport
  }
}
