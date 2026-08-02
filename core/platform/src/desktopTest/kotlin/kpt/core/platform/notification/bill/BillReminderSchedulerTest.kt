/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.platform.notification.bill

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Locks the desktop-stub semantics for [BillReminderScheduler]:
 *  - all three suspend operations complete without throwing,
 *  - none observe / mutate any external state.
 *
 * The contract these tests pin down is the **interface promise**: common-code callers
 * (ViewModels, Koin modules) can call `schedule/cancel/cancelAll` from the desktop target
 * without crashing, even though the stub doesn't actually deliver a notification. When a
 * real desktop notification surface (e.g. `awt.TrayIcon`) is wired in the future, these
 * tests should grow assertions on the resulting state.
 */
class BillReminderSchedulerTest {

    private val scheduler = BillReminderScheduler()

    @Test
    fun scheduleCompletesForFutureInstant() = runTest {
        scheduler.schedule(
            BillReminderSchedule(
                billId = "B1",
                title = "Electricity",
                body = "Due in 1 day",
                triggerAtMs = Long.MAX_VALUE,
            ),
        )
        // Reaching here without throwing is the contract — desktop is a no-op stub.
    }

    @Test
    fun schedulePastInstantStillNoops() = runTest {
        scheduler.schedule(
            BillReminderSchedule(
                billId = "B1",
                title = "Stale",
                body = "Already missed",
                triggerAtMs = 0L,
            ),
        )
    }

    @Test
    fun cancelUnknownBillIsSafe() = runTest {
        scheduler.cancel("missing")
    }

    @Test
    fun cancelAllIsIdempotent() = runTest {
        scheduler.cancelAll()
        scheduler.cancelAll()
    }

    @Test
    fun billReminderScheduleEqualityIsValueBased() {
        val a = BillReminderSchedule("id", "T", "B", 1_700_000_000_000L)
        val b = BillReminderSchedule("id", "T", "B", 1_700_000_000_000L)
        assertEquals(a, b)
    }
}
