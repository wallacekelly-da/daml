package com.daml.platform.store.appendonlydao

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.daml.ledger.participant.state.v1.Offset
import com.daml.metrics.{Metrics, Timed}
import com.daml.platform.store.appendonlydao.events.BufferedTransactionsReader
import com.daml.platform.store.appendonlydao.events.BufferedTransactionsReader.{
  Transaction,
  TransactionEvent,
}

class TransactionsBuffer(maxTransactions: Int, metrics: Metrics) {
  private var buffer = Vector.empty[(Offset, TransactionEvent)]

  def push(offset: Offset, transaction: TransactionEvent): Unit = Timed.value(
    metrics.daml.index.bufferPushTransaction,
    synchronized {
      buffer = {
        if (buffer.size == maxTransactions) {
          buffer.drop(1)
        } else buffer
      } :+ {
        metrics.daml.index.bufferSize.inc()
        offset -> transaction
      }
    },
  )

  def getTransactions(
      startExclusive: Offset,
      endInclusive: Offset,
  ): ((Offset, Offset), Source[(Offset, Transaction), NotUsed]) = Timed.value(
    metrics.daml.index.bufferGetTransactions,
    synchronized {
      buffer.headOption
        .map {
          case (bufferStartExclusive, _) if bufferStartExclusive >= endInclusive =>
            (startExclusive, startExclusive) -> Source.empty
          case _ =>
            var bufferedStartExclusive = startExclusive
            var bufferedEndInclusive = startExclusive
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
              .collect { case (offset, event: BufferedTransactionsReader.Transaction) =>
                offset -> event
              }
              .iterator

            (bufferedStartExclusive, bufferedEndInclusive) -> Source.fromIterator(() => slice)
        }
        .getOrElse((startExclusive, startExclusive) -> Source.empty)
    },
  )
}
