package com.daml.platform.store.appendonlydao.events

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import com.codahale.metrics.MetricRegistry
import com.daml.ledger.api.v1.active_contracts_service.GetActiveContractsResponse
import com.daml.ledger.api.v1.transaction_service.{
  GetFlatTransactionResponse,
  GetTransactionResponse,
  GetTransactionTreesResponse,
  GetTransactionsResponse,
}
import com.daml.ledger.participant.state.v1.{Offset, TransactionId}
import com.daml.lf.data.Ref
import com.daml.logging.LoggingContext
import com.daml.metrics.Metrics
import com.daml.platform.store.appendonlydao.events.BufferedTransactionsReaderSpec.{
  apiFlatTx,
  delegateBuilder,
}
import com.daml.platform.store.cache.EventsBuffer
import com.daml.platform.store.dao.LedgerDaoTransactionsReader
import com.daml.platform.store.dao.events.ContractStateEvent
import com.daml.platform.store.interfaces.TransactionLogUpdate
import org.mockito.MockitoSugar
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

import scala.concurrent.Future

class BufferedTransactionsReaderSpec extends AsyncWordSpec with MockitoSugar with Matchers {
  private implicit val loggingContext: LoggingContext = LoggingContext.ForTesting

  private val actorSystem = ActorSystem()
  private implicit val materializer: Materializer = Materializer(actorSystem)

  "getFlatTransactions" should {
    "serve flat transactions correctly" in {
      val metrics = new Metrics(new MetricRegistry())
      val Seq(offset1, offset2, offset3) =
        (1 to 3).map(idx => Offset.fromByteArray(BigInt(idx + 1234).toByteArray))

      val getTransactionsResponseMock = apiFlatTx("some tx")

      val delegate = delegateBuilder(flatTxs =
        Map(
          (offset1, offset2, Map.empty[Party, Set[Ref.Identifier]], false) -> Iterator(
            offset3 -> getTransactionsResponseMock
          )
        )
      )

      val bufferedTransactionsReader = new BufferedTransactionsReader(
        delegate = delegate,
        transactionsBuffer = new EventsBuffer[Offset, TransactionLogUpdate](
          maxBufferSize = 100L,
          metrics = metrics,
          bufferQualifier = "test",
          isRangeEndMarker = _.isInstanceOf[TransactionLogUpdate.LedgerEndMarker],
        ),
        toFlatTransaction = { case wrong =>
          throw new RuntimeException(s"Non-matching $wrong")
        },
        toTransactionTree = { case wrong =>
          throw new RuntimeException(s"Non-matching $wrong")
        },
        metrics = metrics,
      )

      bufferedTransactionsReader
        .getFlatTransactions(offset1, offset2, Map.empty, verbose = false)
        .runWith(Sink.seq)
        .flatMap(
          _ should contain theSameElementsInOrderAs Seq(offset3 -> getTransactionsResponseMock)
        )
    }
  }

//  "getTransactions" when {
//    "buffer empty" should {
//      "fetch from buffer" in {
//        BufferedTransactionsReader.getTransactions()
//      }
//    }
//  }
}

object BufferedTransactionsReaderSpec {
  private def apiFlatTx(discriminator: String) =
    new GetTransactionsResponse(
      transactions = Seq(
        com.daml.ledger.api.v1.transaction.Transaction(
          transactionId = discriminator,
          commandId = "",
          workflowId = "",
          effectiveAt = None,
          events = Seq.empty,
          offset = "",
          traceContext = None,
        )
      )
    )

  private def delegateBuilder(
      flatTxs: Map[(Offset, Offset, FilterRelation, Boolean), Iterator[
        (Offset, GetTransactionsResponse)
      ]],
      txTrees: Map[(Offset, Offset, Set[Party], Boolean), Iterator[
        (Offset, GetTransactionTreesResponse)
      ]] = Map.empty,
  ): LedgerDaoTransactionsReader =
    new LedgerDaoTransactionsReader {
      override def getTransactionTrees(
          startExclusive: Offset,
          endInclusive: Offset,
          requestingParties: Set[Party],
          verbose: Boolean,
      )(implicit
          loggingContext: LoggingContext
      ): Source[(Offset, GetTransactionTreesResponse), NotUsed] =
        Source.fromIterator(() =>
          txTrees((startExclusive, endInclusive, requestingParties, verbose))
        )

      override def getFlatTransactions(
          startExclusive: Offset,
          endInclusive: Offset,
          filter: FilterRelation,
          verbose: Boolean,
      )(implicit
          loggingContext: LoggingContext
      ): Source[(Offset, GetTransactionsResponse), NotUsed] =
        Source.fromIterator(() => flatTxs((startExclusive, endInclusive, filter, verbose)))

      override def lookupFlatTransactionById(
          transactionId: TransactionId,
          requestingParties: Set[Party],
      )(implicit loggingContext: LoggingContext): Future[Option[GetFlatTransactionResponse]] = ???

      override def getTransactionLogUpdates(
          startExclusive: (Offset, Long),
          endInclusive: (Offset, Long),
      )(implicit
          loggingContext: LoggingContext
      ): Source[((Offset, Long), TransactionLogUpdate), NotUsed] = ???

      override def lookupTransactionTreeById(
          transactionId: TransactionId,
          requestingParties: Set[Party],
      )(implicit loggingContext: LoggingContext): Future[Option[GetTransactionResponse]] = ???

      override def getActiveContracts(activeAt: Offset, filter: FilterRelation, verbose: Boolean)(
          implicit loggingContext: LoggingContext
      ): Source[GetActiveContractsResponse, NotUsed] = ???

      override def getContractStateEvents(
          startExclusive: (Offset, Long),
          endInclusive: (Offset, Long),
      )(implicit
          loggingContext: LoggingContext
      ): Source[((Offset, Long), ContractStateEvent), NotUsed] = ???
    }
}
