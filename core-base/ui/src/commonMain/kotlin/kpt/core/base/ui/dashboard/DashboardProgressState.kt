/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.ui.dashboard

import androidx.compose.runtime.Immutable

/**
 * Aggregate progress snapshot for a multi-card dashboard. Computed from a list of
 * [kpt.core.base.store.screen.ScreenState] instances — one per card — so the
 * dashboard can render a single high-level signal ("4 of 7 loaded") above a grid of
 * independently-loading cards.
 *
 * Produced by [aggregateDashboardProgress] and consumed by [DashboardProgressBar].
 *
 * Semantics:
 *  - [loaded] counts only `ScreenState.Content` cards — cards displaying real data.
 *  - [total] is the total number of cards observed; the bar hides itself when
 *    `total == 0` (no cards) or `loaded == total` (everything loaded).
 *  - [isAnyLoading] is true when at least one card is still in the initial
 *    `ScreenState.Loading` state (it does NOT include `UPDATING` content cards —
 *    those are visible-with-spinner via `DataFreshness.UPDATING` on the card itself).
 *  - [hasAnyError] is true when at least one card is in `ScreenState.Error` or
 *    `ScreenState.NoNetwork`. Apps may surface a "Some cards failed to load" banner
 *    above the grid when this is true.
 *  - [hasAnyEmpty] is true when at least one card is in `ScreenState.Empty`. Useful
 *    for analytics ("did the user see any 'no data' cards this session?").
 *
 * The shape is intentionally simple: a single value class consumed by both UI and
 * telemetry. For per-card retry, route through the card's own `onRetry`.
 */
@Immutable
data class DashboardProgressState(
    val loaded: Int,
    val total: Int,
    val isAnyLoading: Boolean,
    val hasAnyError: Boolean,
    val hasAnyEmpty: Boolean,
)
