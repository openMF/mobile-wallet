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

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class FakeScreenDataStreamTest {

    @Test
    fun emittedStatesAreCollected() = runTest {
        val fake = FakeScreenDataStream<String>()
        val states = mutableListOf<ScreenState<String>>()

        val job = launch { fake.state.take(2).toList(states) }
        yield() // let collector subscribe before emitting

        fake.emit(ScreenState.Loading)
        fake.emit(ScreenState.Content("hello"))

        job.join()
        assertIs<ScreenState.Loading>(states[0])
        assertIs<ScreenState.Content<String>>(states[1])
        assertEquals("hello", (states[1] as ScreenState.Content).data)
    }

    @Test
    fun tryEmitReturnsTrueWhenBufferHasSpace() = runTest {
        val fake = FakeScreenDataStream<Int>()
        assertTrue(fake.tryEmit(ScreenState.Loading))
    }

    @Test
    fun replayOneDeliversLatestStateToLateCollector() = runTest {
        val fake = FakeScreenDataStream<String>()
        fake.emit(ScreenState.Content("latest"))

        val state = fake.state.first { it is ScreenState.Content }
        assertIs<ScreenState.Content<String>>(state)
        assertEquals("latest", state.data)
    }

    @Test
    fun multipleEmissionsDeliveredInOrder() = runTest {
        val fake = FakeScreenDataStream<String>()
        val states = mutableListOf<ScreenState<String>>()

        val job = launch { fake.state.take(3).toList(states) }
        yield() // let collector subscribe before emitting

        fake.emit(ScreenState.Loading)
        fake.emit(ScreenState.NoNetwork())
        fake.emit(ScreenState.Content("data"))

        job.join()
        assertIs<ScreenState.Loading>(states[0])
        assertIs<ScreenState.NoNetwork>(states[1])
        assertIs<ScreenState.Content<String>>(states[2])
    }

    @Test
    fun asStreamStateMatchesFakeState() = runTest {
        val fake = FakeScreenDataStream<String>()
        fake.emit(ScreenState.Content("data"))

        val fromFake = fake.state.first { it is ScreenState.Content }
        val fromStream = fake.asStream.state.first { it is ScreenState.Content }
        assertEquals(fromFake, fromStream)
    }

    @Test
    fun retryAndRefreshDoNotThrow() = runTest {
        val fake = FakeScreenDataStream<String>()
        fake.asStream.retry()
        fake.asStream.refresh()
    }
}
