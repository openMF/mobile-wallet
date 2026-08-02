/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.store.push

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.mobilenativefoundation.store.core5.ExperimentalStoreApi
import org.mobilenativefoundation.store.store5.MutableStore
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import org.mobilenativefoundation.store.store5.StoreReadResponseOrigin
import org.mobilenativefoundation.store.store5.StoreWriteRequest
import org.mobilenativefoundation.store.store5.StoreWriteResponse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Verifies that [PushStoreAdapter] correctly translates each [PushEvent] variant into
 * the right [MutableStore] call, and that the [PushMergeStrategy] is honoured for
 * `Update` events.
 *
 * Uses a hand-rolled [FakeMutableStore] rather than spinning up a real Store5 instance
 * (the Store5 builder pipeline pulls in coroutine flows, network monitor, source-of-truth
 * persistence, and a memory policy — overkill for the dispatcher-level contract under test).
 */
@OptIn(ExperimentalCoroutinesApi::class, ExperimentalStoreApi::class)
class PushStoreAdapterTest {

    @Test
    fun `Insert event dispatches store write with the incoming value`() = runTest {
        val store = FakeMutableStore<String, String>()
        val adapter = PushStoreAdapter(
            store = store,
            events = flowOf<PushEvent<String, String>>(PushEvent.Insert("k1", "v1")),
            scope = TestScope(testScheduler),
        )

        adapter.start().join()

        assertEquals("v1", store.values["k1"])
        assertEquals(1, store.writes.size)
        assertTrue(store.cleared.isEmpty())
    }

    @Test
    fun `Update event with default Replace strategy overwrites the existing value`() = runTest {
        val store = FakeMutableStore<String, String>().apply { values["k1"] = "old" }
        val adapter = PushStoreAdapter(
            store = store,
            events = flowOf<PushEvent<String, String>>(PushEvent.Update("k1", "new")),
            mergeStrategy = PushMergeStrategy.Replace,
            scope = TestScope(testScheduler),
        )

        adapter.start().join()

        assertEquals("new", store.values["k1"])
    }

    @Test
    fun `Delete event clears the key from the store`() = runTest {
        val store = FakeMutableStore<String, String>().apply { values["k1"] = "to-go" }
        val adapter = PushStoreAdapter(
            store = store,
            events = flowOf<PushEvent<String, String>>(PushEvent.Delete("k1")),
            scope = TestScope(testScheduler),
        )

        adapter.start().join()

        assertNull(store.values["k1"])
        assertEquals(listOf("k1"), store.cleared)
    }

    @Test
    fun `Update event with Merge strategy invokes merger when readCurrent returns a value`() = runTest {
        val store = FakeMutableStore<String, String>().apply { values["k1"] = "head:" }
        var mergerInvoked = false
        val adapter = PushStoreAdapter(
            store = store,
            events = flowOf<PushEvent<String, String>>(PushEvent.Update("k1", "tail")),
            mergeStrategy = PushMergeStrategy.Merge<String> { existing, incoming ->
                mergerInvoked = true
                existing + incoming
            },
            scope = TestScope(testScheduler),
            readCurrent = { key -> store.values[key] },
        )

        adapter.start().join()

        assertTrue(mergerInvoked, "merger lambda must be invoked when readCurrent returns non-null")
        assertEquals("head:tail", store.values["k1"])
    }

    @Test
    fun `multiple events are processed in order`() = runTest {
        val store = FakeMutableStore<String, Int>()
        val events: Flow<PushEvent<String, Int>> = flowOf(
            PushEvent.Insert("a", 1),
            PushEvent.Insert("b", 2),
            PushEvent.Update("a", 10),
            PushEvent.Delete("b"),
        )
        val adapter = PushStoreAdapter(
            store = store,
            events = events,
            scope = TestScope(testScheduler),
        )

        adapter.start().join()

        assertEquals(10, store.values["a"])
        assertNull(store.values["b"])
        assertEquals(3, store.writes.size) // Insert a, Insert b, Update a
        assertEquals(listOf("b"), store.cleared)
    }
}

/**
 * Minimal in-memory [MutableStore] for tests — implements only the surface that
 * [PushStoreAdapter] actually touches (`write` + `clear(key)`). Read APIs return empty
 * flows; clear-all and conflict-resolution streams are unused.
 */
@OptIn(ExperimentalStoreApi::class)
private class FakeMutableStore<K : Any, V : Any> : MutableStore<K, V> {

    val values: MutableMap<K, V> = mutableMapOf()
    val writes: MutableList<Pair<K, V>> = mutableListOf()
    val cleared: MutableList<K> = mutableListOf()

    @Suppress("UNCHECKED_CAST")
    override suspend fun <Response : Any> write(
        request: StoreWriteRequest<K, V, Response>,
    ): StoreWriteResponse {
        values[request.key] = request.value
        writes += request.key to request.value
        return StoreWriteResponse.Success.Typed(request.value as Response)
    }

    override fun <Response : Any> stream(
        requestStream: Flow<StoreWriteRequest<K, V, Response>>,
    ): Flow<StoreWriteResponse> = MutableSharedFlow()

    @Suppress("UNCHECKED_CAST")
    override fun <Response : Any> stream(
        request: StoreReadRequest<K>,
    ): Flow<StoreReadResponse<V>> =
        MutableStateFlow<StoreReadResponse<V>>(StoreReadResponse.Loading(StoreReadResponseOrigin.Cache))

    override suspend fun clear(key: K) {
        values.remove(key)
        cleared += key
    }
}
