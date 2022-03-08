// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.platform.store.cache

import com.codahale.metrics.Timer
import com.daml.caching.ConcurrentCache
import com.daml.logging.{ContextualizedLogger, LoggingContext}

import scala.concurrent.{ExecutionContext, Future}

/** This class is a wrapper around a Caffeine cache designed to handle
  * correct resolution of concurrent updates for the same key.
  */
private[platform] case class StateCache[K, V](
    cache: ConcurrentCache[K, (V, Long)],
    registerUpdateTimer: Timer,
)(implicit
    ec: ExecutionContext
) {
  private val logger: ContextualizedLogger = ContextualizedLogger.get(getClass)

  /** Fetch the corresponding value for an input key, if present.
    *
    * @param key the key to query for
    * @return optionally [[V]]
    */
  def get(key: K)(implicit loggingContext: LoggingContext): Option[V] =
    cache.getIfPresent(key) match {
      case Some(value) =>
        logger.debug(s"Cache hit for $key -> ${value.toString.take(100)}")
        Some(value._1)
      case None =>
        logger.debug(s"Cache miss for $key ")
        None
    }

  /** Update the cache synchronously.
    *
    * In face of multiple in-flight updates competing for the `key` (see [[putAsync()]]),
    * this method updates the cache only if the to-be-inserted tuple is the most recent
    * (i.e. it has `validAt` highest amongst the competing updates).
    *
    * @param key the key at which to update the cache
    * @param validAt ordering discriminator for pending updates for the same key
    * @param value the value to insert
    */
  def put(key: K, validAt: Long, value: V): Unit =
    cache.merge(
      key,
      value -> validAt,
      (oldValue, newValue) =>
        if (oldValue._2 < newValue._2) newValue
        else oldValue,
    )

  /** Update the cache asynchronously.
    *
    * In face of multiple in-flight updates competing for the `key`,
    * this method registers an async update to the cache
    * only if the to-be-inserted tuple is the most recent
    * (i.e. it has `validAt` highest amongst the competing updates).
    *
    * @param key the key at which to update the cache
    * @param validAt ordering discriminator for pending updates for the same key
    * @param eventualValue the eventual result signaling successful enqueuing of the cache async update
    */
  final def putAsync(key: K, validAt: Long, eventualValue: Future[V])(implicit
      loggingContext: LoggingContext
  ): Future[Unit] =
    eventualValue
      .map { (value: V) =>
        put(key, validAt, value)
      }
      .recover { case err =>
        logger.warn(s"Failure in pending cache update for key $key", err)
      }

}

object StateCache {

  /** Used to track competing updates to the cache for a specific key.
    * @param pendingCount The number of in-progress updates.
    * @param latestValidAt Highest version of any pending update.
    */
  private[cache] case class PendingUpdatesState(
      var pendingCount: Long,
      var latestValidAt: Long,
  )
  private[cache] object PendingUpdatesState {
    def empty: PendingUpdatesState = PendingUpdatesState(
      0L,
      Long.MinValue,
    )
  }
}
