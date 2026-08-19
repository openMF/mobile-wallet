/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.ui.viewmodel

import kpt.core.base.store.submit.SubmitOutbox

/**
 * How a [BaseMutationViewModel] handles its submit — the single knob that replaces the old
 * `BaseSubmitMutationViewModel` / `BaseDraftMutationViewModel` class split. A feature ViewModel
 * extends ONE base and *passes* the mode; the base behaves accordingly.
 *
 * - [InSession] — fire-and-forget submit. No outbox, no persistence, no auto-retry, no resume.
 *   The failure mode is user-recoverable in-session (network blip → the user retries).
 * - [Draft] — offline-resilient submit. Persists the payload to a [SubmitOutbox] so it survives
 *   process death, auto-retries on reconnect (via `OfflineSubmitSyncer`), and surfaces the
 *   three-case resume flow on the next form open.
 *
 * A `Draft` groups the correlated fields (outbox + keys + auto-save) so illegal states are
 * unrepresentable — you cannot declare a `formKey` without an `outbox`.
 *
 * @param T Domain payload type submitted to the server / repository.
 */
sealed interface MutationMode<out T> {

    /** In-session, non-durable submit — no draft persistence, retry, or resume. */
    data object InSession : MutationMode<Nothing>

    /**
     * Durable, offline-resilient submit backed by [outbox].
     *
     * @param outbox        Per-form durable outbox for payload [T] (inject via DI).
     * @param formKey       Stable identifier grouping this form's drafts (e.g. `"loan"`).
     * @param uniqueKey     Sub-identifier when N concurrent drafts coexist under one [formKey]
     *                      (e.g. `loan_id`); `null` for the singleton-per-form case.
     * @param autoSaveDraft When true the draft persists silently on network failure; when false
     *                      the UI prompts the user to save.
     */
    data class Draft<T>(
        val outbox: SubmitOutbox<T>,
        val formKey: String,
        val uniqueKey: String? = null,
        val autoSaveDraft: Boolean = false,
    ) : MutationMode<T>
}
