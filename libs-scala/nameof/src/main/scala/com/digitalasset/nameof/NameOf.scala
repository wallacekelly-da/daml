// Copyright (c) 2021 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.nameof

trait NameOf {
  import scala.language.experimental.macros

  /** Obtain an identifier name as a constant string.
    *
    * Example usage:
    * {{{
    *   val amount = 5
    *   nameOf(amount) => "amount"
    * }}}
    */
  def qualifiedNameOfCurrentFunc(): String = macro NameOfImpl.nameOf
}
object NameOf extends NameOf
