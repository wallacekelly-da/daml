// Copyright (c) 2022 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.platform.store.cache

import java.util.concurrent.atomic.AtomicLong

import com.codahale.metrics.Timer
import com.daml.caching.ConcurrentCache
import com.daml.platform.store.cache.StateCache.State

import scala.concurrent.{ExecutionContext, Future, Promise}

/** This class is a wrapper around a Caffeine cache designed to handle
  * correct resolution of concurrent updates for the same key.
  */
private[platform] case class StateCache[K, V](
    cache: ConcurrentCache[K, State[V]],
    registerUpdateTimer: Timer,
    load: (K, Long) => Future[V],
    initialIndex: Long,
)(implicit
    ec: ExecutionContext
) {
  private val index: AtomicLong = new AtomicLong(initialIndex)

  def getIfPresent(key: K): Option[V] =
    cache.getIfPresent(key).flatMap {
      case State.Present(value) => Some(value)
      case State.Loading(promise) => promise.future.value.flatMap(_.toOption)
    }

  def get(key: K): Future[V] = {
    val promise = Promise[V]()
    cache.getOrAcquire(key, _ => State.Loading(promise)) match {
      case State.Present(value) => Future.successful(value)

      case loading @ State.Loading(fetchedPromise) =>
        if (fetchedPromise == promise)
          doLoad(key, loading) // this is what we started: initiate loading
        fetchedPromise.future
    }
  }

  private def doLoad(key: K, loading: State.Loading[V]): Unit =
    cache.getIfPresent(key) match {
      case None =>
        // inbetween got evicted (unlikely, but points to extreme pressure: not caching, loading the latest possible)
        completeLoading(key, loading)

      case Some(State.Present(value)) =>
        // inbetween got loaded, must be newer than what we wanted to load
        loading.promise.trySuccess(value)
        ()

      case Some(State.Loading(loading.`promise`)) =>
        // this is what was started: start loading it
        completeLoading(key, loading)

      case Some(State.Loading(promise)) =>
        // this loading belongs to a separate thread: just using it (possible if the loading this thread started is evicted, and a new loading started already)
        promise.future.onComplete(loading.promise.tryComplete)
    }

  private def completeLoading(key: K, loading: State.Loading[V]): Unit =
    // getting the index here ensures that no stale entries can be stuck in the cache since:
    // 1 - put is always preceded by its index update
    // 2 - signed Loading entry must be preceded by put, otherwise it will be changed
    // 3 - fetching the index here must be preceded by a signed Loading entry in the cache
    // Therefore index of the last put must precede this index.
    load(key, index.get()).onComplete { tryResult =>
      loading.promise.tryComplete(tryResult) // try to complete with the result
      if (tryResult.isFailure) {
        cache.getIfPresent(key) match {
          case Some(State.Loading(loading.`promise`)) =>
            // we do not want to cache a failure
            cache.invalidate(key)

          case _ =>
        }
      }
    }

  def put(key: K, validAt: Long, value: V): Unit =
    if (index.get() < validAt) {
      index.set(validAt)
      cache.merge(
        key,
        State.Present(value),
        {
          case (State.Present(_), newValue) => newValue
          case (State.Loading(promise), newValue) =>
            promise.trySuccess(value) // try to complete since this is for sure newer
            newValue // put always wins against loading
        },
      )
    }

}

private[cache] object StateCache {

  sealed trait State[V]

  object State {
    final case class Present[V](value: V) extends State[V]
    final case class Loading[V](promise: Promise[V]) extends State[V]
  }

}
