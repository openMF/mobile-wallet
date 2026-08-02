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

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

/**
 * Verifies the pure helper functions of [DataFreshnessIndicator]:
 * - [formatDurationAgo] human-readable duration formatting
 * - [buildStaleText] / [buildUpdatingText] timestamp-aware copy
 *
 * These are the behaviors a user observes in the staleness banner. PLAN-fw-260504
 * polish (G2 "always shows just now" / "showing cached data") regressions land
 * here — if these tests pass, the banner shows the right text.
 */
@OptIn(ExperimentalTime::class)
class DataFreshnessTextTest {

    // ── formatDurationAgo: human-readable bucket ──────────────────────────

    @Test
    fun formatDurationAgo_secondsBucket_isJustNow() {
        assertEquals("just now", formatDurationAgo(0.seconds))
        assertEquals("just now", formatDurationAgo(30.seconds))
        assertEquals("just now", formatDurationAgo(59.seconds))
    }

    @Test
    fun formatDurationAgo_minutesBucket_showsM() {
        assertEquals("1m ago", formatDurationAgo(1.minutes))
        assertEquals("5m ago", formatDurationAgo(5.minutes))
        assertEquals("59m ago", formatDurationAgo(59.minutes))
    }

    @Test
    fun formatDurationAgo_hoursBucket_showsH() {
        assertEquals("1h ago", formatDurationAgo(1.hours))
        assertEquals("23h ago", formatDurationAgo(23.hours))
    }

    @Test
    fun formatDurationAgo_daysBucket_showsD() {
        assertEquals("1d ago", formatDurationAgo(1.days))
        assertEquals("7d ago", formatDurationAgo(7.days))
    }

    // ── buildStaleText tests removed 2026-06-17 (staleness-ui-consumer-ownership epic)
    //    StaleBanner + its buildStaleText helper were deleted; feature screens own
    //    their stale UI via the content lambda + per-card FreshnessIndicator info-icon.
    //    Tests for that copy moved out with the banner. The remaining tests below
    //    cover buildUpdatingText (the refreshing-banner copy still in scope).

    // ── buildUpdatingText: indicator copy during refresh ──────────────────

    @Test
    fun buildUpdatingText_nullFetchedAt_isRefreshing() {
        assertEquals("Refreshing…", buildUpdatingText(fetchedAt = null))
    }

    @Test
    fun buildUpdatingText_recentFetch_showsLastUpdated() {
        val twoMinutesAgo = Clock.System.now() - 2.minutes
        val text = buildUpdatingText(twoMinutesAgo)
        assertEquals("Refreshing · Last updated 2m ago", text)
    }
}
