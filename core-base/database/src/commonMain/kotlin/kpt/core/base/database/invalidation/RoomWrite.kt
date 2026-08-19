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

/**
 * Wraps a Room write so [RoomChangeBus] is notified iff the write completes successfully.
 * If [block] throws (e.g. constraint violation, SQLite busy), no signal is emitted — the
 * tables haven't actually changed, so collectors aren't woken to re-query stale state.
 *
 * Use anywhere a repository currently calls `dao.upsert(...)` / `dao.deleteById(...)` etc:
 *
 * ```kotlin
 * // Before:
 * override suspend fun upsert(bill: BillReminder) {
 *     billReminderDao.upsert(bill.toEntity())
 * }
 *
 * // After:
 * override suspend fun upsert(bill: BillReminder) {
 *     notifyingWrite("banking_bill_reminders") {
 *         billReminderDao.upsert(bill.toEntity())
 *     }
 * }
 * ```
 *
 * Multi-table writes pass all touched tables — the signal carries the full set so any
 * `daoFlow(...)` watching any of them re-emits exactly once:
 *
 * ```kotlin
 * notifyingWrite("banking_loans", "banking_loan_payments") {
 *     loanDao.upsertWithPayment(loan, payment)
 * }
 * ```
 *
 * **Atomicity** — `RoomChangeBus.notify` runs after `block()` returns. If the underlying
 * write is itself transactional (Room's `@Transaction` or `db.useWriterConnection`), the
 * notification reflects post-commit state, not in-flight mutations. This matches what
 * subscribers want: re-query and read the new committed rows.
 *
 * **Inline + crossinline** — zero overhead. The compiler inlines this entirely so the
 * call site looks identical to a bare `block()` plus a single `RoomChangeBus.notify(...)`.
 *
 * @param tables Room table names this write modifies. See [daoFlow] for the same
 *   over-declare-is-safe / under-declare-loses-emissions contract.
 * @param block The actual DAO write. May suspend.
 * @return Whatever [block] returns. Most DAO writes return `Unit`; `@Upsert` returning
 *   `Long` (rowId) and `@Update` returning `Int` (rows affected) are also passed through.
 */
public suspend inline fun <T> notifyingWrite(
    vararg tables: String,
    crossinline block: suspend () -> T,
): T {
    require(tables.isNotEmpty()) {
        "notifyingWrite requires at least one table name — pass the Room @Entity " +
            "tableName(s) your write modifies. Empty tables list means downstream " +
            "daoFlow(...) subscribers would never re-emit after this write."
    }
    val result = block()
    if (tables.size == 1) {
        RoomChangeBus.notify(tables[0])
    } else {
        RoomChangeBus.notify(tables.toSet())
    }
    return result
}
