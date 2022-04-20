// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.ledger.runner.common

import com.daml.caching
import com.daml.lf.data.Ref
import com.daml.platform.apiserver.ApiServerConfig
import com.daml.platform.configuration.IndexConfiguration
import com.daml.platform.indexer.IndexerConfig

import java.time.Duration

final case class ParticipantConfig(
    mode: ParticipantRunMode,
    participantId: Ref.ParticipantId,
    // A name of the participant shard in a horizontally scaled participant.
    shardName: Option[String],
    indexerConfig: IndexerConfig,
    indexConfiguration: IndexConfiguration,
    lfValueTranslationContractCache: caching.SizedCache.Configuration,
    lfValueTranslationEventCache: caching.SizedCache.Configuration,
    maxDeduplicationDuration: Option[Duration],
    apiServerConfig: ApiServerConfig,
) {
  def metricsRegistryName: String = participantId + shardName.map("-" + _).getOrElse("")
}
