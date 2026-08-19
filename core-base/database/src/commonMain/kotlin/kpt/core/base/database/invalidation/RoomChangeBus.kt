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

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Project-wide write-notification bus that delivers a deterministic "tables changed" signal
 * to subscribers immediately after a repository write commits.
 *
 * **Why this exists** — On wasmJs (single-threaded JS event loop) Room 3 alpha05's
 * `InvalidationTracker.refreshAsync()` schedules its fan-out via
 * `database.getCoroutineScope().launch { ... }`, which on a single-threaded runtime is
 * subject to scheduling races and an internal `AtomicBoolean pendingRefresh` gate that
 * can stay stuck. Long-lived `Flow` collectors (e.g. a Home dashboard `combine(...)`)
 * may never see the post-write re-emission. On Android/Desktop/iOS the same code works
 * because real parallel threads run the refresh concurrently with the writer.
 *
 * This bus is a parallelism-independent fallback signal. Repositories call [notify]
 * after every write; readers wrapping their Room flows with `daoFlow("table") { ... }`
 * re-emit on matching signals. Subscribers run on the same coroutine as the publisher's
 * next microtask — deterministic, no separate launch, no AtomicBoolean gate.
 *
 * **Lifecycle**: process-wide singleton. No init, no teardown. Safe to import from anywhere.
 *
 * **Cost on other platforms**: a `tryEmit` to a `SharedFlow` with 0 collectors is a few
 * nanoseconds; with N collectors it's an O(N) walk. Negligible.
 *
 * **Removal plan**: when Room 3 stable proves InvalidationTracker is reliable on wasmJs,
 * convert [notify], `daoFlow {}`, and `notifyingWrite {}` to no-op pass-throughs (each
 * is one file), then codemod-remove the call sites at consumer-app convenience.
 * No behavior risk because the bus runs alongside Room's own tracker, not in place of it.
 *
 * See `core-base/database/.../invalidation/README.md` for the full rationale and the
 * integration recipe for new features.
 */
public object RoomChangeBus {

    private val _signal = MutableSharedFlow<Set<String>>(
        replay = 0,
        extraBufferCapacity = SIGNAL_BUFFER_CAPACITY,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    /**
     * Stream of "tables modified" signals. Each emission is the set of table names whose
     * contents may have changed. Consumers that observe a specific table filter for
     * `it.contains("my_table")` (or use [daoFlow], which does this automatically).
     */
    public val signal: SharedFlow<Set<String>> = _signal.asSharedFlow()

    /** Single-table convenience. Equivalent to `notify(setOf(table))`. */
    public fun notify(table: String) {
        _signal.tryEmit(setOf(table))
    }

    /** Multi-table notification. Use when one transaction touches several tables. */
    public fun notify(tables: Set<String>) {
        if (tables.isEmpty()) return
        _signal.tryEmit(tables)
    }

    /**
     * Capacity reserves room for bursts of writes (e.g. batch upserts) without dropping
     * signals on slow collectors. DROP_OLDEST means under sustained overflow we lose
     * older signals first — this is correct because each signal carries a table-name
     * set, and the next signal will re-trigger the same collectors anyway. The cost is
     * a marginally delayed UI refresh, never a missed final state.
     */
    private const val SIGNAL_BUFFER_CAPACITY = 64
}
