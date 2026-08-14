/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifospay.core.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * Fork-wide alias for `Flow<ScreenState<T>>` — the read-side stream shape
 * every Phase-3 repository exposes to its ViewModels.
 *
 * Type-alias transparency means any existing `Flow<ScreenState<T>>` return
 * type overrides / satisfies a `ScreenStateStream<T>` declaration and vice
 * versa; nothing on the call site changes.
 */
typealias ScreenStateStream<T> = Flow<ScreenState<T>>

/**
 * Convert a `Flow<T>` (typically the raw Ktorfit API flow) into a
 * `Flow<ScreenState<T>>`. Phase-3 TRANSITIONAL shim — mirrors
 * [asDataStateFlow] semantics but emits [ScreenState] variants.
 */
fun <T> Flow<T>.asScreenStateFlow(
    isEmpty: (T) -> Boolean = { false },
): Flow<ScreenState<T>> =
    map<T, ScreenState<T>> { value ->
        if (isEmpty(value)) ScreenState.Empty else ScreenState.Content(data = value)
    }
        .onStart { emit(ScreenState.Loading) }
        .catch { e -> emit(AppErrorMapper.mapError(e)) }

/**
 * Body-parsing overload — routes ktor 4xx/5xx exceptions through
 * [AppErrorMapper.mapErrorBody] so the server's Mifos-error JSON is
 * lifted into `HttpStatusException.userMessage`.
 */
fun <T> Flow<T>.asScreenStateFlow(
    errorBodyParser: ErrorBodyParser,
    isEmpty: (T) -> Boolean = { false },
): Flow<ScreenState<T>> =
    map<T, ScreenState<T>> { value ->
        if (isEmpty(value)) ScreenState.Empty else ScreenState.Content(data = value)
    }
        .onStart { emit(ScreenState.Loading) }
        .catch { e -> emit(AppErrorMapper.mapErrorBody<T>(e, errorBodyParser)) }

/**
 * Bridge for repositories that still return `Flow<DataState<T>>` — lifts each
 * emission into a `Flow<ScreenState<T>>`. Useful when a caller has migrated
 * to `ScreenState` but the underlying repo has not yet.
 */
fun <T> Flow<DataState<T>>.toScreenStateFlow(
    isEmpty: (T) -> Boolean = { false },
): Flow<ScreenState<T>> =
    map<DataState<T>, ScreenState<T>> { ds ->
        val screen = ds.toScreenState()
        if (screen is ScreenState.Content && isEmpty(screen.data)) ScreenState.Empty else screen
    }

/**
 * Extension on `Flow<ScreenState<T>>` mirroring the template's
 * `mapContent { data, freshness -> R }` — the freshness slot is dropped
 * because Phase-3 lacks the FreshnessSignal engine. Phase-4 replaces this
 * with the template's two-arg lambda.
 */
fun <T, R> Flow<ScreenState<T>>.mapContent(transform: (T) -> R): Flow<ScreenState<R>> =
    map { it.map(transform) }

/**
 * Template-parity `emptyIfContent` — flips [ScreenState.Content] whose data
 * satisfies [predicate] to [ScreenState.Empty]. Applied AFTER the upstream
 * repository has already emitted; use for business-level empty (e.g.
 * `list.isEmpty()`).
 */
fun <T> Flow<ScreenState<T>>.emptyIfContent(
    predicate: (T) -> Boolean,
): Flow<ScreenState<T>> = map { state ->
    when (state) {
        is ScreenState.Content -> if (predicate(state.data)) ScreenState.Empty else state
        else -> state
    }
}
