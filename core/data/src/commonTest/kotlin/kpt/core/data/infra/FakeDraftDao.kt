/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.data.infra

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kpt.core.base.database.infra.dao.DraftDao
import kpt.core.base.database.infra.entity.DraftEntity

/**
 * In-memory fake of [DraftDao] whose reactive reads are **cold snapshots** — each
 * subscription emits the current rows exactly once, then completes.
 *
 * Models the wasmJs failure mode the invalidation bridge absorbs: Room 3 alpha05's
 * `InvalidationTracker` does not fan out to a live collector after a write, so the DAO
 * `Flow` never re-emits on its own. Re-emission must come from `daoFlow { }` re-subscribing
 * on the `RoomChangeBus` signal that `notifyingWrite { }` publishes. Deliberately cold (not
 * a hot `MutableStateFlow`) so there is exactly one re-emit source and no Turbine race —
 * see [kpt.core.data.demo.watchlist.FakeWatchlistDao] for the same rationale.
 */
internal class FakeDraftDao : DraftDao {

    private val rows = mutableListOf<DraftEntity>()
    private var nextId = 1L

    private val nonTerminal = setOf("PENDING", "RETRYING", "FAILED")

    override suspend fun insert(entity: DraftEntity): Long {
        val id = nextId++
        rows.add(entity.copy(id = id))
        return id
    }

    override suspend fun getById(id: Long): DraftEntity? = rows.firstOrNull { it.id == id }

    override suspend fun getPendingByFormKey(formKey: String): DraftEntity? =
        rows.firstOrNull { it.formKey == formKey && it.uniqueKey == null && it.status == "PENDING" }

    override fun observePendingByFormKey(formKey: String): Flow<DraftEntity?> =
        flow { emit(rows.firstOrNull { it.formKey == formKey && it.uniqueKey == null && it.status == "PENDING" }) }

    override suspend fun getPendingByUniqueKey(formKey: String, uniqueKey: String): DraftEntity? =
        rows.firstOrNull { it.formKey == formKey && it.uniqueKey == uniqueKey && it.status == "PENDING" }

    override fun observePendingByUniqueKey(formKey: String, uniqueKey: String): Flow<DraftEntity?> =
        flow { emit(rows.firstOrNull { it.formKey == formKey && it.uniqueKey == uniqueKey && it.status == "PENDING" }) }

    override fun observeAllByFormKey(formKey: String): Flow<List<DraftEntity>> =
        flow {
            emit(
                rows.filter { it.formKey == formKey && it.status in nonTerminal }
                    .sortedByDescending { it.createdAtMs },
            )
        }

    override fun observeAll(): Flow<List<DraftEntity>> =
        flow {
            emit(
                rows.filter { it.status in nonTerminal }
                    .sortedByDescending { it.updatedAtMs },
            )
        }

    override suspend fun getAllPending(): List<DraftEntity> = rows.filter { it.status == "PENDING" }

    override suspend fun markRetrying(id: Long, nowMs: Long) =
        updateRow(id) { it.copy(status = "RETRYING", updatedAtMs = nowMs) }

    override suspend fun requeue(id: Long, nowMs: Long) =
        updateRow(id) { it.copy(status = "PENDING", errorMessage = null, updatedAtMs = nowMs) }

    override suspend fun markSubmitted(id: Long, nowMs: Long) =
        updateRow(id) { it.copy(status = "SUBMITTED", updatedAtMs = nowMs) }

    override suspend fun markFailed(id: Long, nowMs: Long, error: String?) =
        updateRow(id) { it.copy(status = "FAILED", updatedAtMs = nowMs, errorMessage = error) }

    override suspend fun updatePayload(id: Long, payloadJson: String, nowMs: Long) =
        updateRow(id) { it.copy(payloadJson = payloadJson, updatedAtMs = nowMs) }

    override suspend fun deleteByFormKey(formKey: String) {
        rows.removeAll { it.formKey == formKey }
    }

    override suspend fun deleteByUniqueKey(formKey: String, uniqueKey: String) {
        rows.removeAll { it.formKey == formKey && it.uniqueKey == uniqueKey }
    }

    override suspend fun deleteById(id: Long) {
        rows.removeAll { it.id == id }
    }

    override suspend fun deleteAll() {
        rows.clear()
    }

    override suspend fun deleteOlderThan(thresholdMs: Long) {
        rows.removeAll { it.createdAtMs < thresholdMs && (it.status == "SUBMITTED" || it.status == "FAILED") }
    }

    private fun updateRow(id: Long, block: (DraftEntity) -> DraftEntity) {
        val idx = rows.indexOfFirst { it.id == id }
        if (idx >= 0) rows[idx] = block(rows[idx])
    }
}
