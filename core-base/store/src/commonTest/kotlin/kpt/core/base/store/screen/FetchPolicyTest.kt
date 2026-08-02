/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.store.screen

import kotlin.test.Test
import kotlin.test.assertNotNull

/**
 * Vocabulary tests for [FetchPolicy].
 *
 * Asserts that every documented policy variant exists at the type level. Treats
 * [FetchPolicy] as an open vocabulary (sealed interface) — callers reference the
 * `data object` instances by name; if anyone deletes or renames one, this test
 * fails and forces a coordinated migration.
 *
 * Behavioral coverage (which Store5 request the policy maps to, what stream
 * shape comes out) lives in [FetchPolicyEnforcementTest] +
 * [ScreenDataStreamIntegrationTest] — this file only guarantees the vocabulary.
 */
class FetchPolicyTest {

    @Test
    fun networkWithCache_exists() {
        assertNotNull(FetchPolicy.NETWORK_WITH_CACHE)
    }

    @Test
    fun networkOnly_exists() {
        assertNotNull(FetchPolicy.NETWORK_ONLY)
    }

    @Test
    fun cacheOnly_exists() {
        assertNotNull(FetchPolicy.CACHE_ONLY)
    }

    @Test
    fun periodic_exists() {
        assertNotNull(FetchPolicy.PERIODIC(intervalMillis = 5_000L))
    }
}
