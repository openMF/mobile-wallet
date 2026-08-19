/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.ui.freshness

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * Locks the [humanizeDuration] contract — pure boundary mapping used by
 * `FreshnessIndicator` tooltips ("Updated 5m ago" / "yesterday" / "3 days ago").
 *
 * Boundaries:
 *  - `< 60s`     → "just now"
 *  - `< 60min`   → "Nm ago"
 *  - `< 24h`     → "Nh ago"
 *  - `< 48h`     → "yesterday"
 *  - else        → "N days ago"
 */
class FreshnessStringsTest {

    @Test
    fun `under 60s returns just now`() {
        assertEquals("just now", humanizeDuration(59.seconds))
    }

    @Test
    fun `exactly 60s returns 1m ago`() {
        assertEquals("1m ago", humanizeDuration(60.seconds))
    }

    @Test
    fun `59m returns 59m ago`() {
        assertEquals("59m ago", humanizeDuration(59.minutes))
    }

    @Test
    fun `1h returns 1h ago`() {
        assertEquals("1h ago", humanizeDuration(1.hours))
    }

    @Test
    fun `23h returns 23h ago`() {
        assertEquals("23h ago", humanizeDuration(23.hours))
    }

    @Test
    fun `24h returns yesterday`() {
        assertEquals("yesterday", humanizeDuration(24.hours))
    }

    @Test
    fun `47h returns yesterday`() {
        assertEquals("yesterday", humanizeDuration(47.hours))
    }

    @Test
    fun `48h returns 2 days ago`() {
        assertEquals("2 days ago", humanizeDuration(48.hours))
    }

    @Test
    fun `72h returns 3 days ago`() {
        assertEquals("3 days ago", humanizeDuration(72.hours))
    }
}
