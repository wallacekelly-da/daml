package com.daml.platform.store.appendonlydao

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.daml.ledger.participant.state.v1.Offset
import com.daml.metrics.{Metrics, Timed}
import com.daml.platform.store.appendonlydao.events.BufferedTransactionsReader.Transaction
import com.daml.scalautil.Statement.discard

import scala.collection.mutable

class TransactionsBuffer(maxTransactions: Int, metrics: Metrics) {
  private val buffer = mutable.Queue.empty[(Offset, Transaction)]

  def push(offset: Offset, transaction: Transaction): Unit = buffer.synchronized {
    Timed.value(
      metrics.daml.index.bufferPushTransaction, {
        discard {
          {
            if (buffer.size == maxTransactions) {
              metrics.daml.index.bufferSize.dec()
              discard(buffer.dequeue())
              buffer
            } else buffer
          }.enqueue {
            metrics.daml.index.bufferSize.inc()
            offset -> transaction
          }
        }
      },
    )
  }

  def getTransactions(
      startExclusive: Offset,
      endInclusive: Offset,
  ): ((Offset, Offset), Source[(Offset, Transaction), NotUsed]) =
    buffer.synchronized {
      buffer.headOption
        .map {
          case (bufferStartExclusive, _) if bufferStartExclusive >= endInclusive =>
            (startExclusive, startExclusive) -> Source.empty
          case _ =>
            var bufferedStartExclusive = startExclusive
            var bufferedEndInclusive = startExclusive

            Timed.value(
              metrics.daml.index.bufferGetTransactions, {
                val slice = buffer
                  .dropWhile { case (offset, _) =>
                    offset <= startExclusive && {
                      bufferedStartExclusive = offset
                      bufferedEndInclusive = offset
                      true
                    }
                  }
                  .takeWhile { case (offset, _) =>
                    offset <= endInclusive && {
                      bufferedEndInclusive = offset
                      true
                    }
                  }

                val source = Source.fromIterator(() => slice.iterator)
                (bufferedStartExclusive, bufferedEndInclusive) -> source
              },
            )
        }
        .getOrElse((startExclusive, startExclusive) -> Source.empty)
    }
}
