/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
@file:OptIn(ExperimentalUuidApi::class)

package kpt.sync.infra

import io.github.mobilebytelabs.worker.ExistingPeriodicWorkPolicy
import io.github.mobilebytelabs.worker.ExistingWorkPolicy
import io.github.mobilebytelabs.worker.OneTimeWorkRequest
import io.github.mobilebytelabs.worker.PeriodicWorkRequest
import io.github.mobilebytelabs.worker.WorkInfo
import io.github.mobilebytelabs.worker.WorkManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kpt.sync.DATA_SYNC_WORK_NAME
import kpt.sync.NotificationContent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * `DefaultWorkScheduler` is a thin façade over worker-kmp's `WorkManager`. These tests
 * pin the delegation + `WorkHandle` contract that `LoanReminderUseCase` (the library-design
 * regression test) relies on.
 */
class DefaultWorkSchedulerTest {

    /** Records the calls the scheduler makes; returns a fixed id for enqueues. */
    private class FakeWorkManager : WorkManager {
        val enqueuedUniqueNames = mutableListOf<String>()
        val enqueuedOneTime = mutableListOf<OneTimeWorkRequest>()
        val cancelledIds = mutableListOf<Uuid>()
        val fixedId: Uuid = Uuid.random()

        override suspend fun enqueue(request: OneTimeWorkRequest): Uuid {
            enqueuedOneTime += request
            return fixedId
        }

        override suspend fun enqueueUniquePeriodicWork(
            uniqueWorkName: String,
            existingPeriodicWorkPolicy: ExistingPeriodicWorkPolicy,
            request: PeriodicWorkRequest,
        ): Uuid = fixedId

        override suspend fun enqueueUniqueWork(
            uniqueWorkName: String,
            existingWorkPolicy: ExistingWorkPolicy,
            request: OneTimeWorkRequest,
        ): Uuid {
            enqueuedUniqueNames += uniqueWorkName
            return fixedId
        }

        override suspend fun cancelWorkById(id: Uuid) {
            cancelledIds += id
        }

        override suspend fun cancelAllWorkByTag(tag: String) = Unit
        override fun getWorkInfosByTag(tag: String): Flow<List<WorkInfo>> = flowOf(emptyList())
        override fun getWorkInfosForUniqueWorkFlow(uniqueWorkName: String): Flow<List<WorkInfo>> = flowOf(emptyList())
        override suspend fun getWorkInfoById(id: Uuid): WorkInfo? = null
    }

    @Test
    fun enqueueDataSync_enqueues_unique_work_and_returns_a_named_handle() = runTest {
        val wm = FakeWorkManager()

        val handle = DefaultWorkScheduler(wm).enqueueDataSync()

        assertEquals(DATA_SYNC_WORK_NAME, handle.uniqueName, "data sync must be a uniquely-named work")
        assertEquals(wm.fixedId, handle.id)
        assertEquals(listOf(DATA_SYNC_WORK_NAME), wm.enqueuedUniqueNames)
    }

    @Test
    fun scheduleNotification_enqueues_a_one_time_request_with_no_unique_name() = runTest {
        val wm = FakeWorkManager()

        val handle = DefaultWorkScheduler(wm).scheduleNotification(
            NotificationContent(title = "Loan due", body = "EMI due tomorrow"),
        )

        assertEquals(null, handle.uniqueName, "one-off notifications are not unique work")
        assertEquals(1, wm.enqueuedOneTime.size, "exactly one one-time request enqueued")
    }

    @Test
    fun cancelWork_cancels_by_the_handle_id() = runTest {
        val wm = FakeWorkManager()
        val scheduler = DefaultWorkScheduler(wm)

        val handle = scheduler.enqueueDataSync()
        scheduler.cancelWork(handle)

        assertEquals(listOf(handle.id), wm.cancelledIds)
    }
}
