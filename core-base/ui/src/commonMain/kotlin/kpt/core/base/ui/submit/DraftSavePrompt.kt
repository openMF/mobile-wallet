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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kpt.core.base.designsystem.component.KptButton
import kpt.core.base.designsystem.component.KptTextButton
import kpt.core.base.store.error.ErrorCategory
import kpt.core.base.store.submit.SubmitState

/**
 * Out-of-box dialog shown when a form submission fails due to a network error.
 *
 * Renders nothing when [submitState] is not [SubmitState.Failed] with [ErrorCategory.Network],
 * or when the draft has already been saved ([SubmitState.Failed.draftSaved] == true).
 *
 * Place alongside [SubmitProgressOverlay] in your Screen composable:
 * ```kotlin
 * Box {
 *     ScreenContent(state) { data -> FormBody(data) }
 *     SubmitProgressOverlay(submitState)
 *     DraftSavePrompt(
 *         submitState = submitState,
 *         onSaveDraft = viewModel::onSaveDraft,
 *         onDismiss   = viewModel::onDismissDraftPrompt,
 *     )
 * }
 * ```
 *
 * When the user taps **Save Draft**, call `viewModel.onSaveDraft()` which delegates to
 * [kpt.core.base.store.submit.DraftSubmitHandler.saveDraft]. When the user taps
 * **Dismiss**, call `viewModel.onDismissDraftPrompt()` which delegates to
 * [kpt.core.base.store.submit.DraftSubmitHandler.discardDraft].
 */
@Composable
fun DraftSavePrompt(
    submitState: SubmitState<*>,
    onSaveDraft: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val failed = submitState as? SubmitState.Failed ?: return
    if (failed.category != ErrorCategory.Network || failed.draftSaved) return

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = {
            Text(
                text = "No connection",
                style = MaterialTheme.typography.titleMedium,
            )
        },
        text = {
            Column {
                Text(
                    text = "Your submission couldn't be sent. Save as a draft to finish later when you're back online?",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                KptTextButton(onClick = onDismiss) {
                    Text("Dismiss")
                }
                KptButton(onClick = onSaveDraft) {
                    Text("Save Draft")
                }
            }
        },
    )
}
