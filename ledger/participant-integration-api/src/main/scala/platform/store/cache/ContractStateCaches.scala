// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.platform.store.cache

import com.daml.ledger.offset.Offset
import com.daml.ledger.participant.state.v2.Update
import com.daml.lf.transaction.Transaction.ChildrenRecursion
import com.daml.lf.transaction.{GlobalKey, Node}
import com.daml.logging.LoggingContext
import com.daml.metrics.Metrics
import com.daml.platform.store.cache.ContractKeyStateValue.{Assigned, Unassigned}
import com.daml.platform.store.cache.ContractStateValue.{Active, Archived, ExistingContractValue}

import scala.concurrent.ExecutionContext

class ContractStateCaches(
    private[cache] val keyState: StateCache[GlobalKey, ContractKeyStateValue],
    private[cache] val contractState: StateCache[ContractId, ContractStateValue],
)(implicit loggingContext: LoggingContext) {
  def push(offset: Offset, transactionAccepted: Update.TransactionAccepted): Unit = {
    val keyMappingsBuilder = Map.newBuilder[Key, ContractKeyStateValue]
    val contractMappingsBuilder = Map.newBuilder[ContractId, ExistingContractValue]
    val txLedgerEffectiveTime = transactionAccepted.transactionMeta.ledgerEffectiveTime

    def cacheArchive(exercise: Node.Exercise): Unit = {
      exercise.key.foreach { keyWithMaintainers =>
        val key = GlobalKey.assertBuild(exercise.templateId, keyWithMaintainers.key)
        keyMappingsBuilder.addOne(key -> Unassigned)
      }
      contractMappingsBuilder.addOne(exercise.targetCoid, Archived(exercise.stakeholders))
    }

    def cacheCreate(create: Node.Create): Unit = {
      val contractId = create.coid
      val stakeholders = create.stakeholders

      val activeContract = Active(
        contract = create.versionedCoinst,
        stakeholders = stakeholders,
        createLedgerEffectiveTime = txLedgerEffectiveTime,
      )

      create.key.foreach { keyWithMaintainers =>
        val key = GlobalKey.assertBuild(create.templateId, keyWithMaintainers.key)
        keyMappingsBuilder.addOne(key -> Assigned(contractId, stakeholders))
      }
      contractMappingsBuilder.addOne(contractId -> activeContract)
    }

    val transactionEvents = transactionAccepted.transaction
      .foldInExecutionOrder(Vector.empty[Node])(
        exerciseBegin = (acc, _, node) => (acc :+ node, ChildrenRecursion.DoRecurse),
        // Rollback nodes are not included in the indexer
        rollbackBegin = (acc, _, _) => (acc, ChildrenRecursion.DoNotRecurse),
        leaf = (acc, _, node) => acc :+ node,
        exerciseEnd = (acc, _, _) => acc,
        rollbackEnd = (acc, _, _) => acc,
      )

    transactionEvents.foreach {
      case create: Node.Create => cacheCreate(create)
      case exercise: Node.Exercise if exercise.consuming => cacheArchive(exercise)
      case _ =>
    }

    val keyMappings = keyMappingsBuilder.result()
    val contractMappings = contractMappingsBuilder.result()

    if (keyMappings.nonEmpty) keyState.putBatch(offset, keyMappings)
    if (contractMappings.nonEmpty) contractState.putBatch(offset, contractMappings)
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
