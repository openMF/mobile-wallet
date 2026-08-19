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
 * Desktop (JVM) stub for [BillReminderScheduler].
 *
 * Compose Multiplatform on the desktop does not yet expose a portable system-notification
 * API across Windows/macOS/Linux, so this implementation deliberately no-ops. Bills are
 * still persisted by the caller, and the in-app "Upcoming Bills" surface acts as the
 * fallback reminder when the user opens the app.
 *
 * Forks may swap this for an `awt.SystemTray` / `java.awt.TrayIcon` implementation, or
 * a Windows toast / macOS user-notification bridge, without touching common code.
 */
actual class BillReminderScheduler : NotificationScheduler<BillReminderSchedule> {
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    actual override suspend fun schedule(bill: BillReminderSchedule) {
        // Intentional no-op — see KDoc above.
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    actual override suspend fun cancel(billId: String) {
        // No persisted state on desktop, so nothing to cancel.
    }

    actual override suspend fun cancelAll() {
        // No persisted state on desktop, so nothing to cancel.
    }
}
