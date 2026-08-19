/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.store.infra

import kotlinx.coroutines.flow.Flow
import kpt.core.base.store.submit.SubmitOutboxStatus

/**
 * Framework-shared, **cross-form** view over every draft the app is holding.
 *
 * [SubmitOutbox] is generic in a single payload type `P` (one instance per form). The
 * template-level **Sync & Drafts** screen (Settings) instead needs an *untyped* live feed of
 * EVERY non-terminal draft across ALL forms — a user-facing preview of what is pending sync,
 * retrying, or failed — plus the three cross-form actions that don't need the payload type:
 * discard a row, re-queue a failed row for sync, and the manual prune.
 *
 * This is the seam feature modules consume (feature UIs never touch [DraftDao] directly). It is
 * framework-owned (`core-base/store`) so it upgrades cleanly across template versions; forks get
 * the Sync & Drafts surface for free and never re-implement it.
 *
 * Backed by [kpt.core.base.database.infra.dao.DraftDao]; wired as a Koin `single` in the app's
 * store module (next to [StoreCacheManager]).
 */
interface DraftInventory {

    /**
     * Live cross-form feed of every non-terminal draft (PENDING / RETRYING / FAILED),
     * newest-first by last-update. Terminal SUBMITTED rows are excluded — they are not
     * actionable and are pruned by [StoreCacheManager.pruneExpiredDrafts].
     */
    fun observeAll(): Flow<List<DraftRecord>>

    /**
     * Permanently discards a single draft by [id] (the per-row Discard). Irreversible —
     * the caller confirms intent in the UI.
     */
    suspend fun discard(id: Long)

    /**
     * Re-queues a draft by [id] for sync: transitions it back to PENDING and clears its error,
     * so the per-form [kpt.core.base.store.submit.OfflineSubmitSyncer] re-attempts it on the next
     * online transition. Typically used on a FAILED row (the per-row Retry).
     */
    suspend fun retry(id: Long)

    /**
     * Manual counterpart to the app-start [StoreCacheManager.pruneExpiredDrafts] — deletes
     * SUBMITTED/FAILED rows older than [StoreCacheManager.DEFAULT_DRAFT_TTL_MS]. PENDING drafts
     * are never pruned. Surfaced as the Sync & Drafts screen's "Prune expired" action so the
     * user can reclaim space on demand without waiting for the next cold start.
     */
    suspend fun pruneExpired()
}

/**
 * One draft row as seen by the cross-form [DraftInventory] — untyped (no deserialized payload),
 * because the Sync & Drafts screen renders every form's drafts side-by-side and cannot know each
 * `P`. For a typed, per-form view use [kpt.core.base.store.submit.SubmitOutbox.observeAllByFormKey].
 *
 * @param id          Surrogate row id — the handle for [DraftInventory.discard] / [DraftInventory.retry].
 * @param formKey     The form/screen this draft belongs to (e.g. `"bill_reminder"`).
 * @param uniqueKey   Sub-identifier within [formKey]; `null` = the singleton draft for that form.
 * @param status      Current lifecycle state (PENDING / RETRYING / FAILED).
 * @param createdAtMs Epoch millis when first saved.
 * @param updatedAtMs Epoch millis of the most recent status transition.
 * @param errorMessage Last failure reason for a FAILED row (nullable).
 */
data class DraftRecord(
    val id: Long,
    val formKey: String,
    val uniqueKey: String?,
    val status: SubmitOutboxStatus,
    val createdAtMs: Long,
    val updatedAtMs: Long,
    val errorMessage: String?,
)
