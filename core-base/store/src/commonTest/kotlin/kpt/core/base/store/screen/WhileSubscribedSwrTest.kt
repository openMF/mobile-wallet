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

import app.cash.turbine.test
import io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkInfo
import io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkStatus
import io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kpt.core.base.store.fixtures.FakeNetworkMonitor
import kpt.core.base.store.infra.FakeFetchedAtRepository
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.StoreBuilder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.ExperimentalTime

/**
 * Fence test for GOAL D13 — feature ViewModels stay pinned at
 * `stateIn(WhileSubscribed(5_000), ScreenState.Loading)`. With
 * [FetchPolicy.CACHE_FIRST_SWR] active, the SWR read path must serve the
 * cache instantly on re-subscription so the 5-second cancellation window
 * is no longer a UX pain — the user does NOT see a `Loading` flash after
 * a brief navigation away.
 *
 * The referenced runtime code is `ScreenDataStream.asScreenStream(...)` in
 * `core-base/store/src/commonMain/kotlin/kpt/core/base/store/screen/ScreenDataStream.kt`,
 * specifically the `refreshTrigger.onStart { emit(Unit) }.flatMapLatest { ... }`
 * pipeline that re-runs `streamDataForPolicy(CACHE_FIRST_SWR)` on every fresh
 * subscription and consequently re-serves the cached value.
 *
 * Kept in `core-base/store` commonTest so the fence lives next to the primitive
 * it protects; the source-side grep guard-scan referenced in the plan
 * (`grep -rEc "SharingStarted\\.Eagerly|WhileSubscribed\\(Long\\.MAX_VALUE\\)" feature/`)
 * runs at CI time and is documented under the Acceptance section of the
 * companion sub-plan `01-swr-read-path.md`.
 */
@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
class WhileSubscribedSwrTest {

    private val onlineInfo = NetworkInfo(type = NetworkType.WiFi, isMetered = false)
    private val online = NetworkStatus.Available(onlineInfo)

    @Test
    fun reSubscribeAfter5sServesCacheNotLoading() = runTest {
        var fetchCount = 0
        val store = StoreBuilder
            .from<String, Int>(
                fetcher = Fetcher.of { _ ->
                    fetchCount++
                    7
                },
            )
            .build()

        // Prime the store so the second subscription can serve from cache.
        store.streamData("k").test {
            var item = awaitItem()
            while (item.isEmpty) item = awaitItem()
            assertEquals(7, item.data)
            cancelAndIgnoreRemainingEvents()
        }

        val stream = store.asScreenStream(
            key = "k",
            networkMonitor = FakeNetworkMonitor(online),
            fetchedAtRepository = FakeFetchedAtRepository(),
            cacheKey = "test:while-subscribed-swr",
            scope = backgroundScope,
            fetchPolicy = FetchPolicy.CACHE_FIRST_SWR,
        )

        // Simulate a feature VM: stateIn(WhileSubscribed(5_000), Loading).
        val hot = stream.state.stateIn(
            backgroundScope,
            SharingStarted.WhileSubscribed(5_000),
            ScreenState.Loading,
        )

        // First subscription — an explicit collector that we can cancel to
        // drop subscriber count back to zero, allowing the
        // SharingStarted.WhileSubscribed(5_000) stop-timeout to fire.
        val firstSub: Job = backgroundScope.launch { hot.collect { /* keep sub alive */ } }
        val firstContent = hot.first { it !is ScreenState.Loading }
        val first = assertIs<ScreenState.Content<Int>>(firstContent)
        assertEquals(7, first.data)
        firstSub.cancel()
        // Give WhileSubscribed(5_000) time to observe zero subscribers and
        // cancel the upstream. The stop-timeout is 5 seconds — advance well
        // past it so the underlying collect coroutine has definitely stopped.
        advanceTimeBy(6_000)
        advanceUntilIdle()

        // Re-subscribe — the first non-Loading state must be Content served
        // from cache, NOT a fresh Loading spinner + delay. The value must
        // match the primed value; a second fetcher call is not required for
        // the fence to hold.
        val second = hot.first { it !is ScreenState.Loading }
        val content = assertIs<ScreenState.Content<Int>>(second)
        assertEquals(7, content.data)
    }
}
