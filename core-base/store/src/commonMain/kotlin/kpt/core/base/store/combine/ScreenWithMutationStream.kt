/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.store.combine

import kotlinx.coroutines.flow.StateFlow

/**
 * Unified read + write + sync seam for screens that both display data and submit
 * mutations against the same domain object (edit forms, settings panels, in-place
 * record updates).
 *
 * Replaces three separate per-ViewModel state holders (`uiState` for reads, a
 * `submitState` flow for writes, and an `outbox.count` projection) with one
 * [CombinedState] flow. The read pipeline still uses [asScreenStream] /
 * [asLoadOnceStream]; the write pipeline still uses [SubmitHandler] /
 * [DraftSubmitHandler]. This seam fuses them so the screen renders against a
 * single value.
 *
 * **ViewModel usage** (when implementations land — Phase 03):
 * ```kotlin
 * private val stream = StoreFactory.createScreenWithMutation(
 *     store = loanStore,
 *     key = loanId,
 *     networkMonitor = networkMonitor,
 *     fetchedAtRepository = fetchedAtRepo,
 *     cacheKey = "loans:detail:$loanId",
 *     submitHandler = viewModelScope.submitHandler<LoanForm>(),
 *     submitBlock = { form -> loanRepository.update(loanId, form); form },
 *     scope = viewModelScope,
 * )
 * val state = stream.state
 *
 * fun onSave(form: LoanForm) = stream.submit(form)
 * fun onRetry() = stream.refresh()
 * ```
 *
 * **Screen usage** (sketch):
 * ```kotlin
 * val combined by viewModel.state.collectAsStateWithLifecycle()
 * ScreenContent(state = combined.read) { data ->
 *     LoanForm(initial = data, onSubmit = viewModel::onSave)
 *     SubmitProgressOverlay(visible = combined.mutation.isSubmitting)
 *     OutboxBadge(pending = combined.outboxPending, syncing = combined.isSyncing)
 * }
 * ```
 *
 * Phase 01 introduces the contract + a default implementation; rollout to
 * existing ViewModels (e.g. `feature/bills/BillsAddViewModel`) is a follow-up
 * sweep in a later phase.
 */
interface ScreenWithMutationStream<R, W> {

    /**
     * Combined read + write + outbox state. Backed by a hot [StateFlow] so the
     * screen can `collectAsStateWithLifecycle()` without rebuilding the pipeline
     * on each subscription.
     */
    val state: StateFlow<CombinedState<R, W>>

    /**
     * Trigger a network refresh of the read pipeline. No-op if read-policy is
     * [kpt.core.base.store.screen.FetchPolicy.CACHE_ONLY]. Mirrors
     * [kpt.core.base.store.screen.ScreenDataStream.refresh].
     */
    fun refresh()

    /**
     * Submit [payload] through the wired write pipeline. The actual API call
     * was supplied at construction time as a `suspend (W) -> Unit` block; this
     * function wraps that block and dispatches it through the underlying
     * [kpt.core.base.store.submit.SubmitHandler], surfacing progress and
     * outcome in [CombinedState.mutation].
     *
     * Idempotent — additional calls while a submission is in flight are dropped
     * silently (the underlying [kpt.core.base.store.submit.SubmitHandler]
     * enforces this).
     */
    fun submit(payload: W)

    /**
     * Cancel an in-flight submission and return [CombinedState.mutation] to
     * [kpt.core.base.store.submit.SubmitState.Idle]. Called by screens
     * after the user dismisses an error dialog or navigates away.
     */
    fun cancelPendingSubmit()
}
