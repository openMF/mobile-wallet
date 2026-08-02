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

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.StoreBuilder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Verifies the cache-first contract of [loadPage] (PLAN-fw-260504 §1.2, G2).
 *
 * Without `refresh = false`, every load-more page would hit the network even if
 * the page was just fetched — defeating Store5's caching for paginated reads.
 */
class StorePagingSourceTest {

    @Test
    fun loadPage_cacheFirst_doesNotRefetchOnRepeatedCalls() = runTest {
        var fetchCount = 0
        val store = StoreBuilder
            .from<PageKey, List<String>>(
                fetcher = Fetcher.ofFlow { key ->
                    flow {
                        fetchCount++
                        emit(List(key.pageSize) { i -> "page=${key.page} item=$i" })
                    }
                },
            )
            .build()

        val key = PageKey.first(pageSize = 5)

        // First call: cache empty → network fetch.
        val first = store.loadPage(key)
        assertIs<StorePageResult.Success<String>>(first)
        assertEquals(5, first.items.size)
        assertEquals(1, fetchCount, "First load must hit network exactly once")

        // Second call: same key, cache-first default → no additional network call.
        val second = store.loadPage(key)
        assertIs<StorePageResult.Success<String>>(second)
        assertEquals(5, second.items.size)
        assertEquals(
            1,
            fetchCount,
            "Second load with default refresh=false must serve from cache (G2 contract)",
        )
    }

    @Test
    fun loadPage_acceptsExplicitRefreshTrue_withoutError() = runTest {
        // Smoke test that the refresh=true overload exists and round-trips through
        // Store5 without throwing. The actual "fresh data on refresh" semantics are
        // governed by Store5's StoreReadRequest.cached(refresh=true) contract — see
        // PagingScreenStream.refresh() which calls loadInitialPage(refresh=true) for
        // pull-to-refresh.
        var fetchCount = 0
        val store = StoreBuilder
            .from<PageKey, List<String>>(
                fetcher = Fetcher.ofFlow { key ->
                    flow {
                        fetchCount++
                        emit(List(key.pageSize) { "p=${key.page}-i=$it-fetch=$fetchCount" })
                    }
                },
            )
            .build()

        val key = PageKey.first(pageSize = 3)
        val result = store.loadPage(key, refresh = true)
        assertIs<StorePageResult.Success<String>>(result)
        assertEquals(3, result.items.size)
        assertTrue(fetchCount >= 1, "refresh=true must invoke fetcher at least once")
    }
}
