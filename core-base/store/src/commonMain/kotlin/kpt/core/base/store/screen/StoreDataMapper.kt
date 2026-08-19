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
import kotlinx.coroutines.flow.transform
import org.mobilenativefoundation.store.store5.StoreReadResponse
import org.mobilenativefoundation.store.store5.StoreReadResponseOrigin
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.TimeSource

/**
 * Maps a [StoreReadResponse] flow into `Flow<StoreData<Output>>`.
 *
 * Works for both source modes:
 * - **Network + Cache store**: Loading -> Data(Cache, refreshing) -> Data(Network)
 * - **Network-only store**: Loading -> Data(Network) or Error
 *
 * @param isEmpty Optional predicate to detect empty results (e.g., `{ it.isEmpty() }` for lists).
 *   Defaults to false (all data is non-empty).
 */
@OptIn(ExperimentalTime::class)
fun <Output : Any> Flow<StoreReadResponse<Output>>.mapToStoreData(
    isEmpty: (Output) -> Boolean = { false },
): Flow<StoreData<Output>> {
    var refreshing = false
    var lastFetchMark: TimeSource.Monotonic.ValueTimeMark? = null
    var lastFetchInstant: Instant? = null

    return transform { response ->
        when (response) {
            is StoreReadResponse.Initial,
            is StoreReadResponse.Loading,
            -> {
                refreshing = true
            }

            is StoreReadResponse.Data -> {
                @Suppress("SENSELESS_NULL_IN_WHEN", "USELESS_CAST")
                val value = (response.value as? Output) ?: return@transform
                val origin = response.origin.toDataOrigin()
                if (origin == DataOrigin.NETWORK) {
                    lastFetchMark = TimeSource.Monotonic.markNow()
                    lastFetchInstant = Clock.System.now()
                    refreshing = false
                } else if (lastFetchInstant == null) {
                    lastFetchMark = TimeSource.Monotonic.markNow()
                    lastFetchInstant = Clock.System.now()
                }
                emit(
                    StoreData(
                        data = value,
                        origin = origin,
                        isRefreshing = refreshing && origin != DataOrigin.NETWORK,
                        fetchedAt = lastFetchMark,
                        fetchedAtInstant = lastFetchInstant,
                        isEmpty = isEmpty(value),
                    ),
                )
            }

            is StoreReadResponse.NoNewData -> {
                refreshing = false
            }

            is StoreReadResponse.Error -> {
                refreshing = false
            }
        }
    }
}

/**
 * Like [mapToStoreData] but also emits on errors, carrying the last known data.
 *
 * Handles all status scenarios:
 * - **Success**: Data arrives -> emit with origin and staleness
 * - **Empty**: Data arrives but isEmpty predicate is true -> emit with isEmpty=true
 * - **Error with stale data**: Error after cache -> emit error with last known data
 * - **Error without data**: Error before any data -> emit error with fallback (isEmpty=true)
 * - **No network**: Error type hints at connectivity -> ViewModel can check error type
 *
 * @param fallback Value to use if an error arrives before any data.
 * @param isEmpty Predicate to detect empty results.
 */
@OptIn(ExperimentalTime::class)
fun <Output : Any> Flow<StoreReadResponse<Output>>.mapToStoreDataWithErrors(
    fallback: Output,
    isEmpty: (Output) -> Boolean = { false },
): Flow<StoreData<Output>> {
    var refreshing = false
    var lastFetchMark: TimeSource.Monotonic.ValueTimeMark? = null
    var lastFetchInstant: Instant? = null
    var lastData: Output = fallback
    var hasReceivedData = false

    return transform { response ->
        when (response) {
            is StoreReadResponse.Initial,
            is StoreReadResponse.Loading,
            -> {
                refreshing = true
            }

            is StoreReadResponse.Data -> {
                @Suppress("SENSELESS_NULL_IN_WHEN", "USELESS_CAST")
                val value = (response.value as? Output) ?: return@transform
                val origin = response.origin.toDataOrigin()
                if (origin == DataOrigin.NETWORK) {
                    lastFetchMark = TimeSource.Monotonic.markNow()
                    lastFetchInstant = Clock.System.now()
                    refreshing = false
                } else if (lastFetchInstant == null) {
                    lastFetchMark = TimeSource.Monotonic.markNow()
                    lastFetchInstant = Clock.System.now()
                }
                lastData = value
                hasReceivedData = true
                emit(
                    StoreData(
                        data = value,
                        origin = origin,
                        isRefreshing = refreshing && origin != DataOrigin.NETWORK,
                        fetchedAt = lastFetchMark,
                        fetchedAtInstant = lastFetchInstant,
                        isEmpty = isEmpty(value),
                    ),
                )
            }

            is StoreReadResponse.NoNewData -> {
                refreshing = false
            }

            is StoreReadResponse.Error -> {
                refreshing = false
                val error = response.toThrowable()
                emit(
                    StoreData(
                        data = lastData,
                        origin = if (hasReceivedData) DataOrigin.CACHE else DataOrigin.NETWORK,
                        isRefreshing = false,
                        fetchedAt = lastFetchMark,
                        fetchedAtInstant = lastFetchInstant,
                        error = error,
                        isEmpty = !hasReceivedData,
                    ),
                )
            }
        }
    }
}

/**
 * Maps Store responses to StoreData without requiring a fallback.
 * On error before any data: emits [StoreData] with isEmpty=true and error set.
 *
 * Note: When isEmpty=true, the `data` field contains the [EMPTY_SENTINEL] cast.
 * Callers MUST check isEmpty before accessing data. [DecisionEngine] enforces this.
 */
@OptIn(ExperimentalTime::class)
internal fun <Output : Any> Flow<StoreReadResponse<Output>>.mapToStoreDataNoFallback(
    isEmpty: (Output) -> Boolean = { false },
): Flow<StoreData<Output>> {
    var lastData: Output? = null
    var refreshing = false
    var lastFetchMark: TimeSource.Monotonic.ValueTimeMark? = null
    var lastFetchInstant: Instant? = null

    return transform { response ->
        when (response) {
            is StoreReadResponse.Initial,
            is StoreReadResponse.Loading,
            -> {
                refreshing = true
                val data = lastData
                if (data != null) {
                    emit(
                        StoreData(
                            data = data,
                            origin = DataOrigin.CACHE,
                            isRefreshing = true,
                            fetchedAt = lastFetchMark,
                            fetchedAtInstant = lastFetchInstant,
                            isEmpty = false,
                        ),
                    )
                } else {
                    // DecisionEngine starvation fix: emit explicit "no data, refreshing"
                    // so combine() in asScreenStream can run DecisionEngine on
                    // networkStatus immediately, instead of waiting for Ktor to
                    // time out (~30s) and surface an Error event. Without this
                    // emission, combine() starves until the first Data/Error.
                    @Suppress("UNCHECKED_CAST")
                    emit(
                        StoreData(
                            data = EMPTY_SENTINEL as Output,
                            origin = DataOrigin.NETWORK,
                            isRefreshing = true,
                            fetchedAt = null,
                            fetchedAtInstant = null,
                            isEmpty = true,
                        ),
                    )
                }
            }

            is StoreReadResponse.Data -> {
                // Guard: Store5 can emit Data with null value from SOT reader
                // despite Output : Any constraint (platform type leak)
                @Suppress("SENSELESS_NULL_IN_WHEN", "USELESS_CAST")
                val value = (response.value as? Output) ?: return@transform
                val origin = response.origin.toDataOrigin()
                if (origin == DataOrigin.NETWORK) {
                    lastFetchMark = TimeSource.Monotonic.markNow()
                    lastFetchInstant = Clock.System.now()
                    refreshing = false
                } else if (lastFetchInstant == null) {
                    // First CACHE/MEMORY emission with no prior network fetch this session.
                    // Record approximate load time so staleness UI can show a timestamp
                    // instead of bare "showing cached data". Resets to real fetch time
                    // once a network call succeeds.
                    lastFetchMark = TimeSource.Monotonic.markNow()
                    lastFetchInstant = Clock.System.now()
                }
                lastData = value
                emit(
                    StoreData(
                        data = value,
                        origin = origin,
                        isRefreshing = refreshing && origin != DataOrigin.NETWORK,
                        fetchedAt = lastFetchMark,
                        fetchedAtInstant = lastFetchInstant,
                        isEmpty = isEmpty(value),
                    ),
                )
            }

            is StoreReadResponse.NoNewData -> {
                refreshing = false
            }

            is StoreReadResponse.Error -> {
                refreshing = false
                val error = response.toThrowable()
                val data = lastData
                if (data != null) {
                    emit(
                        StoreData(
                            data = data,
                            origin = DataOrigin.CACHE,
                            isRefreshing = false,
                            fetchedAt = lastFetchMark,
                            fetchedAtInstant = lastFetchInstant,
                            error = error,
                            isEmpty = false,
                        ),
                    )
                } else {
                    // No data yet — emit empty with error for DecisionEngine.
                    // Uses sentinel cast — caller MUST check isEmpty before accessing data.
                    @Suppress("UNCHECKED_CAST")
                    emit(
                        StoreData(
                            data = EMPTY_SENTINEL as Output,
                            origin = DataOrigin.NETWORK,
                            isRefreshing = false,
                            fetchedAt = null,
                            fetchedAtInstant = null,
                            error = error,
                            isEmpty = true,
                        ),
                    )
                }
            }
        }
    }
}

/**
 * Internal sentinel object used as placeholder data when isEmpty=true.
 * Prevents NPE in data class generated methods (toString, hashCode, equals).
 */
internal val EMPTY_SENTINEL: Any = object {
    override fun toString(): String = "<empty>"
}

/**
 * Maps [StoreReadResponseOrigin] to [DataOrigin].
 *
 * [StoreReadResponseOrigin] is a top-level sealed class (NOT nested in StoreReadResponse).
 * [StoreReadResponseOrigin.Fetcher] is a data class with optional `name: String?`,
 * while SourceOfTruth, Cache, and Initial are singleton objects.
 */
internal fun StoreReadResponseOrigin.toDataOrigin(): DataOrigin {
    return when (this) {
        is StoreReadResponseOrigin.Fetcher -> DataOrigin.NETWORK
        StoreReadResponseOrigin.SourceOfTruth -> DataOrigin.CACHE
        StoreReadResponseOrigin.Cache -> DataOrigin.MEMORY
        StoreReadResponseOrigin.Initial -> DataOrigin.MEMORY
    }
}

/**
 * Extracts a [Throwable] from any [StoreReadResponse.Error] variant.
 */
internal fun StoreReadResponse.Error.toThrowable(): Throwable {
    return when (this) {
        is StoreReadResponse.Error.Exception -> error
        is StoreReadResponse.Error.Message -> RuntimeException(message)
        is StoreReadResponse.Error.Custom<*> -> RuntimeException("Store error: $error")
    }
}
