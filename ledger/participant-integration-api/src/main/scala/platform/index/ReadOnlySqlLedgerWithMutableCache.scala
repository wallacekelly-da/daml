// Copyright (c) 2021 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.platform.index

import java.util.concurrent.TimeUnit

import akka.stream._
import akka.stream.scaladsl.{Keep, RestartSource, Sink, Source}
import akka.{Done, NotUsed}
import com.codahale.metrics.Timer
import com.daml.ledger.api.domain.LedgerId
import com.daml.ledger.participant.state.v1.Offset
import com.daml.ledger.resources.{Resource, ResourceContext, ResourceOwner}
import com.daml.logging.LoggingContext
import com.daml.metrics.Metrics
import com.daml.platform.akkastreams.dispatcher.Dispatcher
import com.daml.platform.akkastreams.dispatcher.SubSource.RangeSource
import com.daml.platform.index.ReadOnlySqlLedgerWithMutableCache.DispatcherLagMeter
import com.daml.platform.store.appendonlydao.EventSequentialId
import com.daml.platform.store.appendonlydao.events.BufferedTransactionsReader.{
  CreatedEvent,
  ExercisedEvent,
  TransactionEvent,
}
import com.daml.platform.store.appendonlydao.events.{
  BufferedTransactions,
  BufferedTransactionsReader,
  Contract,
  Key,
  Party,
}
import com.daml.platform.store.cache.MutableCacheBackedContractStore
import com.daml.platform.store.cache.MutableCacheBackedContractStore.SignalNewLedgerHead
import com.daml.platform.store.dao.LedgerReadDao
import com.daml.platform.store.dao.events.ContractStateEvent
import com.daml.scalautil.Statement.discard

import scala.collection.mutable
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

private[index] object ReadOnlySqlLedgerWithMutableCache {
  final class Owner(
      ledgerDao: LedgerReadDao,
      ledgerId: LedgerId,
      metrics: Metrics,
      maxContractStateCacheSize: Long,
      maxContractKeyStateCacheSize: Long,
  )(implicit mat: Materializer, loggingContext: LoggingContext)
      extends ResourceOwner[ReadOnlySqlLedgerWithMutableCache] {

    override def acquire()(implicit
        context: ResourceContext
    ): Resource[ReadOnlySqlLedgerWithMutableCache] =
      for {
        (ledgerEndOffset, ledgerEndSequentialId) <- Resource.fromFuture(
          ledgerDao.lookupLedgerEndOffsetAndSequentialId()
        )
        contractStateEventsDispatcher <- dispatcherOffsetSeqIdOwner(
          ledgerEndOffset,
          ledgerEndSequentialId,
        ).acquire()
        generalDispatcher <- dispatcherOwner(ledgerEndOffset).acquire()
        dispatcherLagMeter <- Resource.successful(
          new DispatcherLagMeter(generalDispatcher.signalNewHead)(
            metrics.daml.execution.cache.dispatcherLag
          )
        )
        bufferedTransactions = new BufferedTransactions(1000)
        contractStore <- contractStoreOwner(
          dispatcherLagMeter,
          contractStateEventsDispatcher,
          bufferedTransactions,
        )
        ledger <- ledgerOwner(
          contractStateEventsDispatcher,
          generalDispatcher,
          contractStore,
          dispatcherLagMeter,
        )
          .acquire()
      } yield ledger

    private def ledgerOwner(
        contractStateEventsDispatcher: Dispatcher[(Offset, Long)],
        generalDispatcher: Dispatcher[Offset],
        contractStore: MutableCacheBackedContractStore,
        dispatcherLagger: DispatcherLagMeter,
    ) = {
      ResourceOwner
        .forCloseable(() =>
          new ReadOnlySqlLedgerWithMutableCache(
            ledgerId,
            ledgerDao,
            contractStore,
            contractStateEventsDispatcher,
            generalDispatcher,
            dispatcherLagger,
          )
        )
    }

    private def contractStoreOwner(
        signalNewLedgerHead: SignalNewLedgerHead,
        contractStateEventsDispatcher: Dispatcher[(Offset, Long)],
        bufferedTransactions: BufferedTransactions,
    )(implicit
        context: ResourceContext
    ) =
      MutableCacheBackedContractStore.owner(
        contractsReader = ledgerDao.contractsReader,
        signalNewLedgerHead = signalNewLedgerHead,
        subscribeToContractStateEvents = (offset: Offset, eventSequentialId: Long) =>
          contractStateEventsDispatcher
            .startingAt(
              offset -> eventSequentialId,
              RangeSource(
                ledgerDao.transactionsReader.getTransactionEvents(_, _)
              ),
            )
            .alsoTo {
              Sink.foreach {
                case ((offset, _), event: BufferedTransactionsReader.Transaction) =>
                  bufferedTransactions.push(offset, event)
                case _ => ()
              }
            }
            .flatMapConcat(tx => toContractStateEvents(tx._2)),
        metrics = metrics,
        maxContractsCacheSize = maxContractStateCacheSize,
        maxKeyCacheSize = maxContractKeyStateCacheSize,
      )

    private def toContractStateEvents(tx: TransactionEvent): Source[ContractStateEvent, NotUsed] =
      tx match {
        case tx: BufferedTransactionsReader.Transaction =>
          Source.fromIterator(() =>
            tx.events.iterator.collect {
              case createdEvent: CreatedEvent =>
                ContractStateEvent.Created(
                  contractId = createdEvent.contractId,
                  contract = Contract(
                    template = createdEvent.templateId,
                    arg = createdEvent.createArgument,
                    agreementText = "",
                  ),
                  globalKey = createdEvent.contractKey.map(k =>
                    Key.assertBuild(createdEvent.templateId, k.value)
                  ),
                  ledgerEffectiveTime = createdEvent.ledgerEffectiveTime,
                  stakeholders = createdEvent.flatEventWitnesses.map(Party.assertFromString),
                  eventOffset = createdEvent.eventOffset,
                  eventSequentialId = createdEvent.eventSequentialId,
                )
              case exercisedEvent: ExercisedEvent if exercisedEvent.consuming =>
                ContractStateEvent.Archived(
                  contractId = exercisedEvent.contractId,
                  globalKey = exercisedEvent.contractKey.map(k =>
                    Key.assertBuild(exercisedEvent.templateId, k.value)
                  ),
                  stakeholders = exercisedEvent.flatEventWitnesses.map(Party.assertFromString),
                  eventOffset = exercisedEvent.eventOffset,
                  eventSequentialId = exercisedEvent.eventSequentialId,
                )
            }
          )
        case BufferedTransactionsReader.LedgerEndMarker(eventOffset, eventSequentialId) =>
          Source.single(ContractStateEvent.LedgerEndMarker(eventOffset, eventSequentialId))
      }

    private def dispatcherOffsetSeqIdOwner(ledgerEnd: Offset, evtSeqId: Long) = {
      Dispatcher.owner(
        name = "contract-state-events",
        zeroIndex = (Offset.beforeBegin, EventSequentialId.beforeBegin),
        headAtInitialization = (ledgerEnd, evtSeqId),
      )
    }

    private def dispatcherOwner(ledgerEnd: Offset): ResourceOwner[Dispatcher[Offset]] =
      Dispatcher.owner(
        name = "sql-ledger",
        zeroIndex = Offset.beforeBegin,
        headAtInitialization = ledgerEnd,
      )
  }

  /** Computes the lag between the contract state events dispatcher and the general dispatcher.
    *
    * Internally uses a size bound for preventing memory leaks if misused.
    *
    * @param delegate The ledger head dispatcher delegate.
    * @param timer The timer measuring the delta.
    */
  private class DispatcherLagMeter(delegate: SignalNewLedgerHead, maxSize: Long = 1000L)(
      timer: Timer
  ) extends SignalNewLedgerHead {
    private val ledgerHeads = mutable.Map.empty[Offset, Long]

    override def apply(offset: Offset): Unit = {
      delegate(offset)
      ledgerHeads.synchronized {
        ledgerHeads.remove(offset).foreach { startNanos =>
          val endNanos = System.nanoTime()
          timer.update(endNanos - startNanos, TimeUnit.NANOSECONDS)
        }
      }
    }

    private[ReadOnlySqlLedgerWithMutableCache] def startTimer(head: Offset): Unit =
      ledgerHeads.synchronized {
        ensureBounded()
        discard(ledgerHeads.getOrElseUpdate(head, System.nanoTime()))
      }

    private def ensureBounded(): Unit =
      if (ledgerHeads.size > maxSize) {
        // If maxSize is reached, remove randomly ANY element.
        ledgerHeads.headOption.foreach(head => ledgerHeads.remove(head._1))
      } else ()
  }
}

private final class ReadOnlySqlLedgerWithMutableCache(
    ledgerId: LedgerId,
    ledgerDao: LedgerReadDao,
    contractStore: MutableCacheBackedContractStore,
    contractStateEventsDispatcher: Dispatcher[(Offset, Long)],
    dispatcher: Dispatcher[Offset],
    dispatcherLagger: DispatcherLagMeter,
)(implicit mat: Materializer, loggingContext: LoggingContext)
    extends ReadOnlySqlLedger(ledgerId, ledgerDao, contractStore, dispatcher) {

  protected val (ledgerEndUpdateKillSwitch, ledgerEndUpdateDone) =
    RestartSource
      .withBackoff(
        RestartSettings(minBackoff = 1.second, maxBackoff = 10.seconds, randomFactor = 0.2)
      )(() =>
        Source
          .tick(0.millis, 100.millis, ())
          .mapAsync(1)(_ => ledgerDao.lookupLedgerEndOffsetAndSequentialId())
      )
      .viaMat(KillSwitches.single)(Keep.right[NotUsed, UniqueKillSwitch])
      .toMat(Sink.foreach { case newLedgerHead @ (offset, _) =>
        dispatcherLagger.startTimer(offset)
        contractStateEventsDispatcher.signalNewHead(newLedgerHead)
      })(
        Keep.both[UniqueKillSwitch, Future[Done]]
      )
      .run()

  override def close(): Unit = {
    ledgerEndUpdateKillSwitch.shutdown()

    Await.ready(ledgerEndUpdateDone, 10.seconds)

    super.close()
  }
}
