/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.store.screen

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreReadRequest

/**
 * Streams data from a [Store] with full [StoreData] metadata.
 *
 * This is the primary API for repositories. Source mode (network+cache vs network-only)
 * is determined by how the Store was created:
 * - [StoreFactory.createStore] -> network + cache (Fetcher + SourceOfTruth)
 * - [StoreFactory.createMemoryStore] -> network only (Fetcher only, in-memory cache)
 *
 * Both produce the same `Flow<StoreData<Output>>` -- the ViewModel doesn't need to know.
 *
 * @param key The data identifier.
 * @param refresh Whether to trigger a network refresh. Default true.
 * @param isEmpty Predicate to detect empty results.
 */
fun <Key : Any, Output : Any> Store<Key, Output>.streamData(
    key: Key,
    refresh: Boolean = true,
    isEmpty: (Output) -> Boolean = { false },
): Flow<StoreData<Output>> {
    return stream(StoreReadRequest.cached(key, refresh))
        .mapToStoreData(isEmpty)
}

/**
 * Like [streamData] but also emits on errors with last known data preserved.
 *
 * @param key The data identifier.
 * @param fallback Value to use if error arrives before any data.
 * @param refresh Whether to trigger a network refresh. Default true.
 * @param isEmpty Predicate to detect empty results.
 */
fun <Key : Any, Output : Any> Store<Key, Output>.streamDataWithErrors(
    key: Key,
    fallback: Output,
    refresh: Boolean = true,
    isEmpty: (Output) -> Boolean = { false },
): Flow<StoreData<Output>> {
    return stream(StoreReadRequest.cached(key, refresh))
        .mapToStoreDataWithErrors(fallback, isEmpty)
}

/**
 * Forces a fresh network fetch, ignoring any cached data.
 * Useful for pull-to-refresh or explicit "reload" actions.
 */
fun <Key : Any, Output : Any> Store<Key, Output>.freshData(
    key: Key,
    isEmpty: (Output) -> Boolean = { false },
): Flow<StoreData<Output>> {
    return stream(StoreReadRequest.fresh(key, fallBackToSourceOfTruth = true))
        .mapToStoreData(isEmpty)
}

/**
 * Sibling fresh-fetch path for Store5 S5-5 — the counterpart to
 * [FetchPolicy.CACHE_FIRST_SWR]'s cache-first read. Delegates to the existing
 * [freshData] extension so both surfaces share a single Store5
 * `StoreReadRequest.fresh(fallBackToSourceOfTruth = true)` implementation and
 * cannot drift.
 *
 * Use for pull-to-refresh and post-mutation invalidate: the caller has
 * already asserted intent to burn a network request, so this bypasses the SWR
 * band gate that [asScreenStream] layers over `CACHE_FIRST_SWR`.
 */
fun <Key : Any, Output : Any> Store<Key, Output>.refreshFresh(
    key: Key,
    isEmpty: (Output) -> Boolean = { false },
): Flow<StoreData<Output>> = freshData(key, isEmpty)

/**
 * Reads only from local cache/database, no network.
 * Useful for offline-only screens or pre-fetched data.
 */
fun <Key : Any, Output : Any> Store<Key, Output>.localData(
    key: Key,
    isEmpty: (Output) -> Boolean = { false },
): Flow<StoreData<Output>> {
    return stream(StoreReadRequest.localOnly(key))
        .mapToStoreData(isEmpty)
}

/**
 * Like [streamDataWithErrors] but without requiring a fallback value.
 * Emits StoreData with isEmpty=true when error arrives before any data.
 * Used by ScreenDataStream where DecisionEngine handles the no-data case.
 */
fun <Key : Any, Output : Any> Store<Key, Output>.streamDataNoFallback(
    key: Key,
    refresh: Boolean = true,
    isEmpty: (Output) -> Boolean = { false },
): Flow<StoreData<Output>> {
    return stream(StoreReadRequest.cached(key, refresh))
        .mapToStoreDataNoFallback(isEmpty)
}

/**
 * Maps [StoreData] content while preserving all metadata.
 */
fun <T, R> StoreData<T>.map(transform: (T) -> R): StoreData<R> {
    return StoreData(
        data = transform(data),
        origin = origin,
        isRefreshing = isRefreshing,
        fetchedAt = fetchedAt,
        fetchedAtInstant = fetchedAtInstant,
        error = error,
        isEmpty = isEmpty,
    )
}

/**
 * Maps a Flow of [StoreData] content while preserving all metadata.
 */
fun <T, R> Flow<StoreData<T>>.mapData(transform: (T) -> R): Flow<StoreData<R>> {
    return map { it.map(transform) }
}

/**
 * Bypasses in-memory cache, reads from SourceOfTruth + optional network refresh.
 * Useful when you know in-memory state may be stale but disk is authoritative.
 */
fun <Key : Any, Output : Any> Store<Key, Output>.skipMemoryData(
    key: Key,
    refresh: Boolean = true,
    isEmpty: (Output) -> Boolean = { false },
): Flow<StoreData<Output>> {
    return stream(StoreReadRequest.skipMemory(key, refresh))
        .mapToStoreDataNoFallback(isEmpty)
}

/**
 * Internal helper — selects the correct Store5 request based on [FetchPolicy].
 *
 * Used by [asScreenStream], [asLoadOnceStream], and [PagingScreenStream] so policy
 * logic lives in one place rather than being duplicated across all three callers.
 */
internal fun <Key : Any, Output : Any> Store<Key, Output>.streamDataForPolicy(
    key: Key,
    policy: FetchPolicy,
    isEmpty: (Output) -> Boolean = { false },
): Flow<StoreData<Output>> = when (policy) {
    FetchPolicy.NETWORK_WITH_CACHE -> streamDataNoFallback(key, isEmpty = isEmpty)
    FetchPolicy.NETWORK_ONLY ->
        stream(StoreReadRequest.fresh(key, fallBackToSourceOfTruth = true))
            .mapToStoreDataNoFallback(isEmpty)
    FetchPolicy.CACHE_ONLY ->
        stream(StoreReadRequest.localOnly(key))
            .mapToStoreDataNoFallback(isEmpty)
    // Cache-first SWR: emit cached value with refresh=false so no network hits
    // on the read path itself. `StoreReadRequest.cached(key, refresh = false)`
    // in Store5 5.1.0-alpha08 keeps the subscription open on the store's
    // [org.mobilenativefoundation.store.store5.SourceOfTruth] (when present)
    // and re-emits on every SoT write — proven by
    // `RoomChangeBusSwrTest.notifyingWriteTriggersDaoFlowReEmissionUnderSwr`.
    // This is the mechanism SWR revalidation depends on: `asScreenStream`'s
    // band gate launches an independent `stream(StoreReadRequest.fresh(...))`
    // side-fetch whose network response writes SoT, and the SoT write
    // re-fans-out through THIS subscription as a fresh-value emission that
    // reaches the consumer without any explicit re-subscription.
    //
    // For memory-only stores (no SoT), `cached(refresh = false)` serves the
    // memory cache once; the SWR swap-in is a no-op there (memory cache does
    // not re-emit on write) but the read stays correct.
    //
    // The sibling [Store.refreshFresh] extension + [ScreenDataStream.refreshFresh]
    // method together cover the S5-5 explicit fresh-fetch path required for
    // pull-to-refresh and post-mutation invalidate — those are distinct from
    // the band-gated SWR revalidation and stay untouched.
    FetchPolicy.CACHE_FIRST_SWR ->
        stream(StoreReadRequest.cached(key, refresh = false))
            .mapToStoreDataNoFallback(isEmpty)
    // PERIODIC's read semantic is network-with-cache — the periodic refresh
    // is layered on top by ScreenDataStream via a ticker that fires through
    // the same refresh-trigger pipeline as the reconnect/user-tap refresh.
    is FetchPolicy.PERIODIC -> streamDataNoFallback(key, isEmpty = isEmpty)
}
