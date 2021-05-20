// Copyright (c) 2021 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.http

import com.daml.logging.{ContextualizedLogger, LoggingContext}
import com.daml.telemetry.{
  NoOpTelemetryContext,
  OpenTelemetryTracer,
  RootDefaultTelemetryContext,
  SpanKind,
  TelemetryContext,
}

case class Context(private val lc: LoggingContext, private val tc: TelemetryContext)

object Context {
  def apply()(implicit lc: LoggingContext, tc: TelemetryContext): Context = new Context(lc, tc)
  def apply(lc: LoggingContext, tc: TelemetryContext): Context = new Context(lc, tc)
  def apply(tc: TelemetryContext) = new Context(LoggingContext.empty, tc)
  def apply(lc: LoggingContext) = new Context(lc, NoOpTelemetryContext)

  def empty = Context(LoggingContext.empty, NoOpTelemetryContext)

  def withEnrichedLoggingContext[A](kvs: Map[String, String])(f: Context => A)(implicit
      ctx: Context
  ): A =
    LoggingContext.withEnrichedLoggingContext(kvs)(newLc => f(ctx.copy(lc = newLc)))

  def withEnrichedContext[A](spanKind: SpanKind, attributes: (String, String)*) = {}

  implicit def contextToLoggingContext(implicit ctx: Context): LoggingContext = ctx.lc
  implicit def contextToTelemetryContext(implicit ctx: Context): TelemetryContext = ctx.tc
}

object Foo {
  val logger = ContextualizedLogger.get(getClass)
  import Context._
  def ham()(implicit ctx: Context) = {
    logger.info("")

  }

  def test() = {
    val lc = LoggingContext.ForTesting
    val tc = RootDefaultTelemetryContext(OpenTelemetryTracer)
    implicit val ctx = Context(lc, tc)
    LoggingContext.withEnrichedLoggingContext(("foo", "bar"))(implicit lc => {
      implicit val ctx = implicitly[Context]
      ham()
    })

  }
  def test1() = {
    implicit val lc = LoggingContext.ForTesting
    implicit val tc = RootDefaultTelemetryContext(OpenTelemetryTracer)
    val context = Context

  }
}
