package org.mifospay.widget

import android.content.Context
import androidx.work.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.mifospay.core.data.repository.WidgetManagerRepository
import java.util.concurrent.TimeUnit

/**
 * WorkManager background refresh worker.
 *
 * Imports from:
 *   • androidx.work.*          (WorkManager)
 *   • org.koin.*               (DI)
 *   • org.mifospay.shared.*    (:cmp-shared — WidgetSyncCoordinator)
 *
 * Does NOT import from :core:domain.
 */
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
        private const val WORK_NAME   = "mifospay_widget_refresh_periodic"
        private const val MAX_RETRIES = 3

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
