// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.ledger.sandbox

import com.daml.ledger.participant.state.kvutils.app.{Config, ConfigProvider}
import com.daml.platform.configuration.InitialLedgerConfiguration
import scopt.OptionParser

import java.time.Duration

case class BridgeConfig(
    conflictCheckingEnabled: Boolean,
    submissionBufferSize: Int,
    implicitPartyAllocation: Boolean,
    bridgeThreadPoolSize: Int,
)

object BridgeConfigProvider extends ConfigProvider[BridgeConfig] {
  override def extraConfigParser(parser: OptionParser[Config[BridgeConfig]]): Unit = {
    parser
      .opt[Int]("bridge-submission-buffer-size")
      .text("Submission buffer size. Defaults to 500.")
      .action((p, c) => c.copy(extra = c.extra.copy(submissionBufferSize = p)))

    parser
      .opt[Unit]("enable-conflict-checking")
      .text("Enables the ledger-side submission conflict checking.")
      .action((_, c) => c.copy(extra = c.extra.copy(conflictCheckingEnabled = true)))

    parser
      .opt[Boolean](name = "implicit-party-allocation")
      .optional()
      .action((x, c) => c.copy(extra = c.extra.copy(implicitPartyAllocation = x)))
      .text(
        s"When referring to a party that doesn't yet exist on the ledger, the participant will implicitly allocate that party."
          + s" You can optionally disable this behavior to bring participant into line with other ledgers."
      )

    parser
      .opt[Int](name = "bridge-thread-pool-size")
      .optional()
      .action((x, c) => c.copy(extra = c.extra.copy(bridgeThreadPoolSize = x)))
      .text(s"The size of the bridge thread-pool used for conflict checking.")
    parser.checkConfig(c =>
      Either.cond(
        c.maxDeduplicationDuration.forall(_.compareTo(Duration.ofHours(1L)) <= 0),
        (),
        "Maximum supported deduplication duration is one hour",
      )
    )
    ()
  }

  override def initialLedgerConfig(config: Config[BridgeConfig]): InitialLedgerConfiguration = {
    val superConfig = super.initialLedgerConfig(config)
    superConfig.copy(configuration =
      superConfig.configuration.copy(maxDeduplicationTime = DefaultMaximumDeduplicationTime)
    )
  }

  override val defaultExtraConfig: BridgeConfig = BridgeConfig(
    // TODO SoX: Enabled by default
    conflictCheckingEnabled = false,
    submissionBufferSize = 500,
    implicitPartyAllocation = false,
    bridgeThreadPoolSize = defaultBridgeThreadPoolSize(),
  )

  val DefaultMaximumDeduplicationTime: Duration = Duration.ofMinutes(30L)

  private def defaultBridgeThreadPoolSize() = {
    val target = Runtime.getRuntime.availableProcessors() / 4
    if (target < 2) 2 else target
  }
}
