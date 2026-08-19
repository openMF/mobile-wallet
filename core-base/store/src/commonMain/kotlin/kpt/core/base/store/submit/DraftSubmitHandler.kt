/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.store.submit

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kpt.core.base.store.error.ErrorCategory
import kpt.core.base.store.error.categorize

/**
 * Out-of-box form submission handler with user-prompted draft save and restore/discard support.
 *
 * **Two save modes — controlled by [autoSaveDraft]:**
 *
 * `autoSaveDraft = false` (default) — **user-prompted save:**
 * 1. Network failure → state = Failed(Network, draftSaved=false)
 * 2. [DraftSavePrompt] appears OOB: "No connection. Save as draft?"
 * 3a. User taps Save Draft → ViewModel calls [saveDraft] → outbox.save, draftSaved=true
 * 3b. User taps Dismiss → nothing saved, state stays Failed
 *
 * `autoSaveDraft = true` — **silent auto-save:**
 * 1. Network failure → outbox.save immediately → state = Failed(Network, draftSaved=true)
 * 2. No prompt shown; [DraftResumeBanner] surfaces the draft on next form open
 *
 * **Form open lifecycle (both modes):**
 * 1. Form opens → ViewModel observes `outbox.resumeStateFor(formKey)` (a [DraftResumeState] flow)
 * 2. HasDraft → wire [DraftResumeBanner] to show OOB with Restore / Discard
 * 3a. Restore → pre-populate form fields with draft payload
 * 3b. Discard → `outbox.deleteByFormKey(formKey)`
 *
 * **ViewModel wiring (complete example):**
 * ```kotlin
 * // User-prompted (default):
 * private val draftHandler = viewModelScope.draftSubmitHandler<LoanPayload, Unit>(
 *     outbox  = roomSubmitOutbox,
 *     formKey = "loan_application",
 * )
 * // Auto-save:
 * private val draftHandler = viewModelScope.draftSubmitHandler<LoanPayload, Unit>(
 *     outbox         = roomSubmitOutbox,
 *     formKey        = "loan_application",
 *     autoSaveDraft  = true,
 * )
 * val submitState = draftHandler.state
 *     .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SubmitState.Idle)
 *
 * fun onSubmit(payload: LoanPayload) = draftHandler.submit(payload) { repository.submitLoan(it) }
 * fun onRetry()              = draftHandler.retry()
 * fun onSaveDraft()          = draftHandler.saveDraft()          // user-prompted mode only
 * fun onDismissDraftPrompt() = draftHandler.discardDraft()       // user-prompted mode only
 * ```
 *
 * **Multi-pending drafts (optional [uniqueKey]):**
 * When N concurrent independent drafts must coexist under one [formKey] (e.g. Portfolio
 * Tracker per-holding, Bill Reminders per-bill, wizard per-step), pass a distinct
 * [uniqueKey] per handler instance. The handler then routes through
 * [SubmitOutbox.saveByUniqueKey] / [SubmitOutbox.deleteByUniqueKey] so drafts don't
 * overwrite each other. `uniqueKey = null` (default) preserves the legacy singleton
 * semantics — one PENDING draft per [formKey].
 *
 * @param P             Serializable payload type (the data collected by the form).
 * @param R             Result type returned by the server on success.
 * @param autoSaveDraft When true, saves to outbox silently on network failure.
 *   When false (default), the UI must show [DraftSavePrompt] and call [saveDraft].
 * @param uniqueKey     Optional sub-identifier within [formKey]. `null` = singleton mode
 *   (existing behavior). Non-null = multi-pending mode — one draft per `(formKey, uniqueKey)`.
 */
class DraftSubmitHandler<P, R>(
    private val scope: CoroutineScope,
    private val outbox: SubmitOutbox<P>,
    private val formKey: String,
    private val autoSaveDraft: Boolean = false,
    private val uniqueKey: String? = null,
) {
    private val _state = MutableStateFlow<SubmitState<R>>(SubmitState.Idle)

    /** Observable state — expose via `.stateIn()` in the ViewModel. */
    val state: StateFlow<SubmitState<R>> = _state.asStateFlow()

    private var lastPayload: P? = null
    private var lastBlock: (suspend (P) -> R)? = null
    private var lastDraftId: Long? = null
    private var activeJob: Job? = null

    /**
     * Execute [block] with [payload] as a one-shot submission.
     *
     * - No-op if already [SubmitState.Submitting].
     * - On network failure: transitions to [SubmitState.Failed] with `category=Network`.
     *   Does **not** auto-save — the UI shows [DraftSavePrompt] and the user calls [saveDraft].
     * - On success: marks the draft SUBMITTED (if one exists) and transitions to [SubmitState.Submitted].
     */
    fun submit(payload: P, block: suspend (P) -> R) {
        if (_state.value is SubmitState.Submitting) return
        lastPayload = payload
        lastBlock = block
        execute(payload, block)
    }

    /**
     * Re-run the last submission block with the last payload.
     * No-op if no prior submission or currently [SubmitState.Submitting].
     */
    fun retry() {
        val payload = lastPayload
        val block = lastBlock
        if (payload != null && block != null && _state.value !is SubmitState.Submitting) {
            execute(payload, block)
        }
    }

    /**
     * Explicitly saves the last payload as a PENDING draft in the outbox.
     *
     * Call this from the ViewModel after the user confirms "Save Draft" on [DraftSavePrompt].
     * Updates state to [SubmitState.Failed] with `draftSaved=true` so the prompt hides itself.
     * No-op if there is no pending payload (i.e. no prior network failure this session).
     */
    fun saveDraft() {
        val payload = lastPayload ?: return
        scope.launch {
            val draftId = persistDraft(payload)
            lastDraftId = draftId
            val current = _state.value
            if (current is SubmitState.Failed) {
                _state.value = current.copy(draftSaved = true)
            }
        }
    }

    /**
     * Routes the upsert through the right outbox API: [SubmitOutbox.saveByUniqueKey] in
     * multi-pending mode, [SubmitOutbox.save] in singleton mode. Defined once so the two
     * call sites (user-prompted [saveDraft] + silent auto-save inside [execute]) stay aligned.
     */
    private suspend fun persistDraft(payload: P): Long =
        if (uniqueKey != null) {
            outbox.saveByUniqueKey(formKey, uniqueKey, payload)
        } else {
            outbox.save(formKey, payload)
        }

    /**
     * Discards the in-memory payload and returns to [SubmitState.Idle] without saving to outbox.
     *
     * Call this from the ViewModel after the user taps "Dismiss" on [DraftSavePrompt].
     */
    fun discardDraft() {
        lastPayload = null
        lastBlock = null
        lastDraftId = null
        _state.value = SubmitState.Idle
    }

    /**
     * Return to [SubmitState.Idle] without clearing the outbox draft.
     *
     * **Cancellation behaviour:** if cancelled while [SubmitState.Submitting] and the network
     * call has not yet failed, no draft is saved. If a draft was previously saved it remains
     * PENDING in the outbox so [DraftResumeState] surfaces it on the next screen visit.
     */
    fun reset() {
        activeJob?.cancel()
        _state.value = SubmitState.Idle
    }

    private fun execute(payload: P, block: suspend (P) -> R) {
        activeJob?.cancel()
        _state.value = SubmitState.Submitting()
        activeJob = scope.launch {
            _state.value = try {
                val result = block(payload)
                lastDraftId?.let { outbox.markSubmitted(it) }
                lastDraftId = null
                SubmitState.Submitted(result)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val category = categorize(e)
                if (autoSaveDraft && category == ErrorCategory.Network) {
                    val draftId = persistDraft(payload)
                    lastDraftId = draftId
                    SubmitState.Failed(error = e, category = category, draftSaved = true)
                } else {
                    // User-prompted mode: UI shows DraftSavePrompt; user calls saveDraft() to confirm.
                    lastDraftId?.let { outbox.markFailed(it, e.message) }
                    SubmitState.Failed(error = e, category = category)
                }
            }
        }
    }
}

/**
 * Creates a [DraftSubmitHandler] bound to this [CoroutineScope] (typically `viewModelScope`).
 *
 * ```kotlin
 * // User-prompted save (default):
 * private val draftHandler = viewModelScope.draftSubmitHandler<LoanPayload, Unit>(
 *     outbox  = roomSubmitOutbox,
 *     formKey = "client_registration",
 * )
 * // Silent auto-save on network failure:
 * private val draftHandler = viewModelScope.draftSubmitHandler<LoanPayload, Unit>(
 *     outbox        = roomSubmitOutbox,
 *     formKey       = "client_registration",
 *     autoSaveDraft = true,
 * )
 * ```
 */
fun <P, R> CoroutineScope.draftSubmitHandler(
    outbox: SubmitOutbox<P>,
    formKey: String,
    autoSaveDraft: Boolean = false,
    uniqueKey: String? = null,
): DraftSubmitHandler<P, R> = DraftSubmitHandler(this, outbox, formKey, autoSaveDraft, uniqueKey)
