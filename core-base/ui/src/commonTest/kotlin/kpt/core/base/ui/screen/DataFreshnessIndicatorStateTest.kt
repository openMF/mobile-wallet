/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.ui.screen

import kpt.core.base.store.freshness.FreshnessSignal
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Locks the [deriveDisplayState] contract — the pure mapping from
 * [FreshnessSignal.isRefreshing] to [DisplayState] used by [DataFreshnessIndicator].
 *
 * Post-data-freshness-redesign Phase C (2026-06-17): the deprecated [DataFreshness]
 * enum is deleted; `isRefreshing` lives on [FreshnessSignal]. The Hidden/Updating
 * outputs map purely from that single boolean — stale-band concerns live in the
 * per-card `FreshnessIndicator` info icon, not this banner.
 *
 * Null [ScreenContent] `refreshingIndicator` slot suppression is verified by
 * inspection: the guard `if (current.freshnessSignal.isRefreshing) refreshingIndicator?.invoke()`
 * is a no-op when the slot is null. Full Compose UI tests require compose.uiTest
 * wired into commonTest — tracked separately.
 */
class DataFreshnessIndicatorStateTest {

    @Test
    fun notRefreshingIsHidden() {
        val signal = FreshnessSignal.initial().copy(isRefreshing = false)
        assertEquals(DisplayState.Hidden, deriveDisplayState(signal))
    }

    @Test
    fun refreshingIsUpdating() {
        val signal = FreshnessSignal.initial().copy(isRefreshing = true)
        assertEquals(DisplayState.Updating, deriveDisplayState(signal))
    }
}
