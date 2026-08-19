/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.database.infra.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow
import kpt.core.database.infra.entity.DraftEntity

/**
 * DAO for the framework-owned `framework_submit_drafts` table.
 *
 * Consumed by [kpt.core.data.store.RoomSubmitOutbox]. The framework uses this table to
 * persist form payloads that failed to reach the server so users can resume or retry later.
 */
@Dao
interface DraftDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: DraftEntity): Long

    @Query("SELECT * FROM framework_submit_drafts WHERE id = :id")
    suspend fun getById(id: Long): DraftEntity?

    @Query(
        "SELECT * FROM framework_submit_drafts " +
            "WHERE formKey = :formKey AND uniqueKey IS NULL AND status = 'PENDING' LIMIT 1",
    )
    suspend fun getPendingByFormKey(formKey: String): DraftEntity?

    @Query(
        "SELECT * FROM framework_submit_drafts " +
            "WHERE formKey = :formKey AND uniqueKey IS NULL AND status = 'PENDING' LIMIT 1",
    )
    fun observePendingByFormKey(formKey: String): Flow<DraftEntity?>

    /**
     * Multi-pending companion to [getPendingByFormKey]. Reads the PENDING draft for a specific
     * `(formKey, uniqueKey)` pair — N concurrent independent drafts can coexist under one
     * `formKey` when each carries a distinct `uniqueKey` (e.g. `loan_id`, `bill_id`, wizard step).
     */
    @Query(
        "SELECT * FROM framework_submit_drafts " +
            "WHERE formKey = :formKey AND uniqueKey = :uniqueKey AND status = 'PENDING' LIMIT 1",
    )
    suspend fun getPendingByUniqueKey(formKey: String, uniqueKey: String): DraftEntity?

    /** Streaming variant of [getPendingByUniqueKey]. */
    @Query(
        "SELECT * FROM framework_submit_drafts " +
            "WHERE formKey = :formKey AND uniqueKey = :uniqueKey AND status = 'PENDING' LIMIT 1",
    )
    fun observePendingByUniqueKey(formKey: String, uniqueKey: String): Flow<DraftEntity?>

    /**
     * Observes ALL non-terminal drafts (PENDING / RETRYING / FAILED) for a `formKey`, ordered
     * newest-first by `createdAtMs`. Includes singleton drafts (uniqueKey IS NULL) and all
     * multi-pending rows. Used by feature UIs that need to display "N drafts pending sync".
     */
    @Query(
        "SELECT * FROM framework_submit_drafts " +
            "WHERE formKey = :formKey AND status IN ('PENDING','RETRYING','FAILED') " +
            "ORDER BY createdAtMs DESC",
    )
    fun observeAllByFormKey(formKey: String): Flow<List<DraftEntity>>

    @Query("SELECT * FROM framework_submit_drafts WHERE status = 'PENDING'")
    suspend fun getAllPending(): List<DraftEntity>

    @Query("UPDATE framework_submit_drafts SET status = 'RETRYING', updatedAtMs = :nowMs WHERE id = :id")
    suspend fun markRetrying(id: Long, nowMs: Long)

    @Query("UPDATE framework_submit_drafts SET status = 'SUBMITTED', updatedAtMs = :nowMs WHERE id = :id")
    suspend fun markSubmitted(id: Long, nowMs: Long)

    @Query(
        "UPDATE framework_submit_drafts SET status = 'FAILED', " +
            "updatedAtMs = :nowMs, errorMessage = :error WHERE id = :id",
    )
    suspend fun markFailed(id: Long, nowMs: Long, error: String?)

    @Query("UPDATE framework_submit_drafts SET payloadJson = :payloadJson, updatedAtMs = :nowMs WHERE id = :id")
    suspend fun updatePayload(id: Long, payloadJson: String, nowMs: Long)

    @Query("DELETE FROM framework_submit_drafts WHERE formKey = :formKey")
    suspend fun deleteByFormKey(formKey: String)

    /**
     * Deletes a single `(formKey, uniqueKey)` row. Use after a multi-pending draft
     * successfully submits (paired with [getPendingByUniqueKey]). Does NOT affect the
     * singleton draft (uniqueKey IS NULL) under the same `formKey`.
     */
    @Query("DELETE FROM framework_submit_drafts WHERE formKey = :formKey AND uniqueKey = :uniqueKey")
    suspend fun deleteByUniqueKey(formKey: String, uniqueKey: String)

    @Query("DELETE FROM framework_submit_drafts")
    suspend fun deleteAll()

    /**
     * Deletes SUBMITTED and FAILED rows older than [thresholdMs] (epoch millis).
     * PENDING drafts are never pruned here — the user may still want to resume them.
     * Call on app start via [kpt.core.store.infra.StoreCacheManager.pruneExpiredDrafts].
     */
    @Query(
        "DELETE FROM framework_submit_drafts " +
            "WHERE createdAtMs < :thresholdMs AND status IN ('SUBMITTED', 'FAILED')",
    )
    suspend fun deleteOlderThan(thresholdMs: Long)
}
