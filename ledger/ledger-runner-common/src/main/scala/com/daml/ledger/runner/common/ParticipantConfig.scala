// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.ledger.runner.common

import com.daml.lf.data.Ref
import com.daml.platform.apiserver.ApiServerConfig
import com.daml.platform.configuration.IndexConfiguration
import com.daml.platform.indexer.IndexerConfig
import com.daml.platform.store.LfValueTranslationCache

import java.time.Duration

final case class ParticipantConfig(
    participantId: Ref.ParticipantId,
    shardName: Option[String],
    mode: ParticipantRunMode,
    // A name of the participant shard in a horizontally scaled participant.
    indexer: IndexerConfig,
    index: IndexConfiguration,
    lfValueTranslationCache: LfValueTranslationCache.Config,
    maxDeduplicationDuration: Option[Duration],
    apiServer: ApiServerConfig,
) {
  def metricsRegistryName: String = participantId + shardName.map("-" + _).getOrElse("")
}
