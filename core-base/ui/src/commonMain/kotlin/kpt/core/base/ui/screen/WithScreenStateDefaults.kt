/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/**
 * Wraps [content] so that its screen-state widgets ([ScreenContent], [PagingScreenContent])
 * see [overrides] merged on top of the ambient [LocalScreenStateDefaults]. Useful for
 * per-screen tweaks (e.g. "this list screen should show 'Loading bills…' rather than the
 * generic skeleton").
 *
 * Override precedence (highest first):
 *  1. Per-call slot lambda (`ScreenContent(empty = { ... })`)
 *  2. [WithScreenStateDefaults] (this composable, screen-scoped)
 *  3. App-wide `LocalScreenStateDefaults` (typically set in [KptTheme])
 *  4. Library default
 *
 * Example:
 * ```kotlin
 * WithScreenStateDefaults(
 *     overrides = LocalScreenStateDefaults.current.copy(
 *         empty = ScreenStateEmpty(title = "No bills", message = "Add your first bill below."),
 *     ),
 * ) {
 *     ScreenContent(state = state, onRetry = ::onRetry) { bills -> /* ... */ }
 * }
 * ```
 */
@Composable
fun WithScreenStateDefaults(
    overrides: ScreenStateDefaults,
    content: @Composable () -> Unit,
) {
    val current = LocalScreenStateDefaults.current
    val merged = current.mergeWith(overrides)
    CompositionLocalProvider(LocalScreenStateDefaults provides merged) { content() }
}

/**
 * Returns a new [ScreenStateDefaults] where every field in [other] overrides the
 * corresponding field in `this`. Used by [WithScreenStateDefaults].
 *
 * Simple "other wins" merge: every nested field from [other] replaces `this`'s. To partially
 * override only the `.empty` slot (keeping the rest), construct [other] via `copy()`:
 *
 * ```kotlin
 * val partial = LocalScreenStateDefaults.current.copy(
 *     empty = ScreenStateEmpty(title = "Custom"),
 * )
 * WithScreenStateDefaults(overrides = partial) { /* ... */ }
 * ```
 */
fun ScreenStateDefaults.mergeWith(other: ScreenStateDefaults): ScreenStateDefaults =
    ScreenStateDefaults(
        loading = other.loading,
        empty = other.empty,
        noNetwork = other.noNetwork,
        error = other.error,
    )
