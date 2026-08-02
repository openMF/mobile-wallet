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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kpt.core.base.designsystem.component.progress.KptProgress
import kpt.core.base.designsystem.component.progress.ProgressSize
import kpt.core.base.store.submit.SubmitState

/**
 * Semi-transparent scrim + centered [KptProgress] circular spinner shown while a submission
 * is in-flight. Render above form content inside a [Box].
 *
 * Accepts either a raw [Boolean] (`submitState.isSubmitting`) or the full [SubmitState]
 * via the [SubmitState] overload below.
 *
 * ```kotlin
 * Box(Modifier.fillMaxSize()) {
 *     FormContent(enabled = !submitState.isSubmitting, onSubmit = { … })
 *     SubmitProgressOverlay(visible = submitState.isSubmitting)
 * }
 * ```
 *
 * @param visible Whether the overlay is shown.
 * @param scrimColor Overlay colour. Defaults to 38% black (Material elevation scrim spec).
 */
@Composable
fun SubmitProgressOverlay(
    visible: Boolean,
    modifier: Modifier = Modifier,
    scrimColor: Color = Color.Black.copy(alpha = 0.38f),
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(scrimColor),
            contentAlignment = Alignment.Center,
        ) {
            KptProgress(variant = KptProgress.Circular(size = ProgressSize.Lg))
        }
    }
}

/**
 * [SubmitState]-typed overload. Shows the overlay when [state] is [SubmitState.Submitting].
 *
 * ```kotlin
 * SubmitProgressOverlay(state = submitState)
 * ```
 */
@Composable
fun <R> SubmitProgressOverlay(
    state: SubmitState<R>,
    modifier: Modifier = Modifier,
    scrimColor: Color = Color.Black.copy(alpha = 0.38f),
) {
    SubmitProgressOverlay(
        visible = state is SubmitState.Submitting,
        modifier = modifier,
        scrimColor = scrimColor,
    )
}
