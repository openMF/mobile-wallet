/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.platform.notification

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Generic notification scheduling contract. Domain-specific schedulers
 * (e.g. BillReminderScheduler in `core/platform/notification/bill/`) wrap
 * this with their typed payload.
 */
interface NotificationScheduler<T : NotificationRequest> {
    suspend fun schedule(request: T)
    suspend fun cancel(id: String)
    suspend fun cancelAll()
}

/** Minimal contract every notification request must satisfy. */
@OptIn(ExperimentalTime::class)
interface NotificationRequest {
    val id: String
    val title: String
    val body: String
    val scheduledAt: Instant
}
