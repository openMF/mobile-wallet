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

import kpt.core.base.store.error.ErrorCategory

/**
 * State machine for a single form/action submission lifecycle.
 *
 * Transitions:
 * ```
 *   Idle  ──submit()──▶  Submitting  ──success──▶  Submitted<R>
 *                                    ──failure──▶  Failed
 *   Submitted / Failed  ──reset()──▶  Idle
 *   Failed              ──retry()──▶  Submitting  (re-runs last block)
 * ```
 *
 * Analogous to [ScreenState] for reads — one sealed type, zero ad-hoc booleans.
 * Use [SubmitHandler] to drive transitions; observe [SubmitHandler.state] in the ViewModel.
 *
 * @param R The result type produced by a successful submission. Use [Unit] for fire-and-forget.
 */
sealed interface SubmitState<out R> {

    /** No submission in flight. Initial state; also restored by [SubmitHandler.reset]. */
    data object Idle : SubmitState<Nothing>

    /**
     * API call is executing. Disable the submit button and show a progress overlay.
     *
     * @param message Optional per-mutation copy supplied via
     *   [SubmitHandler.submit] / [SubmitMessages.submitting]. When non-null, UI surfaces
     *   (e.g. `SubmitProgressOverlay`) prefer this over the theme-level default copy.
     */
    data class Submitting(val message: String? = null) : SubmitState<Nothing>

    /**
     * API call completed successfully.
     *
     * @param result  The value returned by the suspend block passed to [SubmitHandler.submit].
     * @param message Optional per-mutation success copy from [SubmitMessages.submitted].
     */
    data class Submitted<out R>(
        val result: R,
        val message: String? = null,
    ) : SubmitState<R>

    /**
     * API call failed. [SubmitHandler.retry] is available to re-run the last block.
     *
     * @param error      The original throwable from the suspend block.
     * @param category   High-level classification for UI routing (Network/Auth/Server/Generic).
     *   Pre-classified so the UI doesn't need to call [categorize] itself.
     * @param draftSaved True after the user explicitly confirmed saving the draft via
     *   [DraftSubmitHandler.saveDraft]. Used by [DraftSavePrompt] to hide itself once the
     *   user has already acted — prevents the prompt from re-appearing on recomposition.
     * @param message    Optional per-mutation failure copy from [SubmitMessages.failed].
     */
    data class Failed(
        val error: Throwable,
        val category: ErrorCategory,
        val draftSaved: Boolean = false,
        val message: String? = null,
    ) : SubmitState<Nothing>
}
