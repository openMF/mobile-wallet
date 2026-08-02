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

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kpt.core.base.platform.notification.NotificationScheduler
import kpt.core.platform.notification.bill.internal.BillReminderWorker
import java.util.concurrent.TimeUnit

/**
 * Android implementation backed by `WorkManager` — survives process death and reboots
 * (when the `OneTimeWorkRequest` is enqueued with a unique name we can replace on edit).
 *
 * The actual OS notification is dispatched by [BillReminderWorker] from its `doWork()`,
 * which is invoked by WorkManager after the requested delay. This indirection lets us
 * keep the **scheduler** API itself free of NotificationManager / channel plumbing —
 * those live next to the worker and can evolve independently.
 *
 * **Idempotency**: `enqueueUniqueWork` with [ExistingWorkPolicy.REPLACE] means re-scheduling
 * the same `billId` (e.g. after the user edits the bill) cleanly replaces the prior
 * request — no duplicate notifications.
 *
 * **Past instants**: WorkManager allows zero/negative `setInitialDelay` (fires immediately).
 * We guard against that here so re-opening the app after a missed reminder doesn't
 * deliver a stale notification; callers are expected to compute the *next* occurrence
 * after the missed one.
 */
actual class BillReminderScheduler(
    private val applicationContext: Context,
) : NotificationScheduler<BillReminderSchedule> {
    private val workManager: WorkManager
        get() = WorkManager.getInstance(applicationContext)

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    actual override suspend fun schedule(bill: BillReminderSchedule) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val delayMs = bill.triggerAtMs - now
        if (delayMs <= 0L) {
            // Past instant — silently drop. Callers should re-compute the next occurrence.
            return@withContext
        }
        val input = Data.Builder()
            .putString(KEY_BILL_ID, bill.billId)
            .putString(KEY_TITLE, bill.title)
            .putString(KEY_BODY, bill.body)
            .build()
        val request = OneTimeWorkRequestBuilder<BillReminderWorker>()
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .setInputData(input)
            .addTag(TAG_BILL_REMINDER)
            .build()
        workManager.enqueueUniqueWork(
            uniqueWorkName(bill.billId),
            ExistingWorkPolicy.REPLACE,
            request,
        )
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    actual override suspend fun cancel(billId: String) = withContext(Dispatchers.IO) {
        workManager.cancelUniqueWork(uniqueWorkName(billId))
        Unit
    }

    actual override suspend fun cancelAll() = withContext(Dispatchers.IO) {
        workManager.cancelAllWorkByTag(TAG_BILL_REMINDER)
        Unit
    }

    companion object {
        /** Stable WorkManager tag — lets us cancel every bill-reminder request in one call. */
        const val TAG_BILL_REMINDER: String = "bill_reminder"

        /** Input-data key for [BillReminderSchedule.billId]. Read by [BillReminderWorker]. */
        const val KEY_BILL_ID: String = "bill_id"

        /** Input-data key for [BillReminderSchedule.title]. */
        const val KEY_TITLE: String = "title"

        /** Input-data key for [BillReminderSchedule.body]. */
        const val KEY_BODY: String = "body"

        /** Per-bill unique work name — re-scheduling the same id replaces the prior request. */
        fun uniqueWorkName(billId: String): String = "bill_reminder_$billId"
    }
}
