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
import kpt.core.base.store.submit.SubmitState
import kpt.core.base.store.submit.submitHandler

/**
 * Base class for edit-screen ViewModels that combine a read stream with a fire-and-forget submit.
 *
 * Use this when:
 * - The form's failure mode is user-recoverable in-session (network blip → user retries).
 * - No need to persist a draft across app restarts.
 * - No background sync on reconnect.
 *
 * For offline-resilient submits that survive process death and auto-retry on reconnect, use
 * [BaseDraftMutationViewModel] instead — sibling class wrapping `DraftSubmitHandler` + `SubmitOutbox`.
 *
 * Extends [BaseViewModel] with the MutationUiState/MutationAction shape baked in — submit
 * lifecycle is driven through the inherited MVI action channel, so concurrent
 * [MutationAction.Submit] / [MutationAction.Retry] / [MutationAction.Dismiss] are serialized.
 *
 * The combined screen + submit state is exposed via the inherited [stateFlow]; the legacy
 * [uiState] property is preserved as a get-only alias for backward compatibility.
 *
 * Subclasses override [performSubmit] to provide the actual network/repository call and
 * populate [mutableScreenState] when the read-side data resolves.
 *
 * Usage:
 * ```kotlin
 * class EditClientViewModel(
 *     private val repo: ClientRepository,
 *     private val clientId: String,
 * ) : BaseSubmitMutationViewModel<ClientDetail, Unit>() {
 *
 *     init {
 *         viewModelScope.launch {
 *             mutableScreenState.value = try {
 *                 ScreenState.Content(repo.getClient(clientId))
 *             } catch (e: Exception) {
 *                 ScreenState.Error(e)
 *             }
 *         }
 *     }
 *
 *     override suspend fun performSubmit(payload: ClientDetail): Unit =
 *         repo.updateClient(clientId, payload)
 * }
 *
 * // From the screen:
 * viewModel.trySendAction(MutationAction.Submit(updatedClient))
 * // — or the convenience shorthand —
 * viewModel.onSubmit(updatedClient)
 * ```
 *
 * @param T Domain type loaded for display.
 * @param R Result type returned by the server on a successful submit.
 */
abstract class BaseSubmitMutationViewModel<T, R> :
    BaseViewModel<MutationUiState<T, R>, Nothing, MutationAction<T>>(MutationUiState()) {

    private val submitHandler = viewModelScope.submitHandler<R>()

    protected val mutableScreenState: MutableStateFlow<ScreenState<T>> =
        MutableStateFlow(ScreenState.Loading)

    /**
     * Backward-compat alias for the inherited [stateFlow]. Existing callers using
     * `viewModel.uiState` keep working with no change.
     */
    val uiState: StateFlow<MutationUiState<T, R>> get() = stateFlow

    init {
        combine(mutableScreenState, submitHandler.state) { screen, submit ->
            MutationUiState(screen = screen, submit = submit)
        }.onEach { combined ->
            updateState { combined }
        }.launchIn(viewModelScope)
    }

    /** Override to provide the network/repository call. Called with the current form payload. */
    protected abstract suspend fun performSubmit(payload: T): R

    override fun handleAction(action: MutationAction<T>) {
        when (action) {
            is MutationAction.Submit -> submitHandler.submit { performSubmit(action.payload) }
            MutationAction.Retry -> submitHandler.retry()
            MutationAction.Dismiss -> submitHandler.reset()
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
}
