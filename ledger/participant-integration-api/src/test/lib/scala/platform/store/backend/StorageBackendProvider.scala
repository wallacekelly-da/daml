// Copyright (c) 2021 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.platform.store.backend

import java.sql.Connection

import com.daml.platform.store.backend.h2.H2StorageBackendFactory
import com.daml.platform.store.backend.oracle.OracleStorageBackendFactory
import com.daml.platform.store.backend.postgresql.PostgresStorageBackendFactory
import com.daml.testing.oracle.OracleAroundAll
import com.daml.testing.postgresql.PostgresAroundAll
import org.scalatest.Suite

/** Creates a database and a [[StorageBackend]].
  * Used by [[StorageBackendSpec]] to run all StorageBackend tests on different databases.
  */
private[backend] trait StorageBackendProvider {
  protected def jdbcUrl: String
  protected def backendFactory: StorageBackendFactory

  protected final def ingest(dbDtos: Vector[DbDto], connection: Connection): Unit = {
    def typeBoundIngest[T](ingestionStorageBackend: IngestionStorageBackend[T]): Unit =
      ingestionStorageBackend.insertBatch(connection, ingestionStorageBackend.batch(dbDtos))
    typeBoundIngest(backendFactory.createIngestionStorageBackend)
  }
}

private[backend] trait StorageBackendProviderPostgres
    extends StorageBackendProvider
    with PostgresAroundAll { this: Suite =>
  override protected def jdbcUrl: String = postgresDatabase.url
  override protected val backendFactory: StorageBackendFactory = PostgresStorageBackendFactory
}

private[backend] trait StorageBackendProviderH2 extends StorageBackendProvider { this: Suite =>
  override protected def jdbcUrl: String = "jdbc:h2:mem:storage_backend_provider;db_close_delay=-1"
  override protected val backendFactory: StorageBackendFactory = H2StorageBackendFactory
}

private[backend] trait StorageBackendProviderOracle
    extends StorageBackendProvider
    with OracleAroundAll { this: Suite =>
  override protected def jdbcUrl: String =
    s"jdbc:oracle:thin:$oracleUser/$oraclePwd@localhost:$oraclePort/ORCLPDB1"
  override protected val backendFactory: StorageBackendFactory = OracleStorageBackendFactory
}
