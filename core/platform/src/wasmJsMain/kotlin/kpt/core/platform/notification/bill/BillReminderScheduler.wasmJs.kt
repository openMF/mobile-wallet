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
 * WasmJS stub for [BillReminderScheduler]. Same rationale as the JS sibling — browser
 * Notifications need a Service Worker for background delivery; deferred to a follow-up.
 */
actual class BillReminderScheduler : NotificationScheduler<BillReminderSchedule> {
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    actual override suspend fun schedule(bill: BillReminderSchedule) {
        // Intentional no-op.
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    actual override suspend fun cancel(billId: String) {
        // No persisted state, so nothing to cancel.
    }

    actual override suspend fun cancelAll() {
        // No persisted state, so nothing to cancel.
    }
}
