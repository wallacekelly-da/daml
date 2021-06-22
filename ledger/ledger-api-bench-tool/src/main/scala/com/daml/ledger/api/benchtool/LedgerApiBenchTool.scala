// Copyright (c) 2021 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.ledger.api.benchtool

import java.util.concurrent.{TimeUnit => JTimeUnit, _}

import com.daml.ledger.api.benchtool.metrics.{
  MetricRegistryOwner,
  MetricsCollector,
  MetricsSet,
  StreamMetrics,
}
import com.daml.ledger.api.benchtool.services.{
  ActiveContractsService,
  CommandCompletionService,
  LedgerIdentityService,
  TransactionService,
}
import com.daml.ledger.api.benchtool.util.TypedActorSystemResourceOwner
import com.daml.ledger.api.tls.TlsConfiguration
import com.daml.ledger.resources.{ResourceContext, ResourceOwner}
import io.grpc.Channel
import io.grpc.netty.{NegotiationType, NettyChannelBuilder}
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

object LedgerApiBenchTool {
  def main(args: Array[String]): Unit = {
    Cli.config(args) match {
      case Some(config) =>
        val benchmark = runBenchmark(config)(ExecutionContext.Implicits.global)
          .recover { case ex =>
            println(s"Error: ${ex.getMessage}")
            sys.exit(1)
          }(scala.concurrent.ExecutionContext.Implicits.global)
        Await.result(benchmark, atMost = Duration.Inf)
        ()
      case _ =>
        logger.error("Invalid configuration arguments.")
    }
  }

  private def runBenchmark(config: Config)(implicit ec: ExecutionContext): Future[Unit] = {
    val printer = pprint.PPrinter(200, 1000)
    logger.info(s"Starting benchmark with configuration:\n${printer(config).toString()}")

    implicit val resourceContext: ResourceContext = ResourceContext(ec)

    val perpetualStreamsEc = ExecutionContext.fromExecutor(Executors.newCachedThreadPool())

    val resources = for {
      executorService <- threadPoolExecutorOwner(config.concurrency)
      channel <- channelOwner(config.ledger, config.tls, executorService)
      system <- TypedActorSystemResourceOwner.owner()
      registry <- new MetricRegistryOwner(
        reporter = config.metricsReporter,
        reportingInterval = config.reportingPeriod,
        logger = logger,
      )
    } yield (channel, system, registry)

    resources.use { case (channel, system, registry) =>
      val ledgerIdentityService: LedgerIdentityService = new LedgerIdentityService(channel)
      val ledgerId: String = ledgerIdentityService.fetchLedgerId()
      val transactionService = new TransactionService(channel, ledgerId)
      val activeContractsService = new ActiveContractsService(channel, ledgerId)
      val commandCompletionService = new CommandCompletionService(channel, ledgerId)
      Future
        .traverse(config.streams) {
          case streamConfig: Config.StreamConfig.TransactionsStreamConfig =>
            val txMetrics = Some(
              MetricsSet
                .transactionExposedMetrics(streamConfig.name, registry, config.reportingPeriod)
            )
            perpetual {
              StreamMetrics
                .observer(
                  streamName = streamConfig.name,
                  logInterval = config.reportingPeriod,
                  metrics = MetricsSet.transactionMetrics(streamConfig.objectives),
                  logger = logger,
                  exposedMetrics = txMetrics,
                )(system, ec)
                .flatMap { observer =>
                  transactionService.transactions(streamConfig, observer)
                }
            }(perpetualStreamsEc)
          case streamConfig: Config.StreamConfig.TransactionTreesStreamConfig =>
            val txTreesMetrics = Some(
              MetricsSet.transactionTreesExposedMetrics(
                streamConfig.name,
                registry,
                config.reportingPeriod,
              )
            )
            perpetual {
              StreamMetrics
                .observer(
                  streamName = streamConfig.name,
                  logInterval = config.reportingPeriod,
                  metrics = MetricsSet.transactionTreesMetrics(streamConfig.objectives),
                  logger = logger,
                  exposedMetrics = txTreesMetrics,
                )(system, ec)
                .flatMap { observer =>
                  transactionService.transactionTrees(streamConfig, observer)
                }
            }(perpetualStreamsEc)
          case streamConfig: Config.StreamConfig.ActiveContractsStreamConfig =>
            perpetual {
              StreamMetrics
                .observer(
                  streamName = streamConfig.name,
                  logInterval = config.reportingPeriod,
                  metrics = MetricsSet.activeContractsMetrics,
                  logger = logger,
                  exposedMetrics = Some(
                    MetricsSet.activeContractsExposedMetrics(
                      streamConfig.name,
                      registry,
                      config.reportingPeriod,
                    )
                  ),
                )(system, ec)
                .flatMap { observer =>
                  activeContractsService.getActiveContracts(streamConfig, observer)
                }
            }(perpetualStreamsEc)
          case streamConfig: Config.StreamConfig.CompletionsStreamConfig =>
            perpetual {
              StreamMetrics
                .observer(
                  streamName = streamConfig.name,
                  logInterval = config.reportingPeriod,
                  metrics = MetricsSet.completionsMetrics,
                  logger = logger,
                  exposedMetrics = Some(
                    MetricsSet
                      .completionsExposedMetrics(
                        streamConfig.name,
                        registry,
                        config.reportingPeriod,
                      )
                  ),
                )(system, ec)
                .flatMap { observer =>
                  commandCompletionService.completions(streamConfig, observer)
                }
            }(perpetualStreamsEc)
        }
        .transform {
          case Success(results) =>
            if (results.contains(MetricsCollector.Message.MetricsResult.ObjectivesViolated))
              Failure(new RuntimeException("Metrics objectives not met."))
            else Success(())
          case Failure(ex) =>
            Failure(ex)
        }
    }
  }

  private def perpetual(
      f: => Future[MetricsCollector.Message.MetricsResult]
  )(ec: ExecutionContext): Future[MetricsCollector.Message.MetricsResult] = Future {
    var r: MetricsCollector.Message.MetricsResult = null
    for (idx <- 1 to 480) {
      println(s"Sleeping 1 minute before starting stream $idx")
      Thread.sleep(60000L)
      val start = System.currentTimeMillis()
      r = Await.result(f, Duration(8, JTimeUnit.HOURS))
      println(
        s"Stream $idx completed after ${(System.currentTimeMillis() - start) / 1000L} seconds"
      )
    }
    r
  }(ec)

  private def channelOwner(
      ledger: Config.Ledger,
      tls: TlsConfiguration,
      executor: Executor,
  ): ResourceOwner[Channel] = {
    logger.info(
      s"Setting up a managed channel to a ledger at: ${ledger.hostname}:${ledger.port}..."
    )
    logger.info("Sleeping 70 seconds before starting channel")
    Thread.sleep(70000L)
    val MessageChannelSizeBytes: Int = 32 * 1024 * 1024 // 32 MiB
    val ShutdownTimeout: FiniteDuration = 5.seconds

    val channelBuilder = NettyChannelBuilder
      .forAddress(ledger.hostname, ledger.port)
      .executor(executor)
      .maxInboundMessageSize(MessageChannelSizeBytes)
      .usePlaintext()

    if (tls.enabled) {
      tls.client.map { sslContext =>
        logger.info(s"Setting up a managed channel with transport security...")
        channelBuilder
          .useTransportSecurity()
          .sslContext(sslContext)
          .negotiationType(NegotiationType.TLS)
      }
    }

    ResourceOwner.forChannel(channelBuilder, ShutdownTimeout)
  }

  private def threadPoolExecutorOwner(
      config: Config.Concurrency
  ): ResourceOwner[ThreadPoolExecutor] =
    ResourceOwner.forExecutorService(() =>
      new ThreadPoolExecutor(
        config.corePoolSize,
        config.maxPoolSize,
        config.keepAliveTime,
        JTimeUnit.SECONDS,
        if (config.maxQueueLength == 0) new SynchronousQueue[Runnable]()
        else new ArrayBlockingQueue[Runnable](config.maxQueueLength),
      )
    )

  private val logger: Logger = LoggerFactory.getLogger(getClass)
}
