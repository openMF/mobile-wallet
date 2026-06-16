/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.widget

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.mifospay.core.data.repository.WidgetManagerRepository
import java.util.concurrent.TimeUnit

class WidgetRefreshWorker(
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams), KoinComponent {

    // Injects from :cmp-shared — not from :core:domain
    private val widgetManager: WidgetManagerRepository by inject()

    override suspend fun doWork(): Result {
        return try {
            widgetManager.refresh()

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
    companion object {
        private const val WORK_NAME = "mifospay_widget_refresh_periodic"
        fun schedule(context: Context) {
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                PeriodicWorkRequestBuilder<WidgetRefreshWorker>(15, TimeUnit.MINUTES)
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build(),
                    )
                    .setBackoffCriteria(
                        BackoffPolicy.EXPONENTIAL,
                        WorkRequest.MIN_BACKOFF_MILLIS,
                        TimeUnit.MILLISECONDS,
                    )
                    .build(),
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
