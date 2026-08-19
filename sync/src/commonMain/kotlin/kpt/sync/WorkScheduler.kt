/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.sync

import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

/**
 * Koin-injectable façade over worker-kmp's `WorkManager` — the **single API
 * surface** every consumer module (feature/loans, feature/bills, …) uses to
 * schedule data sync + notifications.
 *
 * Consumers depend on the `sync` module to get this interface but NEVER
 * import `io.github.mobilebytelabs.worker.*` directly. That decoupling is the
 * point: when worker-kmp's `WorkManager` API evolves, only `DefaultWorkScheduler`
 * (the impl) changes — call sites stay stable.
 *
 * Example (cross-module use from `feature/loans`):
 * ```kotlin
 * class LoanReminderUseCase(private val scheduler: WorkScheduler) {
 *     fun scheduleDueDateReminder(loan: Loan) {
 *         scheduler.scheduleNotification(
 *             NotificationContent("Loan payment due", "Loan #${loan.id}"),
 *             delay = (loan.dueAt - now()) - 1.days,
 *             mode = WorkMode.Foreground,
 *         )
 *     }
 *     fun refreshLoans() = scheduler.enqueueDataSync(mode = WorkMode.Background)
 * }
 * ```
 */
public interface WorkScheduler {
    /**
     * Enqueue a one-shot data sync (fans out the in-process
     * `List<Syncable>` via the `DataSyncWorker`'s explicit awaitAll).
     * Unique-named [DATA_SYNC_WORK_NAME] under KEEP policy so re-invocations
     * don't pile up.
     *
     * @param mode [WorkMode.Foreground] requests Android expedited execution;
     *   silent on background. [WorkMode.Background] runs under normal
     *   constraints (connected network).
     */
    public suspend fun enqueueDataSync(mode: WorkMode = WorkMode.Background): WorkHandle

    /**
     * Schedule a one-shot notification fire-and-forget. The
     * [NotificationWorker] posts the notification via the multiplatform
     * KMPNotifier `localNotifier` (see `SyncNotifier.kt`).
     *
     * @param delay non-negative; pass [Duration.ZERO] for immediate. Backed by
     *   `OneTimeWorkRequest.initialDelay`.
     */
    public suspend fun scheduleNotification(
        content: NotificationContent,
        delay: Duration = Duration.ZERO,
        mode: WorkMode = WorkMode.Background,
    ): WorkHandle

    /**
     * Observe a previously-enqueued work unit. Emits a [WorkStatus] every time
     * the underlying `WorkInfo` transitions.
     */
    public fun observeWork(handle: WorkHandle): Flow<WorkStatus>

    /** Cancel a previously-enqueued work unit by handle. */
    public suspend fun cancelWork(handle: WorkHandle)
}
