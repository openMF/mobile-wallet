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

import kpt.core.base.store.screen.ScreenState

/**
 * Combined UI state for edit/mutation screens that load existing data before allowing changes.
 *
 * Bundles [ScreenState] (read phase) and [SubmitState] (write phase) into a single value
 * for the ViewModel to expose:
 * ```kotlin
 * data class EditClientState(
 *     val mutation: MutationUiState<ClientDetail, Unit> = MutationUiState(),
 * )
 * ```
 *
 * @param T The loaded data type (what the screen fetches to pre-populate fields).
 * @param R The submission result type ([Unit] for fire-and-forget operations).
 */
data class MutationUiState<out T, out R>(
    val screen: ScreenState<T> = ScreenState.Loading,
    val submit: SubmitState<R> = SubmitState.Idle,
) {
    /** True when data is loaded and no submission is in-flight. Use to enable the submit button. */
    val canInteract: Boolean
        get() = screen is ScreenState.Content && submit !is SubmitState.Submitting

    /** True while a submission is in-flight. Use to show progress indicators. */
    val isSubmitting: Boolean
        get() = submit is SubmitState.Submitting

    /** Loaded data, or null when screen is not yet [ScreenState.Content]. */
    val dataOrNull: T?
        get() = (screen as? ScreenState.Content)?.data
}
