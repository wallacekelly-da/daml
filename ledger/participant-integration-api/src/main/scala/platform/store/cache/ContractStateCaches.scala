// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.platform.store.cache

import com.daml.ledger.offset.Offset
import com.daml.ledger.participant.state.v2.Update
import com.daml.lf.transaction.GlobalKey
import com.daml.lf.transaction.Transaction.ChildrenRecursion
import com.daml.logging.{ContextualizedLogger, LoggingContext}
import com.daml.metrics.Metrics
import com.daml.platform.{Node, NodeId}
import com.daml.platform.store.cache.ContractKeyStateValue.{Assigned, Unassigned}
import com.daml.platform.store.cache.ContractStateValue.{Active, Archived, ExistingContractValue}
import com.daml.platform.store.dao.events.ContractStateEvent

import scala.concurrent.ExecutionContext

class ContractStateCaches(
    private[cache] val keyState: StateCache[GlobalKey, ContractKeyStateValue],
    private[cache] val contractState: StateCache[ContractId, ContractStateValue],
)(implicit loggingContext: LoggingContext) {
  private val logger = ContextualizedLogger.get(getClass)

  def push(offset: Offset, transactionAccepted: Update.TransactionAccepted): Unit = {
    val keyMappingsBuilder = Map.newBuilder[Key, ContractKeyStateValue]
    val contractMappingsBuilder = Map.newBuilder[ContractId, ExistingContractValue]

    val transactionEvents = transactionAccepted.transaction
      .foldInExecutionOrder(Vector.empty[Node])(
        exerciseBegin = (acc, _, node) => (acc :+ node, ChildrenRecursion.DoRecurse),
        // Rollback nodes are not included in the indexer
        rollbackBegin = (acc, _, _) => (acc, ChildrenRecursion.DoNotRecurse),
        leaf = (acc, _, node) => acc :+ node,
        exerciseEnd = (acc, _, _) => acc,
        rollbackEnd = (acc, _, _) => acc,
      )

    transactionEvents.collect{
      case
    }

    transactionAccepted.fold.foreach {
      case created: ContractStateEvent.Created =>
        created.globalKey.foreach { key =>
          keyMappingsBuilder.addOne(key -> Assigned(created.contractId, created.stakeholders))
        }
        contractMappingsBuilder.addOne(
          created.contractId,
          Active(created.contract, created.stakeholders, created.ledgerEffectiveTime),
        )
      case archived: ContractStateEvent.Archived =>
        archived.globalKey.foreach { key =>
          keyMappingsBuilder.addOne(key -> Unassigned)
        }
        contractMappingsBuilder.addOne(archived.contractId, Archived(archived.stakeholders))
    }

    val keyMappings = keyMappingsBuilder.result()
    val contractMappings = contractMappingsBuilder.result()

    val validAt = eventsBatch.last.eventOffset
    if (keyMappings.nonEmpty) keyState.putBatch(validAt, keyMappings)
    if (contractMappings.nonEmpty)
      contractState.putBatch(validAt, contractMappings)
  }

  def reset(lastPersistedLedgerEnd: Offset): Unit = {
    keyState.reset(lastPersistedLedgerEnd)
    contractState.reset(lastPersistedLedgerEnd)
  }
}

object ContractStateCaches {
  def build(
      initialCacheIndex: Offset,
      maxContractsCacheSize: Long,
      maxKeyCacheSize: Long,
      metrics: Metrics,
  )(implicit
      executionContext: ExecutionContext,
      loggingContext: LoggingContext,
  ): ContractStateCaches =
    new ContractStateCaches(
      contractState = ContractsStateCache(initialCacheIndex, maxContractsCacheSize, metrics),
      keyState = ContractKeyStateCache(initialCacheIndex, maxKeyCacheSize, metrics),
    )
}
