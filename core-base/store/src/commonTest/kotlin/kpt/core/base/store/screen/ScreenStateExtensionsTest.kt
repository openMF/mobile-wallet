/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.store.screen

import app.cash.turbine.test
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ScreenStateExtensionsTest {

    @Test
    fun `mapContent transforms only Content state`() = runTest {
        val flow = flowOf(
            ScreenState.Content("hello"),
        )
        flow.mapContent { data, _ -> data.length }.test {
            val item = awaitItem()
            assertIs<ScreenState.Content<Int>>(item)
            assertEquals(5, item.data)
            // FreshnessSignal default for the initial Content is not-refreshing.
            assertEquals(false, item.freshnessSignal.isRefreshing)
            awaitComplete()
        }
    }

    @Test
    fun `mapContent passes through Loading`() = runTest {
        val flow = flowOf<ScreenState<String>>(ScreenState.Loading)
        flow.mapContent { data, _ -> data.length }.test {
            assertIs<ScreenState.Loading>(awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `mapContent passes through NoNetwork`() = runTest {
        val flow = flowOf<ScreenState<String>>(ScreenState.NoNetwork(isCaptivePortal = true))
        flow.mapContent { data, _ -> data.length }.test {
            val item = awaitItem()
            assertIs<ScreenState.NoNetwork>(item)
            assertTrue(item.isCaptivePortal)
            awaitComplete()
        }
    }

    @Test
    fun `emptyIfContent converts to Empty when predicate true`() = runTest {
        val flow = flowOf(
            ScreenState.Content(emptyList<String>()),
        )
        flow.emptyIfContent { it.isEmpty() }.test {
            assertIs<ScreenState.Empty>(awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `emptyIfContent keeps Content when predicate false`() = runTest {
        val flow = flowOf(
            ScreenState.Content(listOf("a")),
        )
        flow.emptyIfContent { it.isEmpty() }.test {
            val item = awaitItem()
            assertIs<ScreenState.Content<List<String>>>(item)
            assertEquals(listOf("a"), item.data)
            awaitComplete()
        }
    }

    @Test
    fun `combineContent merges external state with Content`() = runTest {
        val screenFlow = MutableStateFlow<ScreenState<List<String>>>(
            ScreenState.Content(listOf("a", "b", "c")),
        )
        val filterFlow = MutableStateFlow("a")

        screenFlow.combineContent(filterFlow) { data, filter, _ ->
            data.filter { it.contains(filter) }
        }.test {
            val item = awaitItem()
            assertIs<ScreenState.Content<List<String>>>(item)
            assertEquals(listOf("a"), item.data)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `dataOrNull returns data from Content`() {
        val state: ScreenState<String> = ScreenState.Content("hello")
        assertEquals("hello", state.dataOrNull)
    }

    @Test
    fun `dataOrNull returns null for Loading`() {
        val state: ScreenState<String> = ScreenState.Loading
        assertNull(state.dataOrNull)
    }

    @Test
    fun `hasContent returns true for Content`() {
        val state: ScreenState<String> = ScreenState.Content("data")
        assertTrue(state.hasContent)
    }

    @Test
    fun `mapError transforms error throwable`() = runTest {
        val original = RuntimeException("raw")
        val flow = flowOf<ScreenState<String>>(ScreenState.Error(original, isNetworkError = true))
        flow.mapError { IllegalStateException("mapped: ${it.message}") }.test {
            val item = awaitItem()
            assertIs<ScreenState.Error>(item)
            assertEquals("mapped: raw", item.error.message)
            assertTrue(item.isNetworkError)
            awaitComplete()
        }
    }

    @Test
    fun `asLocalScreenState wraps value in Content with default FreshnessSignal`() {
        val state = "hello".asLocalScreenState()
        assertIs<ScreenState.Content<String>>(state)
        assertEquals("hello", state.data)
        // FreshnessSignal default initial — Initial band, not refreshing.
        assertEquals(false, state.freshnessSignal.isRefreshing)
    }

    @Test
    fun `asLocalScreenStream emits Loading then Content`() = runTest {
        flowOf("result").asLocalScreenStream().test {
            assertIs<ScreenState.Loading>(awaitItem())
            val content = awaitItem()
            assertIs<ScreenState.Content<String>>(content)
            assertEquals("result", content.data)
            // Local-only wrap defaults to not-refreshing freshness signal.
            assertEquals(false, content.freshnessSignal.isRefreshing)
            awaitComplete()
        }
    }

    @Test
    fun `asLocalScreenStream catches upstream errors as Error state`() = runTest {
        val err = RuntimeException("upstream failure")
        flow<String> { throw err }.asLocalScreenStream().test {
            assertIs<ScreenState.Loading>(awaitItem())
            val errorState = awaitItem()
            assertIs<ScreenState.Error>(errorState)
            assertEquals("upstream failure", errorState.error.message)
            awaitComplete()
        }
    }
}
