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

import kpt.core.base.platform.notification.NotificationScheduler

/**
 * Kotlin/JS stub for [BillReminderScheduler].
 *
 * The browser `Notification` API only fires while the tab is open and permission has been
 * granted; out-of-band scheduling requires a Service Worker, which the template doesn't
 * yet wire up. This stub no-ops so common code compiles cleanly — bills still persist
 * and the in-app banner fires on next open.
 */
actual class BillReminderScheduler : NotificationScheduler<BillReminderSchedule> {
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    actual override suspend fun schedule(bill: BillReminderSchedule) {
        // Intentional no-op until Service Worker integration ships.
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    actual override suspend fun cancel(billId: String) {
        // No persisted state on web, so nothing to cancel.
    }

    actual override suspend fun cancelAll() {
        // No persisted state on web, so nothing to cancel.
    }
}
