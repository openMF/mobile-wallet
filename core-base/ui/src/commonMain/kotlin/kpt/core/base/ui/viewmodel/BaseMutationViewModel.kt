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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kpt.core.base.store.screen.ScreenState
import kpt.core.base.store.submit.DraftResumeState
import kpt.core.base.store.submit.MutationUiState
import kpt.core.base.store.submit.SubmitState
import kpt.core.base.store.submit.draftSubmitHandler
import kpt.core.base.store.submit.resumeStateFor
import kpt.core.base.store.submit.submitHandler

/**
 * The single base class for edit/mutation-screen ViewModels — the one a feature module extends.
 *
 * It bundles a read stream ([mutableScreenState]) with a submit lifecycle, exposing both through
 * the inherited MVI [stateFlow] as [MutationUiState]. Whether the submit is a fire-and-forget
 * in-session call or a durable, offline-resilient draft is chosen by the [MutationMode] passed at
 * construction — NOT by which class you extend. This replaces the former
 * `BaseSubmitMutationViewModel` / `BaseDraftMutationViewModel` split:
 *
 * `T` is the domain entity the form edits; `R` is what the repository/server returns on submit
 * (often `Unit`, or the persisted entity):
 *
 * ```kotlin
 * // In-session (no persistence) — mode defaults to MutationMode.InSession:
 * class EditEntityViewModel(private val repo: EntityRepository, private val id: String) :
 *     BaseMutationViewModel<Entity, Unit>() {
 *     init { viewModelScope.launch { mutableScreenState.value = ScreenState.Content(repo.get(id)) } }
 *     override suspend fun performSubmit(payload: Entity) = repo.update(id, payload)
 * }
 *
 * // Offline-resilient (durable draft + auto-retry + three-case resume):
 * class EditEntityViewModel(
 *     outbox: SubmitOutbox<Entity>,
 *     entityId: String?,
 *     private val repo: EntityRepository,
 * ) : BaseMutationViewModel<Entity, Entity>(
 *     MutationMode.Draft(
 *         outbox = outbox,
 *         formKey = "entity",                // groups this form's drafts
 *         uniqueKey = entityId ?: "new",     // N concurrent drafts (one per entity); null = singleton
 *         autoSaveDraft = true,              // persist silently on network failure
 *     ),
 * ) {
 *     override suspend fun performSubmit(payload: Entity): Entity = repo.upsert(payload)
 * }
 * ```
 *
 * Submit lifecycle is driven through the inherited action channel, so concurrent
 * [MutationAction.Submit] / [MutationAction.Retry] / [MutationAction.Dismiss] are serialized. For
 * a `Draft` mode, the three-case resume ([MutationAction.ResumeDraft] / [MutationAction.StartFreshDraft]
 * / [MutationAction.DiscardSavedDraft]) is wired automatically and surfaced via
 * [MutationUiState.hasResumableDraft] / [MutationUiState.resumableDraft].
 *
 * Subclasses override [performSubmit] and populate [mutableScreenState] when read-side data resolves.
 *
 * @param T The loaded/submitted domain type.
 * @param R Result type returned by the server on a successful submit.
 * @param mode Submit strategy — [MutationMode.InSession] (default) or [MutationMode.Draft].
 */
abstract class BaseMutationViewModel<T, R>(
    private val mode: MutationMode<T> = MutationMode.InSession,
) : BaseViewModel<MutationUiState<T, R>, Nothing, MutationAction<T>>(MutationUiState()) {

    private val draftMode: MutationMode.Draft<T>? = mode as? MutationMode.Draft<T>

    private val inSessionHandler = if (draftMode == null) viewModelScope.submitHandler<R>() else null
    private val draftHandler = draftMode?.let {
        viewModelScope.draftSubmitHandler<T, R>(
            outbox = it.outbox,
            formKey = it.formKey,
            autoSaveDraft = it.autoSaveDraft,
            uniqueKey = it.uniqueKey,
        )
    }

    private val submitStateFlow: Flow<SubmitState<R>> = draftHandler?.state ?: inSessionHandler!!.state

    /**
     * Once the user resolves the resume prompt (Resume / Start fresh / Discard) we stop surfacing the
     * saved draft even though a PENDING row may still exist (Start fresh keeps it). Per-screen-session.
     */
    private val resumePromptResolved = MutableStateFlow(false)

    protected val mutableScreenState: MutableStateFlow<ScreenState<T>> =
        MutableStateFlow(ScreenState.Loading)

    /** Backward-compat alias for the inherited [stateFlow]. */
    val uiState: StateFlow<MutationUiState<T, R>> get() = stateFlow

    init {
        val resumeStream: Flow<DraftResumeState<T>> = draftMode?.let { m ->
            combine(m.outbox.resumeStateFor(m.formKey, m.uniqueKey), resumePromptResolved) { raw, resolved ->
                if (resolved) DraftResumeState.None else raw
            }
        } ?: flowOf(DraftResumeState.None)

        combine(mutableScreenState, submitStateFlow, resumeStream) { screen, submit, resume ->
            MutationUiState(screen = screen, submit = submit, resume = resume)
        }.onEach { combined ->
            updateState { combined }
        }.launchIn(viewModelScope)
    }

    /** Override to provide the network/repository call. Called with the current form payload. */
    protected abstract suspend fun performSubmit(payload: T): R

    override fun handleAction(action: MutationAction<T>) {
        when (action) {
            is MutationAction.Submit ->
                if (draftHandler != null) {
                    draftHandler.submit(action.payload) { performSubmit(it) }
                } else {
                    inSessionHandler!!.submit { performSubmit(action.payload) }
                }
            MutationAction.Retry -> if (draftHandler != null) draftHandler.retry() else inSessionHandler!!.retry()
            MutationAction.Dismiss -> if (draftHandler != null) draftHandler.reset() else inSessionHandler!!.reset()
            // Three-case resume resolution — Resume + StartFresh only dismiss the prompt (the screen
            // pre-fills from MutationUiState.resumableDraft on Resume); Discard also deletes the row.
            MutationAction.ResumeDraft, MutationAction.StartFreshDraft -> resumePromptResolved.value = true
            MutationAction.DiscardSavedDraft -> {
                draftMode?.let { m ->
                    viewModelScope.launch {
                        if (m.uniqueKey != null) {
                            m.outbox.deleteByUniqueKey(m.formKey, m.uniqueKey)
                        } else {
                            m.outbox.deleteByFormKey(m.formKey)
                        }
                    }
                }
                resumePromptResolved.value = true
            }
        }
    }

    /** Trigger a form submission. No-op if already [SubmitState.Submitting]. */
    open fun onSubmit(payload: T) = trySendAction(MutationAction.Submit(payload))

    /** Retry the last failed submission. */
    open fun onRetry() = trySendAction(MutationAction.Retry)

    /**
     * Dismiss the result overlay and return to [SubmitState.Idle]. Override to add VM-local cleanup
     * (e.g. reset form fields); always call `super.onDismissResult()` to keep the reset behaviour.
     */
    open fun onDismissResult() = trySendAction(MutationAction.Dismiss)

    /**
     * User-prompted draft save (Draft mode only) — persists the in-memory pending payload after the
     * user confirms "Save Draft". No-op in [MutationMode.InSession] and in `autoSaveDraft = true` mode.
     */
    fun onSaveDraft() {
        draftHandler?.saveDraft()
    }

    /**
     * Discard the in-memory pending payload and dismiss the save prompt (Draft mode only). Does NOT
     * delete an already-persisted draft — use [onDiscardSavedDraft] for that.
     */
    fun onDiscardDraft() {
        draftHandler?.discardDraft()
    }

    // ── Three-case resume resolution (form entry, Draft mode) ────────────────────────────────
    // Wire these to the DraftResolutionPrompt buttons; every Draft form gets the flow for free.

    /** Resume the saved draft — the screen reads [MutationUiState.resumableDraft] to pre-fill; hides the prompt. */
    open fun onResumeDraft() = trySendAction(MutationAction.ResumeDraft)

    /** Keep the saved draft (recoverable in Sync & Drafts) but start a blank new entry. */
    open fun onStartFresh() = trySendAction(MutationAction.StartFreshDraft)

    /** Permanently delete the saved draft for this form, then edit blank. */
    open fun onDiscardSavedDraft() = trySendAction(MutationAction.DiscardSavedDraft)
}
