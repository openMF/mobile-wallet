/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.platform.notification.bill.internal

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kpt.core.platform.notification.bill.BillReminderScheduler

/**
 * Worker that fires when `WorkManager` reaches the delay scheduled by [BillReminderScheduler].
 *
 * Responsibilities:
 *  - Ensure the notification channel exists (Android 8+).
 *  - Build and post a [NotificationCompat] notification with the title/body from input data.
 *  - Honour runtime permission denial: on Android 13+, [NotificationManagerCompat.notify]
 *    becomes a silent no-op when `POST_NOTIFICATIONS` is denied — the worker still reports
 *    [Result.success] so WorkManager doesn't retry.
 *
 * The notification id is derived from the `billId` hash so editing the same bill replaces
 * the prior notification (consistent with the unique-work-name idempotency on the scheduler
 * side).
 */
internal class BillReminderWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val billId = inputData.getString(BillReminderScheduler.KEY_BILL_ID) ?: return Result.success()
        val title = inputData.getString(BillReminderScheduler.KEY_TITLE).orEmpty()
        val body = inputData.getString(BillReminderScheduler.KEY_BODY).orEmpty()

        ensureChannel(applicationContext)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // NotificationManagerCompat#notify is a no-op when POST_NOTIFICATIONS is denied
        // on API 33+, so we don't need an explicit permission check here.
        try {
            NotificationManagerCompat.from(applicationContext).notify(billId.hashCode(), notification)
        } catch (security: SecurityException) {
            // Belt-and-braces: some OEM builds throw despite the documented no-op.
            // Permission denial → bill stays saved, in-app banner fires on next open.
        }
        return Result.success()
    }

    private fun ensureChannel(ctx: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (manager.getNotificationChannel(CHANNEL_ID) != null) return
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                "Bill reminders",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "Reminds you before recurring bills are due"
            },
        )
    }

    private companion object {
        const val CHANNEL_ID = "bill_reminders"
    }
}
