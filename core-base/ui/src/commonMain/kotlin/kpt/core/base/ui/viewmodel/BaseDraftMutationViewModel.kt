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

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kpt.core.base.store.screen.ScreenState
import kpt.core.base.store.submit.MutationUiState
import kpt.core.base.store.submit.SubmitOutbox
import kpt.core.base.store.submit.SubmitState
import kpt.core.base.store.submit.draftSubmitHandler

/**
 * Base class for edit-screen ViewModels whose submit MUST survive process death and auto-retry
 * on reconnect.
 *
 * Sibling of [BaseSubmitMutationViewModel]. Both extend the same MVI base
 * ([BaseViewModel]); the difference is which submit handler is wrapped:
 *
 * | Class | Wraps | Persistence | Auto-retry |
 * |---|---|---|---|
 * | [BaseSubmitMutationViewModel] | `SubmitHandler<R>` | None — in-session | No |
 * | [BaseDraftMutationViewModel]  | `DraftSubmitHandler` + `SubmitOutbox` | Across restarts | Yes (syncer) |
 *
 * Use this base when the form's failure mode requires durable retry:
 * - Personal Loan Tracker (one PENDING draft per loan via [uniqueKey] = loan_id)
 * - Bill Reminders (one PENDING draft per bill via [uniqueKey] = bill_id)
 * - Loan Calculator multi-step wizard (one PENDING per step via [uniqueKey] = "step_N")
 *
 * Multi-pending semantics: pass a non-null [uniqueKey] when N concurrent independent drafts
 * must coexist under one [formKey]. Pass `null` (default) for the singleton case — one PENDING
 * draft per [formKey].
 *
 * Save modes — controlled by [autoSaveDraft]:
 * - `false` (default) — user-prompted: on network failure, UI shows DraftSavePrompt; user calls
 *   [onSaveDraft] to confirm persistence (the underlying [DraftSubmitHandler.saveDraft]).
 * - `true` — silent: on network failure, the draft is persisted to the outbox immediately and
 *   the next form open surfaces it via DraftResumeBanner.
 *
 * Subclasses override [performSubmit] for the network/repository call and populate
 * [mutableScreenState] when read-side data resolves.
 *
 * Usage:
 * ```kotlin
 * class EditLoanViewModel(
 *     outbox: SubmitOutbox<LoanPayload>,
 *     private val repo: LoanRepository,
 *     private val loanId: String,
 * ) : BaseDraftMutationViewModel<LoanPayload, Unit>(
 *     outbox = outbox,
 *     formKey = "loan_edit",
 *     uniqueKey = loanId,        // ← per-loan multi-pending
 *     autoSaveDraft = true,
 * ) {
 *     override suspend fun performSubmit(payload: LoanPayload): Unit =
 *         repo.update(loanId, payload)
 * }
 * ```
 *
 * @param T Serializable payload type (form data + result type — both `T` because mutation
 *   typically returns the persisted entity; use a separate `R` if your server returns a
 *   different shape).
 * @param R Result type returned by the server on a successful submit.
 * @param outbox Durable outbox for [T]. Inject via DI per form type.
 * @param formKey Stable identifier for the form (e.g. `"loan_edit"`). Drafts are grouped by this.
 * @param uniqueKey Optional sub-identifier when N concurrent drafts coexist under one [formKey].
 * @param autoSaveDraft When true, drafts persist silently on network failure.
 */
abstract class BaseDraftMutationViewModel<T, R>(
    outbox: SubmitOutbox<T>,
    formKey: String,
    uniqueKey: String? = null,
    autoSaveDraft: Boolean = false,
) : BaseViewModel<MutationUiState<T, R>, Nothing, MutationAction<T>>(MutationUiState()) {

    private val draftHandler = viewModelScope.draftSubmitHandler<T, R>(
        outbox = outbox,
        formKey = formKey,
        autoSaveDraft = autoSaveDraft,
        uniqueKey = uniqueKey,
    )

    protected val mutableScreenState: MutableStateFlow<ScreenState<T>> =
        MutableStateFlow(ScreenState.Loading)

    /**
     * Backward-compat alias for the inherited [stateFlow]. Mirrors [BaseSubmitMutationViewModel.uiState].
     */
    val uiState: StateFlow<MutationUiState<T, R>> get() = stateFlow

    init {
        combine(mutableScreenState, draftHandler.state) { screen, submit ->
            MutationUiState(screen = screen, submit = submit)
        }.onEach { combined ->
            updateState { combined }
        }.launchIn(viewModelScope)
    }

    /** Override to provide the network/repository call. Called with the current form payload. */
    protected abstract suspend fun performSubmit(payload: T): R

    override fun handleAction(action: MutationAction<T>) {
        when (action) {
            is MutationAction.Submit -> draftHandler.submit(action.payload) { performSubmit(it) }
            MutationAction.Retry -> draftHandler.retry()
            MutationAction.Dismiss -> draftHandler.reset()
        }
    }

    /** Trigger a form submission. No-op if already [SubmitState.Submitting]. */
    open fun onSubmit(payload: T) {
        trySendAction(MutationAction.Submit(payload))
    }

    /** Retry the last failed submission. */
    open fun onRetry() {
        trySendAction(MutationAction.Retry)
    }

    /**
     * Dismiss the result overlay and return to [SubmitState.Idle].
     *
     * Override to add VM-local cleanup (e.g. reset form fields). Always call `super.onDismissResult()`
     * to keep the inherited submit-state reset behaviour.
     */
    open fun onDismissResult() {
        trySendAction(MutationAction.Dismiss)
    }

    /**
     * User-prompted draft save. Called from the screen after the user confirms "Save Draft"
     * on the DraftSavePrompt. No-op in `autoSaveDraft = true` mode (the draft was already
     * persisted silently on the network failure) and when there is no pending payload.
     */
    fun onSaveDraft() {
        draftHandler.saveDraft()
    }

    /**
     * Discard the in-memory pending payload and dismiss the prompt. Does NOT delete already-saved
     * drafts from the outbox — use the outbox's `deleteByFormKey` / `deleteByUniqueKey` if the
     * user wants to drop a persisted draft too.
     */
    fun onDiscardDraft() {
        draftHandler.discardDraft()
    }
}
