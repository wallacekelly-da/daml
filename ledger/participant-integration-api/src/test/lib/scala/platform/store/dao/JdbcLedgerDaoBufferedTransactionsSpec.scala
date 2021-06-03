// Copyright (c) 2021 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.platform.store.dao

import akka.NotUsed
import akka.stream.scaladsl.{Sink, Source}
import com.codahale.metrics.MetricRegistry
import com.daml.ledger.api.v1.transaction.TransactionTree
import com.daml.ledger.api.v1.transaction_service.GetTransactionTreesResponse
import com.daml.ledger.participant.state.v1.{Offset, Party}
import com.daml.lf.engine.{Engine, ValueEnricher}
import com.daml.lf.transaction.Node.KeyWithMaintainers
import com.daml.metrics.Metrics
import com.daml.platform.api.v1.event.EventOps.TreeEventOps
import com.daml.platform.store.LfValueTranslationCache
import com.daml.platform.store.appendonlydao.events.{BufferedTransactionsReader, LfValueTranslation}
import com.daml.platform.store.cache.EventsBuffer
import com.daml.platform.store.cache.MutableCacheBackedContractStore.EventSequentialId
import com.daml.platform.store.interfaces.TransactionLogUpdate
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{Inside, LoneElement, OptionValues}

import scala.concurrent.Future

private[dao] trait JdbcLedgerDaoBufferedTransactionsSpec
    extends OptionValues
    with Inside
    with LoneElement {
  this: AsyncFlatSpec with Matchers with JdbcLedgerDaoSuite =>

  behavior of "BufferedTransactionsReader"

  it should "return the same transaction trees as the " in {
    for {
      bufferedStartExclusive <- ledgerDao.lookupLedgerEndOffsetAndSequentialId()
      _ <- store(fullyTransient)
      (from, to) <- storeTestFixture()
      expected <- transactionsOf(
        getTransactionTrees(ledgerDao.transactionsReader)(from._1, to._1)
      )
      actual <- withBufferedTransactionReader(bufferedStartExclusive, to) {
        (bufferedTransactionsReader, _) =>
          transactionsOf(
            getTransactionTrees(bufferedTransactionsReader)(from._1, to._1)
          )
      }
    } yield {
      comparable(actual) should contain theSameElementsInOrderAs comparable(expected)
    }
  }

  private def getTransactionTrees(reader: LedgerDaoTransactionsReader)(
      startExclusive: Offset,
      endInclusive: Offset,
      requestingParties: Set[Party] = Set(alice, bob),
      verbose: Boolean = false,
  ) =
    reader.getTransactionTrees(startExclusive, endInclusive, requestingParties, verbose)

  private def storeTestFixture() =
    for {
      from <- ledgerDao.lookupLedgerEndOffsetAndSequentialId()
      _ <- store(singleCreate)
      // requesting parties are from within the stakeholders
      _ <- store(
        singleCreate(
          createNode(
            _,
            Set(bob),
            Set(alice, bob, charlie),
            Some(KeyWithMaintainers(someContractKey(bob, "some key"), Set(bob))),
            someTemplateId,
            someContractArgument,
          )
        )
      )
      // the requesters are not stakeholders
      (_, created) <- store(
        singleCreate(
          createNode(
            _,
            Set(charlie),
            Set(charlie),
            None,
            someTemplateId,
            someContractArgument,
          )
        )
      )
      _ <- store(singleExercise(nonTransient(created).loneElement))
      _ <- store(fullyTransient)
      _ <- store(multiPartySingleCreate)
      to <- ledgerDao.lookupLedgerEndOffsetAndSequentialId()
    } yield (from, to)

  private def transactionsOf(
      source: Source[(Offset, GetTransactionTreesResponse), NotUsed]
  ): Future[Seq[TransactionTree]] =
    source
      .map(_._2)
      .runWith(Sink.seq)
      .map(_.flatMap(_.transactions))

  // Ensure two sequences of transaction trees are comparable:
  // - witnesses do not have to appear in a specific order
  private def comparable(txs: Seq[TransactionTree]): Seq[TransactionTree] =
    txs.map(tx =>
      tx.copy(eventsById = tx.eventsById.view.mapValues(_.modifyWitnessParties(_.sorted)).toMap)
    )

  private def withBufferedTransactionReader[T](
      startExclusive: (Offset, EventSequentialId),
      endInclusive: (Offset, EventSequentialId),
  )(
      test: (BufferedTransactionsReader, EventsBuffer[Offset, TransactionLogUpdate]) => Future[T]
  ) = {
    val metrics = new Metrics(new MetricRegistry)
    val transactionsBuffer = new EventsBuffer[Offset, TransactionLogUpdate](
      10000,
      metrics,
      "test buffer",
      _.isInstanceOf[TransactionLogUpdate.LedgerEndMarker], // Signifies ledger end
    )
    val bufferedTransactionsReader = new BufferedTransactionsReader(
      delegate = null,
      lfValueTranslation = new LfValueTranslation(
        cache = LfValueTranslationCache.Cache.none,
        metrics = metrics,
        enricherO = Some(new ValueEnricher(new Engine())),
        loadPackage = (packageId, loggingContext) =>
          ledgerDao.getLfArchive(packageId)(loggingContext),
      ),
      transactionsBuffer = transactionsBuffer,
      metrics = metrics,
    )(scala.concurrent.ExecutionContext.global)

    for {
      _ <- ledgerDao.transactionsReader
        .getTransactionLogUpdates(startExclusive, endInclusive)
        .runForeach(el => transactionsBuffer.push(el._1._1, el._2))
      r <- test(bufferedTransactionsReader, transactionsBuffer)
    } yield r
  }
}
