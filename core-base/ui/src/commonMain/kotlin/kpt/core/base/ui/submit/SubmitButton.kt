/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.ui.submit

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kpt.core.base.designsystem.component.KptButton
import kpt.core.base.store.submit.SubmitState

/**
 * Submit button that auto-disables while a submission is in-flight.
 *
 * Wraps [KptButton] and derives `enabled` from [SubmitState] so callers never have to wire
 * `enabled = !submitState.isSubmitting` manually.
 *
 * ```kotlin
 * SubmitButton(state = submitState, onClick = viewModel::onSubmit) {
 *     Text("Save")
 * }
 * ```
 *
 * @param state   Current [SubmitState]. Button is enabled only when state is not [SubmitState.Submitting].
 * @param onClick Called when the button is clicked (only fires when enabled).
 */
@Composable
fun <R> SubmitButton(
    state: SubmitState<R>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    KptButton(
        onClick = onClick,
        modifier = modifier,
        enabled = state !is SubmitState.Submitting,
        content = content,
    )
}

/**
 * Boolean-driven overload for callers that already compute the enabled flag externally
 * (e.g., `screenState.canInteract(submitState)`).
 */
@Composable
fun SubmitButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    KptButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        content = content,
    )
}
