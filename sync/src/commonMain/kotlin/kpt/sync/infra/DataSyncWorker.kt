/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
@file:OptIn(io.github.mobilebytelabs.worker.ExperimentalWorkerApi::class)

package kpt.sync.infra

import io.github.mobilebytelabs.worker.CoroutineWorker
import io.github.mobilebytelabs.worker.WorkResult
import io.github.mobilebytelabs.worker.WorkerContext
import kpt.core.base.data.infra.Synchronizer
import kpt.core.base.datastore.infra.ChangeListVersions
import kpt.core.base.datastore.infra.SyncStatePersister

/**
 * Single data-sync worker. Implements [Synchronizer] so [Syncable] collaborators can read +
 * write [ChangeListVersions] through `this` without an extra abstraction.
 *
 * This fork has no periodic-refresh [Syncable] collaborators of its own (per-screen Store5
 * FetchPolicy handles network freshness instead — see `core/store/README.md`); the template's
 * demo currency/economic-indicator syncers were stripped with the rest of the demo scaffolding.
 * Add a real [Syncable] here (constructor-injected, matching [SyncModule]'s binding) if a
 * future feature needs generic background data sync.
 */
public class DataSyncWorker(
    context: WorkerContext,
    private val persister: SyncStatePersister,
) : CoroutineWorker(context), Synchronizer {

    // Synchronizer's in-memory state for THIS worker invocation. Read at start
    // from the persister; written back at end.
    private var workingVersions: ChangeListVersions = ChangeListVersions()

    override suspend fun getChangeListVersions(): ChangeListVersions = workingVersions

    override suspend fun updateChangeListVersions(update: ChangeListVersions.() -> ChangeListVersions) {
        workingVersions = workingVersions.update()
    }

    override suspend fun doWork(): WorkResult {
        workingVersions = persister.read()
        persister.write(workingVersions)
        return WorkResult.success()
    }
}
