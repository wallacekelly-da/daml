// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.platform.server.api.services.grpc

import com.daml.ledger.api.v1.TransactionServiceOuterClass
trait TransactionServiceAkkaGrpc
    extends TransactionServiceGrpc.TransactionService
    with com.daml.grpc.adapter.server.akka.StreamingServiceLifecycleManagement {
  protected implicit def esf: com.daml.grpc.adapter.ExecutionSequencerFactory
  protected implicit def mat: akka.stream.Materializer

  def getTransactions(
      request: com.daml.ledger.api.v1.transaction_service.GetTransactionsRequest,
      responseObserver: _root_.io.grpc.stub.StreamObserver[
        TransactionServiceOuterClass.GetTransactionsResponse
      ],
  ): Unit =
    registerStream(() => getTransactionsSource(request), responseObserver)

  protected def getTransactionsSource(
      request: com.daml.ledger.api.v1.transaction_service.GetTransactionsRequest
  ): akka.stream.scaladsl.Source[
    TransactionServiceOuterClass.GetTransactionsResponse,
    akka.NotUsed,
  ]

  def getTransactionTrees(
      request: com.daml.ledger.api.v1.transaction_service.GetTransactionsRequest,
      responseObserver: _root_.io.grpc.stub.StreamObserver[
        TransactionServiceOuterClass.GetTransactionTreesResponse
      ],
  ): Unit =
    registerStream(() => getTransactionTreesSource(request), responseObserver)

  protected def getTransactionTreesSource(
      request: com.daml.ledger.api.v1.transaction_service.GetTransactionsRequest
  ): akka.stream.scaladsl.Source[
    TransactionServiceOuterClass.GetTransactionTreesResponse,
    akka.NotUsed,
  ]
}
