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
