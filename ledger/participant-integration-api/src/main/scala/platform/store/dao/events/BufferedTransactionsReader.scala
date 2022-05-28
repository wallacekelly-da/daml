// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.platform.store.dao.events

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.codahale.metrics.InstrumentedExecutorService
import com.daml.ledger.api.v1.active_contracts_service.GetActiveContractsResponse
import com.daml.ledger.api.v1.transaction_service.{
  GetFlatTransactionResponse,
  GetTransactionResponse,
  GetTransactionTreesResponse,
  GetTransactionsResponse,
}
import com.daml.ledger.offset.Offset
import com.daml.lf.data.Ref.TransactionId
import com.daml.logging.entries.LoggingValue.OfString
import com.daml.logging.{ContextualizedLogger, LoggingContext}
import com.daml.metrics.{Metrics, Timed}
import com.daml.platform.store.cache.MutableCacheBackedContractStore.EventSequentialId
import com.daml.platform.store.cache.{BufferSlice, EventsBuffer}
import com.daml.platform.store.dao.LedgerDaoTransactionsReader
import com.daml.platform.store.dao.events.BufferedTransactionsReader.{
  getTransactions,
  invertMapping,
  scalarOffsetDiff,
}
import com.daml.platform.store.dao.events.TransactionLogUpdatesConversions.{
  ToApi,
  ToFlatTransaction,
  ToTransactionTree,
}
import com.daml.platform.store.interfaces.TransactionLogUpdate
import com.daml.platform.{FilterRelation, Identifier, Party}

import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, Future}

private[events] class BufferedTransactionsReader(
    protected val delegate: LedgerDaoTransactionsReader,
    val transactionsBuffer: EventsBuffer[TransactionLogUpdate],
    eventProcessingParallelism: Int,
    filterFlatTransactions: (
        Set[Party],
        Map[Identifier, Set[Party]],
    ) => TransactionLogUpdate.Transaction => Option[TransactionLogUpdate.Transaction],
    flatToApiTransactions: (
        FilterRelation,
        Boolean,
        LoggingContext,
    ) => ToApi[GetTransactionsResponse],
    filterTransactionTrees: Set[Party] => TransactionLogUpdate.Transaction => Option[
      TransactionLogUpdate.Transaction
    ],
    treesToApiTransactions: (
        Set[Party],
        Boolean,
        LoggingContext,
    ) => ToApi[GetTransactionTreesResponse],
    metrics: Metrics,
)(implicit executionContext: ExecutionContext)
    extends LedgerDaoTransactionsReader {
  private val logger = ContextualizedLogger.get(getClass)

  override def getFlatTransactions(
      startExclusive: Offset,
      endInclusive: Offset,
      filter: FilterRelation,
      verbose: Boolean,
  )(implicit loggingContext: LoggingContext): Source[(Offset, GetTransactionsResponse), NotUsed] = {
    logger.debug(
      s"getFlatTransactions from $startExclusive to $endInclusive (diff: ${scalarOffsetDiff(startExclusive, endInclusive)}) for $filter"
    )
    val (parties, partiesTemplates) = filter.partition(_._2.isEmpty)
    val wildcardParties = parties.keySet

    val templatesParties = invertMapping(partiesTemplates)

    val correlationId = loggingContext.entries.contents("submissionId")

    val flatTransactionsBufferMetrics =
      metrics.daml.services.index
        .BufferedReader(s"flat_transactions_${correlationId.asInstanceOf[OfString].value.take(6)}")

    getTransactions(transactionsBuffer)(
      startExclusive = startExclusive,
      endInclusive = endInclusive,
      filter = filter,
      verbose = verbose,
      metrics = metrics,
      eventProcessingParallelism = eventProcessingParallelism,
    )(
      filterEvents = filterFlatTransactions(wildcardParties, templatesParties),
      toApiTx = flatToApiTransactions(filter, verbose, loggingContext),
      fetchTransactions = delegate.getFlatTransactions(_, _, _, _)(loggingContext),
      bufferReaderMetrics = flatTransactionsBufferMetrics,
    )
  }

  override def getTransactionTrees(
      startExclusive: Offset,
      endInclusive: Offset,
      requestingParties: Set[Party],
      verbose: Boolean,
  )(implicit
      loggingContext: LoggingContext
  ): Source[(Offset, GetTransactionTreesResponse), NotUsed] = {
    logger.debug(
      s"getTransactionTrees from $startExclusive to $endInclusive (diff: ${scalarOffsetDiff(startExclusive, endInclusive)}) for $requestingParties"
    )
    val correlationId = loggingContext.entries.contents("submissionId")

    val transactionTreesBufferMetrics =
      metrics.daml.services.index
        .BufferedReader(s"transaction_trees_${correlationId.asInstanceOf[OfString].value.take(6)}")

    getTransactions(transactionsBuffer)(
      startExclusive = startExclusive,
      endInclusive = endInclusive,
      filter = requestingParties,
      verbose = verbose,
      metrics = metrics,
      eventProcessingParallelism = eventProcessingParallelism,
    )(
      filterEvents = filterTransactionTrees(requestingParties),
      toApiTx = treesToApiTransactions(requestingParties, verbose, loggingContext),
      fetchTransactions = delegate.getTransactionTrees(_, _, _, _)(loggingContext),
      bufferReaderMetrics = transactionTreesBufferMetrics,
    )
  }

  override def lookupFlatTransactionById(
      transactionId: TransactionId,
      requestingParties: Set[Party],
  )(implicit loggingContext: LoggingContext): Future[Option[GetFlatTransactionResponse]] =
    delegate.lookupFlatTransactionById(transactionId, requestingParties)

  override def lookupTransactionTreeById(
      transactionId: TransactionId,
      requestingParties: Set[Party],
  )(implicit loggingContext: LoggingContext): Future[Option[GetTransactionResponse]] =
    delegate.lookupTransactionTreeById(transactionId, requestingParties)

  override def getActiveContracts(activeAt: Offset, filter: FilterRelation, verbose: Boolean)(
      implicit loggingContext: LoggingContext
  ): Source[GetActiveContractsResponse, NotUsed] =
    delegate.getActiveContracts(activeAt, filter, verbose)

  override def getContractStateEvents(startExclusive: (Offset, Long), endInclusive: (Offset, Long))(
      implicit loggingContext: LoggingContext
  ): Source[((Offset, Long), Vector[ContractStateEvent]), NotUsed] =
    throw new UnsupportedOperationException(
      s"getContractStateEvents is not supported on ${getClass.getSimpleName}"
    )

  override def getTransactionLogUpdates(
      startExclusive: (Offset, EventSequentialId),
      endInclusive: (Offset, EventSequentialId),
  )(implicit
      loggingContext: LoggingContext
  ): Source[((Offset, EventSequentialId), TransactionLogUpdate), NotUsed] =
    throw new UnsupportedOperationException(
      s"getTransactionLogUpdates is not supported on ${getClass.getSimpleName}"
    )
}

private[platform] object BufferedTransactionsReader {
  private val logger = ContextualizedLogger.get(getClass)

  type FetchTransactions[FILTER, API_RESPONSE] =
    (Offset, Offset, FILTER, Boolean) => Source[(Offset, API_RESPONSE), NotUsed]

  def apply(
      delegate: LedgerDaoTransactionsReader,
      transactionsBuffer: EventsBuffer[TransactionLogUpdate],
      eventProcessingParallelism: Int,
      lfValueTranslation: LfValueTranslation,
      metrics: Metrics,
  ): BufferedTransactionsReader = {
    val executor = Executors.newWorkStealingPool(eventProcessingParallelism * 2)
    val bufferedTransactionsReaderEC =
      ExecutionContext.fromExecutor(
        new InstrumentedExecutorService(
          executor,
          metrics.registry,
          "daml_buffered_transactions_reader",
        )
      )
    new BufferedTransactionsReader(
      delegate = delegate,
      transactionsBuffer = transactionsBuffer,
      metrics = metrics,
      filterFlatTransactions = ToFlatTransaction.filter,
      flatToApiTransactions = ToFlatTransaction.toApiTransaction(_, _, lfValueTranslation)(
        _,
        bufferedTransactionsReaderEC,
      ),
      filterTransactionTrees = ToTransactionTree.filter,
      treesToApiTransactions = ToTransactionTree.toApiTransaction(_, _, lfValueTranslation)(
        _,
        bufferedTransactionsReaderEC,
      ),
      eventProcessingParallelism = eventProcessingParallelism,
    )(bufferedTransactionsReaderEC)
  }

  private[events] def getTransactions[FILTER, API_RESPONSE](
      transactionsBuffer: EventsBuffer[TransactionLogUpdate]
  )(
      startExclusive: Offset,
      endInclusive: Offset,
      filter: FILTER,
      verbose: Boolean,
      metrics: Metrics,
      eventProcessingParallelism: Int,
  )(
      filterEvents: TransactionLogUpdate.Transaction => Option[TransactionLogUpdate.Transaction],
      toApiTx: ToApi[API_RESPONSE],
      fetchTransactions: FetchTransactions[FILTER, API_RESPONSE],
      bufferReaderMetrics: metrics.daml.services.index.BufferedReader,
  )(implicit
      executionContext: ExecutionContext,
      loggingContext: LoggingContext,
  ): Source[(Offset, API_RESPONSE), NotUsed] = {
    val sliceFilter: TransactionLogUpdate => Option[TransactionLogUpdate.Transaction] = {
      case tx: TransactionLogUpdate.Transaction => filterEvents(tx)
      case _ => None
    }

    def bufferSource(
        bufferSlice: Vector[(Offset, TransactionLogUpdate.Transaction)]
    ) =
      if (bufferSlice.isEmpty) Source.empty
      else
        Source(bufferSlice)
          .mapAsync(eventProcessingParallelism) { case (offset, payload) =>
            bufferReaderMetrics.fetchedBuffered.inc()
            Timed.future(
              bufferReaderMetrics.conversion,
              toApiTx(payload).map(offset -> _)(ExecutionContext.parasitic),
            )
          }

    val source = Source
      .unfoldAsync(startExclusive) {
        case scannedToInclusive if scannedToInclusive < endInclusive =>
          Future {
            transactionsBuffer.slice(scannedToInclusive, endInclusive, sliceFilter) match {
              case BufferSlice.Inclusive(slice) =>
                logger.debug(
                  s"Inclusive slice from $scannedToInclusive to $endInclusive (diff ${scalarOffsetDiff(scannedToInclusive, endInclusive)})"
                )
                val sourceFromBuffer = bufferSource(slice)
                val nextChunkStartExclusive = slice.lastOption.map(_._1).getOrElse(endInclusive)
                Some(nextChunkStartExclusive -> sourceFromBuffer)

              case BufferSlice.LastBufferChunkSuffix(bufferedStartExclusive, slice) =>
                logger.debug(
                  s"LastBufferChunkSuffix slice with bufferedStartExclusive: $bufferedStartExclusive from $scannedToInclusive to $endInclusive (diff ${scalarOffsetDiff(scannedToInclusive, endInclusive)})"
                )

                val sourceFromBuffer =
                  fetchTransactions(startExclusive, bufferedStartExclusive, filter, verbose)
                    .concat(bufferSource(slice))
                Some(endInclusive -> sourceFromBuffer)
            }
          }
        case _ => Future.successful(None)
      }
      .flatMapConcat(identity)

    Timed
      .source(
        bufferReaderMetrics.fetchTimer,
        source,
        { durationNanos =>
          logger.debug(s"Took ${durationNanos / 1000000.0} millis to finish source")
        },
      )
      .map { tx =>
        bufferReaderMetrics.fetchedTotal.inc()
        tx
      }
  }

  private[events] def invertMapping(
      partiesTemplates: Map[Party, Set[Identifier]]
  ): Map[Identifier, Set[Party]] =
    partiesTemplates
      .foldLeft(Map.empty[Identifier, Set[Party]]) {
        case (templatesToParties, (party, templates)) =>
          templates.foldLeft(templatesToParties) { case (aux, templateId) =>
            aux.updatedWith(templateId) {
              case None => Some(Set(party))
              case Some(partySet) => Some(partySet + party)
            }
          }
      }

  private def scalarOffsetDiff(start: Offset, end: Offset): BigInt = {
    val startBytes = start.toByteArray
    val endBytes = end.toByteArray

    val startBigInt = if (startBytes.isEmpty) BigInt(0L) else BigInt(start.toByteArray)
    val endBigInt = if (endBytes.isEmpty) BigInt(0L) else BigInt(end.toByteArray)
    endBigInt - startBigInt
  }
}
