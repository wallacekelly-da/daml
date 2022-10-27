// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.platform.apiserver.services

import akka.stream.scaladsl.Source
import scalapb.GeneratedMessage

object ScalaPbOptimizationsFlow {
  implicit class ScalaPbOptimizationsFlow[ScalaPbMessage <: GeneratedMessage, Mat](
      val original: Source[ScalaPbMessage, Mat]
  ) extends AnyVal {

    /** Optimization for gRPC stream throughput.
      *
      * gRPC internal logic marshalls the protobuf response payloads sequentially before
      * sending them over the wire (see io.grpc.ServerCallImpl.sendMessageInternal), imposing as limit
      * the maximum marshalling throughput of a payload type.
      *
      * We've observed empirically that ScalaPB-generated message classes have associated marshallers
      * with significant latencies when encoding complex payloads (e.g. [[com.daml.ledger.api.v1.transaction_service.GetTransactionTreesResponse]]),
      * with the gRPC marshalling bottleneck appearing in some performance tests.
      *
      * As an alleviation of the problem, we can leverage the fact that ScalaPB message classes have the serializedSize value memoized,
      * (see [[scalapb.GeneratedMessage.writeTo]]), whose computation is roughly half of the entire marshalling step.
      *
      * This optimization method takes advantage of the memoized value and forces the message's serializedSize computation,
      * roughly doubling the maximum theoretical ScalaPB stream throughput over the gRPC server layer.
      *
      * @return A new source with precomputed serializedSize for the [[scalapb.GeneratedMessage]]
      */
    def precomputeSerializedSize: Source[ScalaPbMessage, Mat] =
      original.map { msg =>
        val _ = msg.serializedSize
        msg
      }.async
  }
}
