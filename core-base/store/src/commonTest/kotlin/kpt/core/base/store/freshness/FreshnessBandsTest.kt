/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.store.freshness

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class FreshnessBandsTest {

    private val now = Clock.System.now()
    private val ttl = 5.minutes

    @Test
    fun `null lastSyncedAt and no error returns Initial`() {
        assertEquals(
            FreshnessBand.Initial,
            FreshnessBands.bandFor(now = now, lastSyncedAt = null, ttl = ttl, lastError = null),
        )
    }

    @Test
    fun `age within ttl returns Fresh`() {
        val syncedAt = now - 3.minutes
        assertEquals(
            FreshnessBand.Fresh,
            FreshnessBands.bandFor(now = now, lastSyncedAt = syncedAt, ttl = ttl, lastError = null),
        )
    }

    @Test
    fun `age between ttl and 3xttl returns Stale`() {
        val syncedAt = now - 8.minutes
        assertEquals(
            FreshnessBand.Stale,
            FreshnessBands.bandFor(now = now, lastSyncedAt = syncedAt, ttl = ttl, lastError = null),
        )
    }

    @Test
    fun `age over 3xttl returns VeryStale`() {
        val syncedAt = now - 20.minutes
        assertEquals(
            FreshnessBand.VeryStale,
            FreshnessBands.bandFor(now = now, lastSyncedAt = syncedAt, ttl = ttl, lastError = null),
        )
    }

    @Test
    fun `any lastError returns VeryStale regardless of age`() {
        val syncedAt = now - 1.minutes
        assertEquals(
            FreshnessBand.VeryStale,
            FreshnessBands.bandFor(
                now = now,
                lastSyncedAt = syncedAt,
                ttl = ttl,
                lastError = RuntimeException("503"),
            ),
        )
    }

    @Test
    fun `null lastSyncedAt with error returns VeryStale not Initial`() {
        // Error takes precedence over null-sync — surfaces failed initial fetch as VeryStale.
        assertEquals(
            FreshnessBand.VeryStale,
            FreshnessBands.bandFor(
                now = now,
                lastSyncedAt = null,
                ttl = ttl,
                lastError = RuntimeException("network"),
            ),
        )
    }

    @Test
    fun `age exactly equal to ttl returns Fresh boundary`() {
        val syncedAt = now - 5.minutes
        assertEquals(
            FreshnessBand.Fresh,
            FreshnessBands.bandFor(now = now, lastSyncedAt = syncedAt, ttl = ttl, lastError = null),
        )
    }

    @Test
    fun `age exactly equal to 3xttl returns Stale boundary`() {
        val syncedAt = now - 15.minutes
        assertEquals(
            FreshnessBand.Stale,
            FreshnessBands.bandFor(now = now, lastSyncedAt = syncedAt, ttl = ttl, lastError = null),
        )
    }
}
