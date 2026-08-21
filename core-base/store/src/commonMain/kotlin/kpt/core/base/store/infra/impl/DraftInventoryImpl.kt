/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.store.infra.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kpt.core.base.database.infra.dao.DraftDao
import kpt.core.base.database.infra.entity.DraftEntity
import kpt.core.base.database.invalidation.daoFlow
import kpt.core.base.database.invalidation.notifyingWrite
import kpt.core.base.store.infra.DraftInventory
import kpt.core.base.store.infra.DraftRecord
import kpt.core.base.store.infra.StoreCacheManager
import kpt.core.base.store.submit.SubmitOutboxStatus

/**
 * Room-backed [DraftInventory]. A thin, framework-owned read/action facade over [DraftDao] that
 * exposes the cross-form draft feed the Sync & Drafts screen renders.
 *
 * Reuses the same wasmJs invalidation bridge every other draft consumer uses ([daoFlow] /
 * [notifyingWrite]) so the live list re-emits after a discard/retry on every platform.
 */
class DraftInventoryImpl(
    private val draftDao: DraftDao,
) : DraftInventory {

    override fun observeAll(): Flow<List<DraftRecord>> =
        daoFlow(DRAFTS_TABLE) { draftDao.observeAll() }.map { rows -> rows.map { it.toRecord() } }

    override suspend fun discard(id: Long) = notifyingWrite(DRAFTS_TABLE) {
        draftDao.deleteById(id)
    }

    override suspend fun retry(id: Long) = notifyingWrite(DRAFTS_TABLE) {
        draftDao.requeue(id, currentTimeMillis())
    }

    override suspend fun pruneExpired() = notifyingWrite(DRAFTS_TABLE) {
        val thresholdMs = currentTimeMillis() - StoreCacheManager.DEFAULT_DRAFT_TTL_MS
        draftDao.deleteOlderThan(thresholdMs)
    }

    private fun DraftEntity.toRecord(): DraftRecord = DraftRecord(
        id = id,
        formKey = formKey,
        uniqueKey = uniqueKey,
        status = runCatching { SubmitOutboxStatus.valueOf(status) }.getOrDefault(SubmitOutboxStatus.PENDING),
        createdAtMs = createdAtMs,
        updatedAtMs = updatedAtMs,
        errorMessage = errorMessage,
    )
}

private fun currentTimeMillis(): Long = kotlin.time.Clock.System.now().toEpochMilliseconds()

/** Room `@Entity(tableName = …)` for [DraftEntity] — drives the wasmJs invalidation bridge. */
private const val DRAFTS_TABLE = "framework_submit_drafts"
