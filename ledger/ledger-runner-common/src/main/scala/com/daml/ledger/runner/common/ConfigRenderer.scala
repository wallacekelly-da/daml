// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.ledger.runner.common

import com.typesafe.config.{ConfigRenderOptions, ConfigValue}

object ConfigRenderer {

  private def toConfig(
      config: Config
  )(implicit writer: pureconfig.ConfigWriter[Config]): ConfigValue = {
    writer.to(config)
  }

  def render(config: Config): String = {
    import FileBasedConfig._

    val defaultConfigRenderer =
      ConfigRenderOptions.defaults().setOriginComments(false).setComments(false).setJson(false).setFormatted(true)

    val configValue = toConfig(config)
    configValue.render(defaultConfigRenderer)
  }

}
