// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.ledger.api.testtool.suites

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import com.daml.ledger.api.testtool.infrastructure.Allocation._
import com.daml.ledger.api.testtool.infrastructure.LedgerTestSuite
import com.daml.ledger.api.testtool.infrastructure.participant.ParticipantTestContext
import com.daml.ledger.client.binding.Primitive
import com.daml.ledger.test.model.Foo.Foo1

import java.nio.charset.StandardCharsets
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

final class SubmitAndWaitBenchIT extends LedgerTestSuite {
  test("SAWBench", "Creates multiple", allocate(Parties(2)), timeoutScale = 4.0) { implicit ec =>
    { case Participants(Participant(ledger, alice, bob)) =>
      val times = 1000
      val noCommands = 100
      val akkaSystem = ActorSystem()
      implicit val materializer: Materializer = Materializer(akkaSystem)
      timedFuture("whole thing") {
        Source
          .fromIterator(() => (1 to times).iterator)
          .mapAsync(8) { _ =>
            createTx(ledger, alice, bob, noCommands)
          }
          .run()
          .flatMap(_ => akkaSystem.terminate().map(_ => ()))
          .recoverWith { case NonFatal(e) =>
            println(e)
            akkaSystem.terminate().map { t =>
              println(t)
              ()
            }
          }
      }
    }
  }

  private def createTx(
      ledger: ParticipantTestContext,
      alice: Primitive.Party,
      bob: Primitive.Party,
      noCommands: Int,
  )(implicit ec: ExecutionContext) = timedFuture(s"$noCommands commands") {
    val commands = (1 to noCommands).map(_ => Foo1(alice, Seq(bob), randomPayload(1000)))
    ledger
      .createMultiple(
        actAs = List(alice),
        readAs = List.empty,
        commands,
      )
      .map(_ => ())
  }

  private def timedFuture[T](what: String)(f: => Future[T])(implicit ec: ExecutionContext) = {
    val start = System.nanoTime()
    f.map { r =>
      println(s"$what took ${(System.nanoTime() - start) / 1000000} millis")
      r
    }
  }

  private def randomPayload(sizeBytes: Int): String =
    new String(RandomnessProvider.randomBytes(sizeBytes), StandardCharsets.UTF_8)

  object RandomnessProvider {
    private val r = new scala.util.Random(System.currentTimeMillis())
    def randomBytes(n: Int): Array[Byte] = {
      val arr = Array.ofDim[Byte](n)
      r.nextBytes(arr)
      arr
    }
  }
}
