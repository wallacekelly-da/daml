// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.platform.store.cache

import akka.Done
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import com.codahale.metrics.MetricRegistry
import com.daml.ledger.offset.Offset
import com.daml.lf.crypto.Hash
import com.daml.lf.data.{Ref, Time}
import com.daml.lf.transaction.GlobalKey
import com.daml.lf.transaction.test.TransactionBuilder
import com.daml.lf.value.Value
import com.daml.lf.value.Value.{ContractInstance, ValueInt64, VersionedValue}
import com.daml.logging.LoggingContext
import com.daml.metrics.Metrics
import com.daml.platform.store.EventSequentialId
import com.daml.platform.store.appendonlydao.events.ContractStateEvent
import com.daml.platform.store.cache.MutableCacheBackedContractStore.EventSequentialId
import com.daml.platform.store.cache.MutableCacheBackedContractStoreRaceTests.{
  IndexViewContractsReader,
  buildContractStore,
  generateWorkload,
  test,
}
import com.daml.platform.store.interfaces.LedgerDaoContractsReader
import com.daml.platform.store.interfaces.LedgerDaoContractsReader._
import org.mockito.MockitoSugar
import org.scalatest.Assertions.fail
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.Eventually
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import java.util.concurrent.Executors
import scala.annotation.tailrec
import scala.collection.Searching
import scala.collection.immutable.VectorMap
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.Random

class MutableCacheBackedContractStoreRaceTests
    extends AsyncFlatSpec
    with Matchers
    with Eventually
    with MockitoSugar
    with BeforeAndAfterAll {
  behavior of "Mutable state cache updates"

  private val actorSystem = ActorSystem()
  private implicit val materializer: Materializer = Materializer(actorSystem)

  it should "preserve causal monotonicity under contention" in {
    val unboundedExecutionContext = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())

    val workload = generateWorkload(keysCount = 10L, contractsCount = 10L)
    val indexViewContractsReader = IndexViewContractsReader()(unboundedExecutionContext)
    val contractStore = buildContractStore(indexViewContractsReader, unboundedExecutionContext)

    for {
      _ <- test(
        indexViewContractsReader = indexViewContractsReader,
        contractStore = contractStore,
        workload = workload,
        unboundedExecutionContext = unboundedExecutionContext,
      )
    } yield succeed
  }

  override def afterAll(): Unit = {
    Await.ready(actorSystem.terminate(), 10.seconds)
    materializer.shutdown()
  }
}

private object MutableCacheBackedContractStoreRaceTests {
  private implicit val loggingContext: LoggingContext = LoggingContext.ForTesting
  private val stakeholders = Set(Ref.Party.assertFromString("some-stakeholder"))

  private def test(
      indexViewContractsReader: IndexViewContractsReader,
      contractStore: MutableCacheBackedContractStore,
      workload: Seq[EventSequentialId => SimplifiedContractStateEvent],
      unboundedExecutionContext: ExecutionContext,
  )(implicit materializer: Materializer): Future[Done] = {
    implicit val ec: ExecutionContext = unboundedExecutionContext

    Source
      .fromIterator(() => workload.iterator)
      .statefulMapConcat { () =>
        var eventSequentialId = 0L

        eventCtor => {
          eventSequentialId += 1
          Iterator(eventCtor(eventSequentialId))
        }
      }
      .map(event => {
        indexViewContractsReader.update(event)
        event
      })
      .mapAsync(1)(assertKeyState(indexViewContractsReader, contractStore))
      .run()
  }

  private def assertKeyState(
      indexViewContractsReader: IndexViewContractsReader,
      contractStore: MutableCacheBackedContractStore,
  )(event: SimplifiedContractStateEvent)(implicit ec: ExecutionContext): Future[Unit] = {
    val contractStateEvent = toContractStateEvent(event)

    // Validate the view's contents (not the purpose of the test, but only to assert no bugs)
    assertIndexState(indexViewContractsReader, event).flatMap { _ =>
      // Start async key lookup
      val lookupF = Future.delegate(contractStore.lookupContractKey(stakeholders, event.key))
      // Update the mutable contract state cache synchronously
      contractStore.push(contractStateEvent)
      // Dispatch a second async lookup after the first one finished
      lookupF
        .flatMap(_ => contractStore.lookupContractKey(stakeholders, event.key))
        // Assert that the contract store returns the state as seen after the last event update
        .map {
          case Some(contractId) if (event.contractId != contractId) || !event.created =>
            fail(message =
              s"Key state corruption for ${event.key}: " +
                s"expected ${if (event.created) s"assignment to ${event.contractId} -> ${event.contract}"
                else "unassigned"}, " +
                s"but got assignment to $contractId"
            )
          case None if event.created =>
            fail(message =
              s"Key state corruption for ${event.key}: expected assignment to ${event.contractId} -> ${event.contract} " +
                "but got unassigned instead"
            )
          case _ => ()
        }
    }
  }

  private def assertIndexState(
      indexViewContractsReader: IndexViewContractsReader,
      event: SimplifiedContractStateEvent,
  )(implicit ec: ExecutionContext) =
    indexViewContractsReader.lookupKeyState(event.key, event.eventSequentialId).map {
      case KeyAssigned(contractId, _) if contractId == event.contractId && event.created =>
      case KeyUnassigned if !event.created =>
      case actual =>
        fail(s"Test bug: actual $actual after event $event: index view: ${indexViewContractsReader.keyStateStore
          .get(event.key)}")
    }

  private def generateWorkload(
      keysCount: Long,
      contractsCount: Long,
  ): Seq[EventSequentialId => SimplifiedContractStateEvent] = {
    val keys = (0L until keysCount).map { keyIdx =>
      keyIdx -> GlobalKey(Identifier.assertFromString("pkgId:module:entity"), ValueInt64(keyIdx))
    }.toMap

    val keysToContracts = keys.map { case (keyIdx, key) =>
      val contractLifecyclesForKey = contractsCount / keysCount
      key -> (0L until contractLifecyclesForKey)
        .map { contractIdx =>
          val globalContractIdx = keyIdx * contractLifecyclesForKey + contractIdx
          val contractId = ContractId.V1(Hash.hashPrivateKey(globalContractIdx.toString))
          val contractRef = contract(globalContractIdx)
          (contractId, contractRef)
        }
        .foldLeft(VectorMap.empty[ContractId, Contract]) { case (r, (k, v)) =>
          r.updated(k, v)
        }
    }

    val updates =
      keysToContracts.map { case (key, contracts) =>
        contracts.flatMap { case (contractId, contractRef) =>
          Vector(
            (eventSeqId: EventSequentialId) =>
              SimplifiedContractStateEvent(
                eventSequentialId = eventSeqId,
                contractId = contractId,
                contract = contractRef,
                created = true,
                key = key,
              ),
            (eventSeqId: EventSequentialId) =>
              SimplifiedContractStateEvent(
                eventSequentialId = eventSeqId,
                contractId = contractId,
                contract = contractRef,
                created = false,
                key = key,
              ),
          )
        }
      }

    interleaveRandom(updates)
  }

  private def interleaveRandom(
      indexContractsUpdates: Iterable[Iterable[EventSequentialId => SimplifiedContractStateEvent]]
  ): Seq[EventSequentialId => SimplifiedContractStateEvent] = {
    @tailrec
    def interleaveIteratorsRandom[T](acc: Vector[T], col: Set[Iterator[T]]): Vector[T] =
      if (col.isEmpty) acc
      else {
        val vCol = col.toVector
        val randomIteratorIndex = Random.nextInt(vCol.size)
        val targetIterator = vCol(randomIteratorIndex)
        if (targetIterator.hasNext) interleaveIteratorsRandom(acc :+ targetIterator.next(), col)
        else interleaveIteratorsRandom(acc, col - targetIterator)
      }

    interleaveIteratorsRandom(
      Vector.empty[EventSequentialId => SimplifiedContractStateEvent],
      indexContractsUpdates.map(_.iterator).toSet,
    )
  }

  final case class ContractLifecycle(
      contractId: ContractId,
      contract: Contract,
      createdAt: Long,
      archivedAt: Option[Long],
  )

  final case class SimplifiedContractStateEvent(
      eventSequentialId: EventSequentialId,
      contractId: ContractId,
      contract: Contract,
      created: Boolean,
      key: GlobalKey,
  )

  private def contract(idx: Long): Contract = {
    val templateId = Identifier.assertFromString(s"somePackage:someModule:someEntity")
    val contractArgument = Value.ValueInt64(idx)
    val contractInstance = ContractInstance(templateId, contractArgument, "some agreement")
    TransactionBuilder().versionContract(contractInstance)
  }

  private def buildContractStore(
      indexViewContractsReader: IndexViewContractsReader,
      ec: ExecutionContext,
  ) = {
    val ledgerEndCache = MutableLedgerEndCache()
    ledgerEndCache.set(Offset.beforeBegin -> EventSequentialId.beforeBegin)
    MutableCacheBackedContractStore(
      contractsReader = indexViewContractsReader,
      signalNewLedgerHead = (offset, seqId) => ledgerEndCache.set(offset -> seqId),
      ledgerEndCache = ledgerEndCache,
      metrics = new Metrics(new MetricRegistry),
      maxContractsCacheSize = 1L,
      maxKeyCacheSize = 1L,
    )(ec, loggingContext)
  }

  private val toContractStateEvent: SimplifiedContractStateEvent => ContractStateEvent = {
    case SimplifiedContractStateEvent(eventSequentialId, contractId, contract, created, key) =>
      if (created)
        ContractStateEvent.Created(
          contractId = contractId,
          contract = contract,
          globalKey = Some(key),
          ledgerEffectiveTime = Time.Timestamp.MinValue, // Not used
          stakeholders = stakeholders, // Not used
          eventOffset = Offset.beforeBegin, // Not used
          eventSequentialId = eventSequentialId,
        )
      else
        ContractStateEvent.Archived(
          contractId = contractId,
          globalKey = Some(key),
          stakeholders = stakeholders, // Not used
          eventOffset = Offset.beforeBegin, // Not used
          eventSequentialId = eventSequentialId,
        )
  }

  private case class IndexViewContractsReader()(implicit ec: ExecutionContext)
      extends LedgerDaoContractsReader {
    var contractStateStore = Map.empty[ContractId, ContractLifecycle]
    var keyStateStore = Map.empty[Key, Vector[ContractLifecycle]]

    def update(event: SimplifiedContractStateEvent): Unit = synchronized {
      if (event.created) {
        // On create
        val newContractLifecycle = ContractLifecycle(
          contractId = event.contractId,
          contract = event.contract,
          createdAt = event.eventSequentialId,
          archivedAt = None,
        )
        contractStateStore = contractStateStore.updatedWith(event.contractId) {
          case None => Some(newContractLifecycle)
          case Some(_) =>
            throw InvalidUpdateException(s"Already created for contract id: ${event.contractId}")
        }
        keyStateStore = keyStateStore.updatedWith(event.key) {
          case None =>
            Some(Vector(newContractLifecycle))
          case Some(stateTransitions) =>
            stateTransitions.last match {
              case ContractLifecycle(_, _, _, Some(archivedAt)) =>
                if (archivedAt < event.eventSequentialId)
                  Some(stateTransitions :+ newContractLifecycle)
                else
                  throw InvalidUpdateException(
                    s"Key state span conflict: $archivedAt vs ${event.eventSequentialId}"
                  )
              case lastState @ ContractLifecycle(_, _, createdAt, None) =>
                if (createdAt < event.eventSequentialId) {
                  Some(
                    stateTransitions.init :+ lastState.copy(archivedAt =
                      Some(event.eventSequentialId)
                    )
                  )
                } else
                  throw InvalidUpdateException(
                    s"Key state span conflict: $createdAt vs ${event.eventSequentialId}"
                  )
            }
        }
      } else {
        // On archive
        contractStateStore = contractStateStore.updatedWith(event.contractId) {
          case None =>
            throw InvalidUpdateException(s"You cannot archive a non-existing contract")
          case Some(ContractLifecycle(_, _, _, Some(_))) =>
            throw InvalidUpdateException(s"You cannot archive an archived contract")
          case Some(ContractLifecycle(_, _, createdAt, None))
              if createdAt >= event.eventSequentialId =>
            throw InvalidUpdateException("You cannot archive before a create")
          case Some(contractLifecycle @ ContractLifecycle(_, _, _, None)) =>
            Some(contractLifecycle.copy(archivedAt = Some(event.eventSequentialId)))
        }
        keyStateStore = keyStateStore.updatedWith(event.key) {
          case None => throw InvalidUpdateException("You cannot un-assign a non-existing key")
          case Some(stateTransitions) =>
            stateTransitions.last match {
              case ContractLifecycle(_, _, _, Some(_)) =>
                throw InvalidUpdateException(s"You cannot un-assign an unassigned key")
              case ContractLifecycle(_, _, createdAt, None)
                  if createdAt >= event.eventSequentialId =>
                throw InvalidUpdateException(
                  s"You cannot un-assign a key for a contract at or before its create"
                )
              case contractLifecycle @ ContractLifecycle(_, _, _, None) =>
                Some(
                  stateTransitions.init :+ contractLifecycle.copy(archivedAt =
                    Some(event.eventSequentialId)
                  )
                )
            }
        }
      }
    }

    override def lookupContractState(contractId: ContractId, validAt: Long)(implicit
        loggingContext: LoggingContext
    ): Future[Option[ContractState]] =
      Future {
        val _ = loggingContext
        contractStateStore
          .get(contractId)
          .flatMap { case ContractLifecycle(_, contract, createdAt, maybeArchivedAt) =>
            if (validAt < createdAt) None
            else if (maybeArchivedAt.forall(_ > validAt))
              Some(ActiveContract(contract, stakeholders, Time.Timestamp.MinValue))
            else Some(ArchivedContract(stakeholders))
          }
      }(ec)

    override def lookupKeyState(key: Key, validAt: Long)(implicit
        loggingContext: LoggingContext
    ): Future[KeyState] = Future {
      val _ = loggingContext
      keyStateStore
        .get(key)
        .map { stateTransitionsVector =>
          // We can search since we guarantee order at update
          stateTransitionsVector.view.map(_.createdAt).search(validAt) match {
            case Searching.Found(foundIndex) =>
              val state = stateTransitionsVector(foundIndex)
              KeyAssigned(state.contractId, stakeholders)
            case Searching.InsertionPoint(insertionPoint) =>
              if (insertionPoint == 0) KeyUnassigned
              else {
                val state = stateTransitionsVector(insertionPoint - 1)
                state.archivedAt match {
                  case Some(archivedAt) if archivedAt <= validAt => KeyUnassigned
                  case Some(_) => KeyAssigned(state.contractId, stakeholders)
                  case None => KeyUnassigned
                }
              }
          }
        }
        .getOrElse(KeyUnassigned)
    }(ec)

    override def lookupActiveContractAndLoadArgument(readers: Set[Party], contractId: ContractId)(
        implicit loggingContext: LoggingContext
    ): Future[Option[Contract]] = {
      val _ = (loggingContext, readers, contractId)
      // Needs to return None for divulgence lookups
      Future.successful(None)
    }

    override def lookupActiveContractWithCachedArgument(
        readers: Set[Party],
        contractId: ContractId,
        createArgument: VersionedValue,
    )(implicit loggingContext: LoggingContext): Future[Option[Contract]] = {
      val _ = (loggingContext, readers, contractId, createArgument)
      // Needs to return None for divulgence lookups
      Future.successful(None)
    }
  }

  private case class InvalidUpdateException(cause: String) extends RuntimeException(cause)
}
