/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.ui.draft

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kpt.core.base.designsystem.theme.KptTheme

/**
 * The **three-case** draft-resolution prompt shown when a user re-opens a form that already has a
 * saved draft. Extends the framework's original two-case resume banner (resume / ignore) into the
 * full set of choices the user actually needs:
 *
 * - **Resume** — reopen the form pre-filled from the saved draft ([onResume]).
 * - **Discard** — permanently delete the saved draft, then reopen the form blank ([onDiscard]).
 * - **Start fresh** — leave the saved draft untouched but open a blank form for a NEW entry
 *   ([onStartFresh]); the old draft stays recoverable from **Settings → Sync & Drafts**.
 *
 * Framework primitive (`core-base/ui`) consumed by every draft-backed form via `core/ui`. All copy is
 * a REQUIRED parameter — the consuming feature supplies its own localized `stringResource(...)`; this
 * primitive holds NO hardcoded strings (RULE-IMPL-NO-HARDCODED-STRING-001). Pair with
 * [kpt.core.base.store.submit.MutationUiState.resume] (set by a `MutationMode.Draft` ViewModel).
 *
 * @param onResume        Reopen the form pre-filled from the draft.
 * @param onDiscard       Delete the saved draft, then start blank.
 * @param onStartFresh    Keep the saved draft, but start a blank NEW entry.
 * @param onDismiss       Back-press / scrim tap — no decision made (draft untouched).
 * @param title           Dialog title (localized by the caller).
 * @param message         Dialog body (localized by the caller).
 * @param resumeLabel     Resume-button label (localized by the caller).
 * @param discardLabel    Discard-button label (localized by the caller).
 * @param startFreshLabel Start-fresh-button label (localized by the caller).
 */
@Composable
fun DraftResolutionPrompt(
    onResume: () -> Unit,
    onDiscard: () -> Unit,
    onStartFresh: () -> Unit,
    onDismiss: () -> Unit,
    title: String,
    message: String,
    resumeLabel: String,
    discardLabel: String,
    startFreshLabel: String,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = { Text(title) },
        text = { Text(message) },
        // Three actions don't fit the confirm/dismiss slots cleanly — stack them so each reads as a
        // first-class choice (Resume first/emphasised, Discard destructive-coloured).
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
            ) {
                TextButton(onClick = onResume, modifier = Modifier.fillMaxWidth()) { Text(resumeLabel) }
                TextButton(onClick = onStartFresh, modifier = Modifier.fillMaxWidth()) { Text(startFreshLabel) }
                TextButton(onClick = onDiscard, modifier = Modifier.fillMaxWidth()) {
                    Text(discardLabel, color = MaterialTheme.colorScheme.error)
                }
            }
        },
    )
}
