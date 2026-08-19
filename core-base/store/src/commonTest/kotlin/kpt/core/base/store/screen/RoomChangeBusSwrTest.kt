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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import kpt.core.base.store.fixtures.FakeNetworkMonitor
import kpt.core.base.store.infra.FakeFetchedAtRepository
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.StoreBuilder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.ExperimentalTime

/**
 * Integration test locking the "post-mutation freshness" invariant: with
 * [FetchPolicy.CACHE_FIRST_SWR] active, when the SourceOfTruth flow re-emits
 * (i.e. what `RoomChangeBus` + `notifyingWrite { }` + `daoFlow { }` produce in
 * production), the SWR stream reflects the mutated row on a subsequent
 * subscription — no manual `refresh()` required.
 *
 * **Test-seam decision:** the real `RoomChangeBus` / `daoFlow { }` /
 * `notifyingWrite { }` invalidation triad lives in `core-base/database`
 * (`core-base/database/src/commonMain/kotlin/kpt/core/base/database/invalidation/`)
 * which is NOT on `core-base/store`'s commonTest classpath (see
 * `core-base/store/build.gradle.kts` — only kotlin-test, coroutines-test,
 * and turbine are declared). To keep this canary hermetic to the store
 * module we simulate the same re-emission contract with a
 * `MutableStateFlow`-backed [SourceOfTruth] reader — the shape the DAO Flow
 * emits after `RoomChangeBus.notifyChange(...)` fires. A follow-up plan
 * that adds a `core-base/store <-> core-base/database` integration source
 * set (or an `integrationTest` module that depends on both) can replace this
 * with a real `RoomChangeBus` wiring without changing the assertions.
 */
@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
class RoomChangeBusSwrTest {

    private val onlineInfo = NetworkInfo(type = NetworkType.WiFi, isMetered = false)
    private val online = NetworkStatus.Available(onlineInfo)

    @Test
    fun notifyingWriteTriggersDaoFlowReEmissionUnderSwr() = runTest {
        // Simulates the RoomChangeBus contract with a MutableStateFlow —
        // updating the flow's value replays the same re-emission the DAO
        // Flow performs after `notifyingWrite { }` invalidates the observed
        // key set.
        val sotState = MutableStateFlow(listOf("A"))
        val store = StoreBuilder
            .from<String, List<String>, List<String>>(
                fetcher = Fetcher.of { _ -> sotState.value },
                sourceOfTruth = SourceOfTruth.of(
                    reader = { _ ->
                        // Emit the current value AND continue emitting on
                        // every state-flow change — the DAO Flow contract
                        // that `notifyingWrite { }` triggers.
                        flow {
                            sotState.collect { emit(it) }
                        }
                    },
                    writer = { _, value -> sotState.value = value },
                ),
            )
            .build()

        // Prime the store so the SoT has a value on disk.
        store.streamData("loans").test {
            var item = awaitItem()
            while (item.isEmpty) item = awaitItem()
            assertEquals(listOf("A"), item.data)
            cancelAndIgnoreRemainingEvents()
        }

        val stream = store.asScreenStream(
            key = "loans",
            networkMonitor = FakeNetworkMonitor(online),
            fetchedAtRepository = FakeFetchedAtRepository(),
            cacheKey = "test:swr-room-change-bus",
            scope = backgroundScope,
            fetchPolicy = FetchPolicy.CACHE_FIRST_SWR,
        )

        stream.state.test {
            // Drain to the first Content emission.
            var state: ScreenState<List<String>> = awaitItem()
            while (state is ScreenState.Loading) state = awaitItem()
            val first = assertIs<ScreenState.Content<List<String>>>(state)
            assertEquals(listOf("A"), first.data)

            // Simulate `notifyingWrite { dao.insert("B") }` — the DAO Flow
            // re-emits with the mutated row set.
            sotState.value = listOf("A", "B")

            // With CACHE_FIRST_SWR consuming the SoT flow, the downstream
            // Content emission must reflect the mutation without any
            // explicit `stream.refresh()` call.
            var next: ScreenState<List<String>> = awaitItem()
            while (next is ScreenState.Loading || (next is ScreenState.Content && next.data == listOf("A"))) {
                next = awaitItem()
            }
            val mutated = assertIs<ScreenState.Content<List<String>>>(next)
            assertEquals(listOf("A", "B"), mutated.data)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
