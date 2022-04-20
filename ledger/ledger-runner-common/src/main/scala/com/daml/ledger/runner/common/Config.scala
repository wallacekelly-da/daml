// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.ledger.runner.common

import com.daml.lf.engine.EngineConfig

final case class Config(
    ledgerId: String,
    engineConfig: EngineConfig,
    metricsConfig: MetricsConfig,
    participants: Seq[ParticipantConfig],
)
