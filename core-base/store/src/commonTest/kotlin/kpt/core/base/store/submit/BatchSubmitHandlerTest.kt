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

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Tests for [BatchSubmitHandler] — verifies AllOrNothing fail-fast semantics, Independent
 * best-effort semantics, idempotency-key propagation, and edge cases (empty input,
 * full failure).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class BatchSubmitHandlerTest {

    // ─── T1: AllOrNothing happy path ─────────────────────────────────────────

    @Test
    fun `AllOrNothing all payloads succeed produces full success result`() = runTest {
        val handler = BatchSubmitHandler<Int>(TestScope(testScheduler), BatchSubmitMode.AllOrNothing)
        val attempted = mutableListOf<Int>()

        val result = handler.submitBatch(listOf(1, 2, 3)) { _, payload, _ ->
            attempted += payload
        }

        assertEquals(3, result.total)
        assertEquals(3, result.succeeded)
        assertEquals(0, result.failed)
        assertTrue(result.failures.isEmpty())
        assertFalse(result.aborted)
        assertTrue(result.isFullSuccess)
        assertEquals(listOf(1, 2, 3), attempted)
    }

    // ─── T2: AllOrNothing fail-fast ──────────────────────────────────────────

    @Test
    fun `AllOrNothing first failure aborts remaining payloads`() = runTest {
        val handler = BatchSubmitHandler<Int>(TestScope(testScheduler), BatchSubmitMode.AllOrNothing)
        val attempted = mutableListOf<Int>()

        val result = handler.submitBatch(listOf(1, 2, 3, 4)) { index, payload, _ ->
            attempted += payload
            if (index == 1) throw RuntimeException("boom")
        }

        assertEquals(4, result.total)
        assertEquals(1, result.succeeded, "only payload 1 succeeded before abort")
        assertEquals(1, result.failed)
        assertEquals(1, result.failures.size)
        assertEquals(1, result.failures[0].first, "failure index must match the original payload position")
        assertEquals("boom", result.failures[0].second.message)
        assertTrue(result.aborted)
        assertEquals(listOf(1, 2), attempted, "payloads after the failure must NOT be attempted")
    }

    // ─── T3: Independent happy path ──────────────────────────────────────────

    @Test
    fun `Independent all payloads succeed produces full success result`() = runTest {
        val handler = BatchSubmitHandler<String>(TestScope(testScheduler), BatchSubmitMode.Independent)
        val attempted = mutableListOf<String>()

        val result = handler.submitBatch(listOf("a", "b", "c")) { _, payload, _ ->
            attempted += payload
        }

        assertEquals(3, result.total)
        assertEquals(3, result.succeeded)
        assertEquals(0, result.failed)
        assertFalse(result.aborted)
        assertEquals(listOf("a", "b", "c"), attempted)
    }

    // ─── T4: Independent partial failure ─────────────────────────────────────

    @Test
    fun `Independent attempts every payload and reports per-index failures`() = runTest {
        val handler = BatchSubmitHandler<Int>(TestScope(testScheduler), BatchSubmitMode.Independent)
        val attempted = mutableListOf<Int>()

        val result = handler.submitBatch(listOf(10, 20, 30, 40)) { index, payload, _ ->
            attempted += payload
            if (index == 1 || index == 3) error("fail $index")
        }

        assertEquals(4, result.total)
        assertEquals(2, result.succeeded)
        assertEquals(2, result.failed)
        assertFalse(result.aborted, "Independent never aborts")
        assertEquals(listOf(10, 20, 30, 40), attempted, "every payload is attempted")
        assertEquals(listOf(1, 3), result.failures.map { it.first })
        assertEquals(listOf("fail 1", "fail 3"), result.failures.map { it.second.message })
    }

    // ─── T5: Independent all-fail ────────────────────────────────────────────

    @Test
    fun `Independent every payload fails returns isFullFailure`() = runTest {
        val handler = BatchSubmitHandler<Int>(TestScope(testScheduler), BatchSubmitMode.Independent)

        val result = handler.submitBatch(listOf(1, 2, 3)) { _, _, _ ->
            throw RuntimeException("always-fail")
        }

        assertEquals(3, result.total)
        assertEquals(0, result.succeeded)
        assertEquals(3, result.failed)
        assertTrue(result.isFullFailure)
        assertFalse(result.isFullSuccess)
    }

    // ─── T6: empty batch ─────────────────────────────────────────────────────

    @Test
    fun `empty batch returns zero counts with full success flag`() = runTest {
        val handler = BatchSubmitHandler<Int>(TestScope(testScheduler))

        val result = handler.submitBatch(emptyList<Int>()) { _, _, _ ->
            error("submitBlock must not be called for an empty batch")
        }

        assertEquals(0, result.total)
        assertEquals(0, result.succeeded)
        assertEquals(0, result.failed)
        assertTrue(result.failures.isEmpty())
        assertFalse(result.aborted)
        assertTrue(result.isFullSuccess, "empty batch is treated as trivially complete")
        assertFalse(result.isFullFailure, "isFullFailure requires total > 0")
    }

    // ─── T7: idempotency-key propagation (bonus) ─────────────────────────────

    @Test
    fun `submitBlock receives the same idempotency key for every payload in the batch`() = runTest {
        val handler = BatchSubmitHandler<Int>(TestScope(testScheduler))
        val keysSeen = mutableSetOf<String>()

        val provided = "test-key-12345"
        handler.submitBatch(listOf(1, 2, 3), idempotencyKey = provided) { _, _, key ->
            keysSeen += key
        }

        assertEquals(setOf(provided), keysSeen, "every payload sees the same key per batch")
    }

    @Test
    fun `default idempotency key is auto-generated UUID-shaped string`() = runTest {
        val handler = BatchSubmitHandler<Int>(TestScope(testScheduler))
        var observed: String? = null

        handler.submitBatch(listOf(1)) { _, _, key -> observed = key }

        val key = assertNotNull(observed)
        // Canonical UUID is 36 chars including hyphens (8-4-4-4-12).
        assertEquals(36, key.length, "default key should be a canonical UUID v4")
        assertTrue(key.contains('-'), "canonical UUID contains hyphens")
    }
}
