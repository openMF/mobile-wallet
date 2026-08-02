/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.store.infra

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

/**
 * Tests for the four shipping [ConflictStrategy] variants.
 *
 * Two tests per variant: a happy-path and one edge case that verifies the
 * specified tie-breaking / determinism contract.
 */
class ConflictStrategyTest {

    private data class Record(val id: String, val updatedAt: Long, val tags: List<String> = emptyList())

    // ─── ServerWins ──────────────────────────────────────────────────────────

    @Test
    fun `ServerWins returns server value`() {
        val strategy = ConflictStrategy.ServerWins<Record>()
        val server = Record(id = "S", updatedAt = 100L)
        val client = Record(id = "C", updatedAt = 200L)

        assertEquals(server, strategy.resolve(server, client))
    }

    @Test
    fun `ServerWins returns server even when client is newer`() {
        val strategy = ConflictStrategy.ServerWins<Record>()
        val server = Record(id = "S", updatedAt = 1L)
        val client = Record(id = "C", updatedAt = 9_999L)

        // Authority wins regardless of recency
        assertSame(server, strategy.resolve(server, client))
    }

    // ─── ClientWins ──────────────────────────────────────────────────────────

    @Test
    fun `ClientWins returns client value`() {
        val strategy = ConflictStrategy.ClientWins<Record>()
        val server = Record(id = "S", updatedAt = 200L)
        val client = Record(id = "C", updatedAt = 100L)

        assertEquals(client, strategy.resolve(server, client))
    }

    @Test
    fun `ClientWins returns client even when server is newer`() {
        val strategy = ConflictStrategy.ClientWins<Record>()
        val server = Record(id = "S", updatedAt = 9_999L)
        val client = Record(id = "C", updatedAt = 1L)

        assertSame(client, strategy.resolve(server, client))
    }

    // ─── LastWriteWins ───────────────────────────────────────────────────────

    @Test
    fun `LastWriteWins picks the side with the larger timestamp`() {
        val strategy = ConflictStrategy.LastWriteWins<Record> { it.updatedAt }
        val server = Record(id = "S", updatedAt = 100L)
        val client = Record(id = "C", updatedAt = 200L)

        assertEquals(client, strategy.resolve(server, client))
    }

    @Test
    fun `LastWriteWins ties go to server`() {
        // Defensive — repeated sync cycles must converge; tie-go-server preserves the
        // canonical SoT so subsequent equality checks stop diverging.
        val strategy = ConflictStrategy.LastWriteWins<Record> { it.updatedAt }
        val server = Record(id = "S", updatedAt = 500L)
        val client = Record(id = "C", updatedAt = 500L)

        assertSame(server, strategy.resolve(server, client))
    }

    // ─── Merge ───────────────────────────────────────────────────────────────

    @Test
    fun `Merge invokes the caller-supplied merger`() {
        val strategy = ConflictStrategy.Merge<Record> { s, c ->
            // Union of tags, max updatedAt, server's id (authority for identity)
            s.copy(
                tags = (s.tags + c.tags).distinct(),
                updatedAt = maxOf(s.updatedAt, c.updatedAt),
            )
        }
        val server = Record(id = "S", updatedAt = 100L, tags = listOf("a", "b"))
        val client = Record(id = "C", updatedAt = 200L, tags = listOf("b", "c"))

        val resolved = strategy.resolve(server, client)
        assertEquals("S", resolved.id, "id must come from server (authority for identity)")
        assertEquals(200L, resolved.updatedAt, "updatedAt must be max")
        assertEquals(listOf("a", "b", "c"), resolved.tags, "tags must be union")
    }

    @Test
    fun `Merge is deterministic for identical inputs`() {
        var counter = 0
        val strategy = ConflictStrategy.Merge<Record> { s, c ->
            counter++
            s.copy(updatedAt = maxOf(s.updatedAt, c.updatedAt))
        }
        val server = Record(id = "S", updatedAt = 100L)
        val client = Record(id = "C", updatedAt = 200L)

        val first = strategy.resolve(server, client)
        val second = strategy.resolve(server, client)

        assertEquals(first, second, "Merge must produce equal results for equal inputs")
        assertEquals(2, counter, "merger lambda must have fired exactly once per call")
    }
}
