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

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart

/**
 * Wraps a Room DAO `Flow` so it re-queries whenever [RoomChangeBus] reports that any of
 * the [tables] has been written. The initial subscription emits immediately so the first
 * query runs without waiting for a write — preserving Room's natural "fresh subscriber
 * sees current state" semantics.
 *
 * Use anywhere a repository or Store factory currently returns `dao.observeXxx()`:
 *
 * ```kotlin
 * // Before:
 * override fun observeUpcoming(window: Set<Int>) =
 *     billReminderDao.observeUpcoming(window).map { it.map(Entity::toDomain) }
 *
 * // After:
 * override fun observeUpcoming(window: Set<Int>) =
 *     daoFlow("banking_bill_reminders") { billReminderDao.observeUpcoming(window) }
 *         .map { it.map(Entity::toDomain) }
 * ```
 *
 * The wrapper is platform-agnostic: on Android/Desktop/iOS it is redundant with Room's
 * own InvalidationTracker (both emit; the downstream collector dedupes via state equality).
 * On wasmJs it is the only reliable propagation path until Room 3 stable lands.
 *
 * **Subscribe-time behavior** — `flatMapLatest` cancels the previous inner `block()` Flow
 * whenever a new signal arrives. The inner Flow is therefore expected to be a cold
 * Room `Flow` (created on each subscription), not a hot shared one. All `Dao.observeXxx`
 * methods returning `Flow<T>` satisfy this — they are cold per Room's contract.
 *
 * **Backpressure** — sustained write bursts collapse into the most recent value because
 * `flatMapLatest` only retains the latest. This matches Room's own semantics under load.
 *
 * @param tables Room table names this flow's underlying SQL reads from. Pass every table
 *   touched by the inner query (joins, sub-selects). The signal filter is `any in
 *   watched`, so over-declaring is safe; under-declaring causes missed re-emissions.
 * @param block Factory for the underlying Room `Flow`. Called once on initial subscribe
 *   and again on every matching signal.
 */
@OptIn(ExperimentalCoroutinesApi::class)
public fun <T> daoFlow(
    vararg tables: String,
    block: () -> Flow<T>,
): Flow<T> {
    require(tables.isNotEmpty()) {
        "daoFlow requires at least one table name — pass the Room @Entity tableName(s) " +
            "your query reads from. Empty tables list means the flow would never re-emit."
    }
    val watched = tables.toSet()
    return RoomChangeBus.signal
        .filter { changed -> changed.any { it in watched } }
        .onStart { emit(emptySet()) } // kick-start: first subscribe → first query
        .flatMapLatest { block() }
}
