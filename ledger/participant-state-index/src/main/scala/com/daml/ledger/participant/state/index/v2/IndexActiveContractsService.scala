// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.ledger.participant.state.index.v2

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.daml.ledger.api.domain.TransactionFilter
import com.daml.ledger.api.v1.active_contracts_service.GetActiveContractsResponse
import com.daml.logging.LoggingContext
import com.daml.platform.packagemeta.PackageMetadata

/** Serves as a backend to implement
  * [[com.daml.ledger.api.v1.active_contracts_service.ActiveContractsServiceGrpc.ActiveContractsService]]
  */
trait IndexActiveContractsService {

  def getActiveContracts(
      filter: TransactionFilter,
      verbose: Boolean,
  )(implicit loggingContext: LoggingContext): Source[GetActiveContractsResponse, NotUsed]

  def currentPackageMetadata(): PackageMetadata

}
