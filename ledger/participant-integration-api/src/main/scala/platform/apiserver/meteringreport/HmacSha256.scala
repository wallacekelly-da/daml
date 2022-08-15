// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.platform.apiserver.meteringreport

import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

import java.util.Base64
import javax.crypto.spec.SecretKeySpec
import javax.crypto.{KeyGenerator, Mac}
import scala.util.Try

object HmacSha256 {

  def toBase64(bytes: Array[Byte]): String = Base64.getUrlEncoder.encodeToString(bytes)

  // The key used for both mac and key generation as defined in
  // https://docs.oracle.com/javase/9/docs/specs/security/standard-names.html
  val algorithm = "HmacSHA256"

  def compute(key: Key, message: Array[Byte]): Either[Throwable, Array[Byte]] = {
    Try {
      val mac = Mac.getInstance(algorithm)
      val secretKey = new SecretKeySpec(key.encoded.bytes, key.algorithm)
      mac.init(secretKey)
      mac.doFinal(message)
    }.toEither
  }

  def generateKey(): Key = {
    val generator = KeyGenerator.getInstance(algorithm)
    val key = generator.generateKey()
    Key(Bytes(key.getEncoded), key.getAlgorithm)
  }

  final case class Bytes(bytes: Array[Byte]) {
    override def equals(obj: Any): Boolean = obj match {
      case Bytes(other) => other.sameElements(bytes)
      case _ => false
    }

    override def toString: String = toBase64
    def toBase64: String = HmacSha256.toBase64(bytes)
  }
  implicit val BytesFormat: RootJsonFormat[Bytes] = jsonFormat1(Bytes.apply)

  final case class Key(encoded: Bytes, algorithm: String)
  implicit val KeyFormat: RootJsonFormat[Key] = jsonFormat2(Key.apply)

}
