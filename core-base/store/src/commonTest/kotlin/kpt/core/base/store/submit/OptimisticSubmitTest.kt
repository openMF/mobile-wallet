/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.store.submit

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Tests for [optimisticSubmit] — the local-first apply/network/rollback orchestration.
 *
 * Covers: happy path emits result + skips rollback; non-cancellation throwables
 * invoke rollback and re-throw; CancellationException is re-thrown without
 * rollback (scope cancellation should not look like a failure to the user);
 * RuntimeException (e.g. NPE / IllegalStateException) is treated identically
 * to checked-style throwables.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class OptimisticSubmitTest {

    // ─── T1: happy path ──────────────────────────────────────────────────────

    @Test
    fun `optimisticSubmit applies payload then returns network result on success`() = runTest {
        val handler = submitHandler<String>()
        var applied = false
        var rolledBack = false

        val result = handler.optimisticSubmit(
            payload = "like-42",
            apply = { _ -> applied = true },
            network = { _ -> "server-ok" },
            rollback = { _, _ -> rolledBack = true },
        )

        assertEquals("server-ok", result)
        assertTrue(applied, "apply must have been called")
        assertFalse(rolledBack, "rollback must NOT be called on success")
    }

    // ─── T2: server error → rollback ─────────────────────────────────────────

    @Test
    fun `optimisticSubmit rolls back and re-throws on network failure`() = runTest {
        class ServerError : RuntimeException("500")
        val handler = submitHandler<Unit>()
        val applied = mutableListOf<Int>()
        val rolledBack = mutableListOf<Int>()
        val capturedErrors = mutableListOf<Throwable>()

        var thrown: Throwable? = null
        try {
            handler.optimisticSubmit(
                payload = 7,
                apply = { p -> applied.add(p) },
                network = { _ -> throw ServerError() },
                rollback = { p, t ->
                    rolledBack.add(p)
                    capturedErrors.add(t)
                },
            )
            fail("expected ServerError to escape optimisticSubmit")
        } catch (t: ServerError) {
            thrown = t
        }

        assertNotNull(thrown)
        assertEquals(listOf(7), applied)
        assertEquals(listOf(7), rolledBack, "rollback must receive the same payload")
        assertEquals(1, capturedErrors.size)
        assertTrue(capturedErrors[0] === thrown, "rollback must receive the same throwable that escapes")
    }

    // ─── T3: cancellation propagates without rollback ────────────────────────

    @Test
    fun `optimisticSubmit re-throws CancellationException without invoking rollback`() = runTest {
        val handler = submitHandler<Unit>()
        var applied = false
        var rolledBack = false

        // CancellationException is caught here explicitly so the test scope itself
        // isn't torn down — we just want to assert it escapes optimisticSubmit and
        // that rollback was NOT invoked.
        var caught: CancellationException? = null
        try {
            handler.optimisticSubmit(
                payload = Unit,
                apply = { applied = true },
                network = { throw CancellationException("scope torn down") },
                rollback = { _, _ -> rolledBack = true },
            )
            fail("expected CancellationException to escape optimisticSubmit")
        } catch (e: CancellationException) {
            caught = e
        }

        assertNotNull(caught, "CancellationException must escape")
        assertTrue(applied, "apply runs before network — must have fired")
        assertFalse(rolledBack, "CancellationException must NOT trigger rollback")
    }

    // ─── T4: arbitrary RuntimeException still triggers rollback ──────────────

    @Test
    fun `optimisticSubmit rolls back on RuntimeException`() = runTest {
        class WeirdRuntimeError : RuntimeException("boom")
        val handler = submitHandler<Unit>()
        var rolledBack = false

        var thrown: Throwable? = null
        try {
            handler.optimisticSubmit(
                payload = "x",
                apply = { /* no-op */ },
                network = { throw WeirdRuntimeError() },
                rollback = { _, _ -> rolledBack = true },
            )
            fail("expected WeirdRuntimeError to escape")
        } catch (t: WeirdRuntimeError) {
            thrown = t
        }

        assertNotNull(thrown)
        assertTrue(rolledBack, "rollback must fire on any non-cancellation throwable")
    }
}
