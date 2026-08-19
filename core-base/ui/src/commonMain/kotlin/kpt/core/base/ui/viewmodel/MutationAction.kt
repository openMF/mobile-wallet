/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.ui.viewmodel

/**
 * MVI actions emitted by mutation-flow screens (form edit, settings save, etc.).
 *
 * Consumed by [BaseMutationViewModel]'s inherited action channel — UI calls
 * `viewModel.trySendAction(MutationAction.Submit(payload))` (or the convenience method
 * `onSubmit(payload)`) and the channel serializes processing through `handleAction`.
 *
 * @param T Domain payload type submitted to the server / repository.
 */
sealed interface MutationAction<out T> {

    /**
     * User confirmed the form — submit [payload] via the subclass's
     * [BaseMutationViewModel.performSubmit] implementation. While a submission is in flight,
     * additional Submit actions queued through the channel are processed sequentially.
     */
    data class Submit<T>(val payload: T) : MutationAction<T>

    /** Retry the last failed submission with the previously-submitted payload. */
    data object Retry : MutationAction<Nothing>

    /** Dismiss the result overlay and return the submit state to `SubmitState.Idle`. */
    data object Dismiss : MutationAction<Nothing>
}
