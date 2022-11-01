// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.platform.store

import scalapb.GeneratedMessage

object ScalaPbOptimizations {
  implicit class ScalaPbMessageWithPrecomputedSerializedSize[
      ScalaPbMsg <: GeneratedMessage with AnyRef
  ](scalaPbMsg: ScalaPbMsg) {
    def precomputeSerializedSize(optimizeGrpcStreamThroughput: Boolean): ScalaPbMsg =
      if (optimizeGrpcStreamThroughput) {
        val _ = scalaPbMsg.serializedSize
        scalaPbMsg
      } else scalaPbMsg
  }
}
