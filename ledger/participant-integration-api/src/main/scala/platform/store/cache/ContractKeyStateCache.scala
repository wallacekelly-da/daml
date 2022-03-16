// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.platform.store.cache

import com.daml.caching.SizedCache
import com.daml.lf.transaction.GlobalKey
import com.daml.metrics.Metrics

import scala.concurrent.{ExecutionContext, Future}

object ContractKeyStateCache {
  def apply(
      cacheSize: Long,
      metrics: Metrics,
      load: (GlobalKey, Long) => Future[ContractKeyStateValue],
      initialIndex: Long,
  )(implicit
      ec: ExecutionContext
  ): StateCache[GlobalKey, ContractKeyStateValue] =
    StateCache(
      cache = SizedCache.from[GlobalKey, StateCache.State[ContractKeyStateValue]](
        SizedCache.Configuration(cacheSize),
        metrics.daml.execution.cache.keyState,
      ),
      registerUpdateTimer = metrics.daml.execution.cache.registerCacheUpdate,
      load = load,
      initialIndex = initialIndex,
    )
}

sealed trait ContractKeyStateValue extends Product with Serializable

object ContractKeyStateValue {

  final case class Assigned(contractId: ContractId, createWitnesses: Set[Party])
      extends ContractKeyStateValue

  final case object Unassigned extends ContractKeyStateValue
}
