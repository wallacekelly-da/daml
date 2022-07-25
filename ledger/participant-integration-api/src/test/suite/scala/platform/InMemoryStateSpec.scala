// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.platform

import com.daml.ledger.offset.Offset
import com.daml.lf.data.Ref
import com.daml.logging.LoggingContext
import com.daml.platform.store.backend.ParameterStorageBackend
import com.daml.platform.store.backend.ParameterStorageBackend.LedgerEnd
import com.daml.platform.store.cache.{
  ContractStateCaches,
  InMemoryFanoutBuffer,
  MutableLedgerEndCache,
}
import com.daml.platform.store.interning.{StringInterningView, UpdatingStringInterningView}
import org.mockito.{InOrder, Mockito, MockitoSugar}
import org.scalatest.Assertion
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Future

class InMemoryStateSpec extends AsyncFlatSpec with MockitoSugar with Matchers {
  private val className = classOf[InMemoryState].getSimpleName
  private implicit val loggingContext: LoggingContext = LoggingContext.ForTesting

  s"$className.initialized" should "return false if not initialized" in withTestFixture {
    case (inMemoryState, _, _, _, _, _, _, _) =>
      inMemoryState.initialized shouldBe false
  }

  s"$className.initializeTo" should "initialize the state" in withTestFixture {
    case (
          inMemoryState,
          mutableLedgerEndCache,
          contractStateCaches,
          inMemoryFanoutBuffer,
          stringInterningView,
          dispatcherState,
          updateStringInterningView,
          inOrder,
        ) =>
      val initOffset = Offset.fromHexString(Ref.HexString.assertFromString("abcdef"))
      val initEventSequentialId = 1337L
      val initStringInterningId = 17

      val initLedgerEnd = ParameterStorageBackend
        .LedgerEnd(initOffset, initEventSequentialId, initStringInterningId)

      when(updateStringInterningView(stringInterningView, initLedgerEnd)).thenReturn(Future.unit)
      when(dispatcherState.stopDispatcher()).thenReturn(Future.unit)

      for {
        // INITIALIZED THE STATE
        _ <- inMemoryState.initializeTo(initLedgerEnd)(updateStringInterningView)
      } yield {
        // ASSERT STATE INITIALIZED

        inOrder.verify(dispatcherState).stopDispatcher()
        inOrder.verify(updateStringInterningView)(stringInterningView, initLedgerEnd)
        inOrder.verify(contractStateCaches).reset(initOffset)
        inOrder.verify(inMemoryFanoutBuffer).flush()
        inOrder.verify(mutableLedgerEndCache).set(initOffset, initEventSequentialId)
        inOrder.verify(dispatcherState).startDispatcher(initLedgerEnd)

        inMemoryState.initialized shouldBe true
      }
  }

  private def withTestFixture(
      test: (
          InMemoryState,
          MutableLedgerEndCache,
          ContractStateCaches,
          InMemoryFanoutBuffer,
          StringInterningView,
          DispatcherState,
          (UpdatingStringInterningView, LedgerEnd) => Future[Unit],
          InOrder,
      ) => Future[Assertion]
  ): Future[Assertion] = {
    val mutableLedgerEndCache = mock[MutableLedgerEndCache]
    val contractStateCaches = mock[ContractStateCaches]
    val inMemoryFanoutBuffer = mock[InMemoryFanoutBuffer]
    val stringInterningView = mock[StringInterningView]
    val dispatcherState = mock[DispatcherState]
    val updateStringInterningView = mock[(UpdatingStringInterningView, LedgerEnd) => Future[Unit]]

    // Mocks should be called in the asserted order
    val inOrderMockCalls = Mockito.inOrder(
      mutableLedgerEndCache,
      contractStateCaches,
      inMemoryFanoutBuffer,
      stringInterningView,
      dispatcherState,
      updateStringInterningView,
    )

    val inMemoryState = new InMemoryState(
      ledgerEndCache = mutableLedgerEndCache,
      contractStateCaches = contractStateCaches,
      inMemoryFanoutBuffer = inMemoryFanoutBuffer,
      stringInterningView = stringInterningView,
      dispatcherState = dispatcherState,
    )

    test(
      inMemoryState,
      mutableLedgerEndCache,
      contractStateCaches,
      inMemoryFanoutBuffer,
      stringInterningView,
      dispatcherState,
      updateStringInterningView,
      inOrderMockCalls,
    )
  }
}
