/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.store.infra.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kpt.core.base.database.invalidation.daoFlow
import kpt.core.base.database.invalidation.notifyingWrite
import kpt.core.base.store.submit.SubmitOutbox
import kpt.core.base.store.submit.SubmitOutboxEntry
import kpt.core.base.store.submit.SubmitOutboxStatus
import kpt.core.base.database.infra.dao.DraftDao
import kpt.core.base.database.infra.entity.DraftEntity

/**
 * Room-backed [SubmitOutbox] that persists form payloads across process death.
 *
 * Serializes payloads to JSON via [kotlinx.serialization] and stores them in the
 * `framework_submit_drafts` table in [kpt.core.database.AppDatabase].
 *
 * Wire one instance per form type in your DI module:
 * ```kotlin
 * single<SubmitOutbox<LoanApplicationPayload>> {
 *     RoomSubmitOutbox(get<AppDatabase>().draftDao, LoanApplicationPayload.serializer())
 * }
 * ```
 *
 * @param P Payload type. Must be annotated with `@Serializable`.
 * @param dao The Room DAO for the `framework_submit_drafts` table.
 * @param serializer Kotlinx serializer for [P]. Obtain via `P.serializer()`.
 */
class RoomSubmitOutbox<P>(
    private val dao: DraftDao,
    private val serializer: KSerializer<P>,
) : SubmitOutbox<P> {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun save(formKey: String, payload: P): Long = notifyingWrite(DRAFTS_TABLE) {
        val nowMs = currentTimeMillis()
        val existing = dao.getPendingByFormKey(formKey)
        if (existing != null) {
            dao.updatePayload(existing.id, json.encodeToString(serializer, payload), nowMs)
            existing.id
        } else {
            val entity = DraftEntity(
                formKey = formKey,
                uniqueKey = null,
                payloadJson = json.encodeToString(serializer, payload),
                status = SubmitOutboxStatus.PENDING.name,
                createdAtMs = nowMs,
                updatedAtMs = nowMs,
            )
            dao.insert(entity)
        }
    }

    override suspend fun saveByUniqueKey(formKey: String, uniqueKey: String, payload: P): Long =
        notifyingWrite(DRAFTS_TABLE) {
            val nowMs = currentTimeMillis()
            val existing = dao.getPendingByUniqueKey(formKey, uniqueKey)
            if (existing != null) {
                dao.updatePayload(existing.id, json.encodeToString(serializer, payload), nowMs)
                existing.id
            } else {
                val entity = DraftEntity(
                    formKey = formKey,
                    uniqueKey = uniqueKey,
                    payloadJson = json.encodeToString(serializer, payload),
                    status = SubmitOutboxStatus.PENDING.name,
                    createdAtMs = nowMs,
                    updatedAtMs = nowMs,
                )
                dao.insert(entity)
            }
        }

    override suspend fun getPending(formKey: String): SubmitOutboxEntry<P>? =
        dao.getPendingByFormKey(formKey)?.toEntry()

    override suspend fun getPendingByUniqueKey(formKey: String, uniqueKey: String): SubmitOutboxEntry<P>? =
        dao.getPendingByUniqueKey(formKey, uniqueKey)?.toEntry()

    override fun observePending(formKey: String): Flow<SubmitOutboxEntry<P>?> =
        daoFlow(DRAFTS_TABLE) { dao.observePendingByFormKey(formKey) }.map { it?.toEntry() }

    override fun observePendingByUniqueKey(formKey: String, uniqueKey: String): Flow<SubmitOutboxEntry<P>?> =
        daoFlow(DRAFTS_TABLE) { dao.observePendingByUniqueKey(formKey, uniqueKey) }.map { it?.toEntry() }

    override fun observeAllByFormKey(formKey: String): Flow<List<SubmitOutboxEntry<P>>> =
        daoFlow(DRAFTS_TABLE) { dao.observeAllByFormKey(formKey) }.map { rows -> rows.mapNotNull { it.toEntry() } }

    override suspend fun getAllPending(): List<SubmitOutboxEntry<P>> = dao.getAllPending().mapNotNull { it.toEntry() }

    override suspend fun markRetrying(id: Long) =
        notifyingWrite(DRAFTS_TABLE) { dao.markRetrying(id, currentTimeMillis()) }

    override suspend fun markSubmitted(id: Long) =
        notifyingWrite(DRAFTS_TABLE) { dao.markSubmitted(id, currentTimeMillis()) }

    override suspend fun markFailed(id: Long, error: String?) =
        notifyingWrite(DRAFTS_TABLE) { dao.markFailed(id, currentTimeMillis(), error) }

    override suspend fun deleteByFormKey(formKey: String) =
        notifyingWrite(DRAFTS_TABLE) { dao.deleteByFormKey(formKey) }

    override suspend fun deleteByUniqueKey(formKey: String, uniqueKey: String) =
        notifyingWrite(DRAFTS_TABLE) { dao.deleteByUniqueKey(formKey, uniqueKey) }

    override suspend fun deleteAll() = notifyingWrite(DRAFTS_TABLE) { dao.deleteAll() }

    private fun DraftEntity.toEntry(): SubmitOutboxEntry<P>? = runCatching {
        SubmitOutboxEntry(
            id = id,
            formKey = formKey,
            payload = json.decodeFromString(serializer, payloadJson),
            status = SubmitOutboxStatus.valueOf(status),
            createdAtMs = createdAtMs,
            uniqueKey = uniqueKey,
            errorMessage = errorMessage,
        )
    }.getOrNull()
}

private fun currentTimeMillis(): Long = kotlin.time.Clock.System.now().toEpochMilliseconds()

/** Room `@Entity(tableName = …)` for [DraftEntity] — drives the wasmJs invalidation bridge. */
private const val DRAFTS_TABLE = "framework_submit_drafts"
