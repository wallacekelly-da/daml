// Copyright (c) 2021 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.nameof

import com.daml.nameof.NameOf.qualifiedNameOfCurrentFunc
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.annotation.nowarn
//import com.daml.nameof._

final class NameOfSpec extends AnyFlatSpec with Matchers {

  behavior of "NameOf"

  @nowarn()
  case class Ham() {
    def ham(): String = {
      qualifiedNameOfCurrentFunc()
    }
  }

  it should "return the correct full qualified name of the ham function in class Ham" in {
    Ham().ham() shouldBe "com.daml.nameof.NameOfSpec.Ham.ham"
  }

}
