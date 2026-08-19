/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.database.invalidation

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Verifies the Room invalidation bridge primitives — `RoomChangeBus`, `daoFlow {}`,
 * `notifyingWrite {}`. Pure coroutines tests; no Room database required.
 *
 * **Test pattern** — each test uses a [ColdFlowFactory] that emits a single snapshot
 * and completes (mimics a Room DAO `Flow` whose underlying SQL is re-run on every
 * subscription). The factory records how many times it was called, so tests verify
 * "did the bus signal trigger a re-query?" by inspecting the call count rather than
 * by mutating a hot StateFlow (which would emit independently of bus signals and
 * conflate the source's own reactivity with the bridge's re-query trigger).
 *
 * The bus is a process-wide singleton, but `MutableSharedFlow(replay = 0)` means new
 * subscribers don't see emissions made before they subscribed, so cross-test pollution
 * is bounded to within a single test's lifetime.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class RoomChangeBusTest {

    // -------------------------------------------------------------------------
    // daoFlow contract
    // -------------------------------------------------------------------------

    @Test
    fun daoFlow_emitsInitialQuery_onSubscribe_withoutWaitingForSignal() = runTest {
        val source = ColdFlowFactory(snapshots = listOf("initial"))
        daoFlow("table_a", block = source).test {
            assertEquals("initial", awaitItem())
            assertEquals(1, source.callCount, "initial subscribe should query exactly once")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun daoFlow_reQueries_whenBusNotifiesMatchingTable() = runTest {
        val source = ColdFlowFactory(snapshots = listOf("v1", "v2"))
        daoFlow("table_b", block = source).test {
            assertEquals("v1", awaitItem())                  // initial query → snapshot[0]
            RoomChangeBus.notify("table_b")
            assertEquals("v2", awaitItem())                  // signal → re-query → snapshot[1]
            assertEquals(2, source.callCount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun daoFlow_ignoresSignalsForUnrelatedTables() = runTest {
        val source = ColdFlowFactory(snapshots = listOf("only"))
        daoFlow("table_c", block = source).test {
            assertEquals("only", awaitItem())
            RoomChangeBus.notify("some_other_table")
            expectNoEvents()
            assertEquals(1, source.callCount, "unrelated signal must not re-query")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun daoFlow_reEmits_whenAnyOfMultipleWatchedTablesIsNotified() = runTest {
        val source = ColdFlowFactory(snapshots = listOf("v0", "v1", "v2"))
        daoFlow("table_d", "table_e", block = source).test {
            assertEquals("v0", awaitItem())
            RoomChangeBus.notify("table_e")                  // second-listed table
            assertEquals("v1", awaitItem())
            RoomChangeBus.notify("table_d")                  // first-listed table
            assertEquals("v2", awaitItem())
            assertEquals(3, source.callCount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun daoFlow_reEmits_whenMultiTableSignalContainsAtLeastOneWatchedTable() = runTest {
        val source = ColdFlowFactory(snapshots = listOf("before", "after"))
        daoFlow("table_f", block = source).test {
            assertEquals("before", awaitItem())
            RoomChangeBus.notify(setOf("table_unrelated", "table_f", "table_other"))
            assertEquals("after", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun daoFlow_requiresAtLeastOneTable() {
        assertFailsWith<IllegalArgumentException> {
            daoFlow<Int>(block = { error("unreached") })
        }
    }

    // -------------------------------------------------------------------------
    // notifyingWrite contract
    // -------------------------------------------------------------------------

    @Test
    fun notifyingWrite_publishesSignal_afterSuccessfulBlock() = runTest {
        val source = ColdFlowFactory(snapshots = listOf("pre-write", "post-write"))
        daoFlow("table_g", block = source).test {
            assertEquals("pre-write", awaitItem())
            notifyingWrite("table_g") { /* simulate DAO write */ }
            assertEquals("post-write", awaitItem())
            assertEquals(2, source.callCount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun notifyingWrite_doesNotPublish_whenBlockThrows() = runTest {
        val source = ColdFlowFactory(snapshots = listOf("untouched"))
        daoFlow("table_h", block = source).test {
            assertEquals("untouched", awaitItem())

            assertFailsWith<IllegalStateException> {
                notifyingWrite("table_h") {
                    error("constraint violation")
                }
            }

            // Failed write must not fire bus signal → collector must not re-query.
            expectNoEvents()
            assertEquals(1, source.callCount, "throwing write must not trigger a re-query")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun notifyingWrite_returnsBlockResult() = runTest {
        val rowId = notifyingWrite("table_i") { 12345L }
        assertEquals(12345L, rowId)
    }

    @Test
    fun notifyingWrite_singleTableFastPath_publishesEquivalentSignal() = runTest {
        val source = ColdFlowFactory(snapshots = listOf("v0", "v1", "v2"))
        daoFlow("table_j", block = source).test {
            assertEquals("v0", awaitItem())

            // Single-table call (fast path: notify(String)).
            notifyingWrite("table_j") { /* write */ }
            assertEquals("v1", awaitItem())

            // Multi-table call (slow path: notify(Set)).
            notifyingWrite("table_j", "table_unrelated") { /* write */ }
            assertEquals("v2", awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun notifyingWrite_requiresAtLeastOneTable() = runTest {
        assertFailsWith<IllegalArgumentException> {
            notifyingWrite<Unit>(tables = emptyArray()) { /* unreached */ }
        }
    }

    // -------------------------------------------------------------------------
    // Bus signal shape
    // -------------------------------------------------------------------------

    @Test
    fun bus_emitsExactTableSet_onMultiTableNotify() = runTest {
        RoomChangeBus.signal.test {
            val tables = setOf("a", "b", "c")
            RoomChangeBus.notify(tables)
            assertEquals(tables, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun bus_dropsEmptyNotifySilently() = runTest {
        RoomChangeBus.signal.test {
            RoomChangeBus.notify(emptySet())
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
        assertTrue(true) // explicit completion sentinel
    }
}

// ---------------------------------------------------------------------------
// Test helper — cold Flow factory that returns one snapshot per call and
// records the call count, simulating a Room DAO `Flow` whose underlying SQL
// is re-run on every fresh subscription.
// ---------------------------------------------------------------------------

private class ColdFlowFactory<T>(private val snapshots: List<T>) : () -> Flow<T> {
    var callCount: Int = 0
        private set

    override fun invoke(): Flow<T> {
        val snapshot = snapshots[callCount.coerceAtMost(snapshots.lastIndex)]
        callCount++
        return flowOf(snapshot)
    }
}
