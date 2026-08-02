/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.store.paging

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.StoreBuilder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Verifies the refresh-semantics contract of [loadPage] (PLAN-fw-260504-screen-polish §1).
 *
 * The bug class this test prevents: loadPage(refresh=true) used to call
 * `cached(refresh=true)` and `.first()` — which returns the SoT cached emission
 * INSTANTLY, never observing the network fetch. Symptoms:
 * - Pull-to-refresh spinner hides immediately (loadPage "completes" in microseconds)
 * - lastFetchedAt never updates because fromNetwork stays false
 *
 * Now refresh=true uses `fresh(key)` which forces the fetcher path, so .first() waits
 * for the actual network response — slow path, but correct semantics.
 */
class LoadPageRefreshSemanticsTest {

    @Test
    fun loadPage_refreshTrue_waitsForNetwork_evenWhenSotIsPopulated() = runTest {
        // Pre-populate the SoT in-memory map so it has data BEFORE the test call.
        val sotData = mutableMapOf<PageKey, List<String>>()
        var fetcherCalled = false
        val store = StoreBuilder
            .from<PageKey, List<String>, List<String>>(
                fetcher = Fetcher.ofFlow { _ ->
                    flow {
                        fetcherCalled = true
                        // Simulate network latency — if .first() short-circuits on SoT,
                        // this delay is observable (test would complete sub-millisecond).
                        delay(50)
                        emit(listOf("fresh-from-network"))
                    }
                },
                sourceOfTruth = SourceOfTruth.of(
                    reader = { key -> flow { emit(sotData[key]) } },
                    writer = { key, value -> sotData[key] = value },
                ),
            )
            .build()

        // Seed SoT directly so cached path would have something to short-circuit on.
        sotData[PageKey.first(pageSize = 3)] = listOf("stale-from-sot")

        val result = store.loadPage(PageKey.first(pageSize = 3), refresh = true)
        assertIs<StorePageResult.Success<String>>(result)
        // refresh=true MUST return network data, not SoT — otherwise pull-to-refresh
        // doesn't actually refresh.
        assertEquals(listOf("fresh-from-network"), result.items)
        assertTrue(result.fromNetwork, "refresh=true must trigger network fetch")
        assertTrue(fetcherCalled, "refresh=true must invoke the fetcher even with SoT data")
    }

    @Test
    fun loadPage_refreshFalse_servesSotImmediately_skipsNetwork() = runTest {
        val sotData = mutableMapOf<PageKey, List<String>>()
        var fetcherCalled = false
        val store = StoreBuilder
            .from<PageKey, List<String>, List<String>>(
                fetcher = Fetcher.of { _ ->
                    fetcherCalled = true
                    listOf("fresh-from-network")
                },
                sourceOfTruth = SourceOfTruth.of(
                    reader = { key -> flow { emit(sotData[key]) } },
                    writer = { key, value -> sotData[key] = value },
                ),
            )
            .build()

        sotData[PageKey.first(pageSize = 3)] = listOf("stale-from-sot")

        val result = store.loadPage(PageKey.first(pageSize = 3), refresh = false)
        assertIs<StorePageResult.Success<String>>(result)
        // refresh=false serves SoT — that's the cache-first contract for load-more
        // and warm-reopen scenarios.
        assertEquals(listOf("stale-from-sot"), result.items)
        assertEquals(false, result.fromNetwork, "SoT served — must NOT mark fromNetwork=true")
        assertEquals(false, fetcherCalled, "refresh=false with populated SoT must not hit network")
    }

    @Test
    fun loadPage_refreshFalse_emptySot_fallsThroughToFetcher() = runTest {
        // When SoT is empty, cached(refresh=false) still allows the fetcher path —
        // otherwise initial loads with no prior data would hang forever.
        val sotData = mutableMapOf<PageKey, List<String>>()
        val store = StoreBuilder
            .from<PageKey, List<String>, List<String>>(
                fetcher = Fetcher.of { _ -> listOf("network-fallback") },
                sourceOfTruth = SourceOfTruth.of(
                    reader = { key -> flow { emit(sotData[key]) } },
                    writer = { key, value -> sotData[key] = value },
                ),
            )
            .build()

        val result = store.loadPage(PageKey.first(pageSize = 2), refresh = false)
        assertIs<StorePageResult.Success<String>>(result)
        assertEquals(listOf("network-fallback"), result.items)
        assertTrue(result.fromNetwork, "Empty SoT → fetcher path → fromNetwork=true")
    }
}
