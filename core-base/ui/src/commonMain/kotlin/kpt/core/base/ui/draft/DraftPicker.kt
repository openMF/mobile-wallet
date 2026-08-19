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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import kpt.core.base.designsystem.theme.KptTheme

/**
 * Full-screen picker for the **multi-pending** case — a form that can hold N concurrent drafts (one
 * per `uniqueKey`, e.g. per-loan or per-bill). When the user opens such a form and ≥2 drafts exist,
 * show this so they choose which to resume, discard individual ones, or start a brand-new entry.
 *
 * Framework primitive (`core-base/ui`) — uses raw Material3 (not `core/ui`'s KptScaffold) to avoid a
 * `core-base → core` dependency. All copy is a REQUIRED parameter — the caller supplies localized
 * `stringResource(...)`; this primitive holds NO hardcoded strings (RULE-IMPL-NO-HARDCODED-STRING-001).
 *
 * @param items          Drafts to choose from (already mapped to [DraftPickerItem]).
 * @param onResume       Resume the draft with this id (pre-fill the form from it).
 * @param onDiscard      Permanently delete the draft with this id.
 * @param onStartFresh   Ignore all drafts and open a blank NEW entry.
 * @param resumeLabel    Resume-button label (localized by the caller).
 * @param discardLabel   Discard-button label (localized by the caller).
 * @param startFreshLabel Start-fresh-button label (localized by the caller).
 */
@Composable
fun DraftPickerList(
    items: List<DraftPickerItem>,
    onResume: (Long) -> Unit,
    onDiscard: (Long) -> Unit,
    onStartFresh: () -> Unit,
    resumeLabel: String,
    discardLabel: String,
    startFreshLabel: String,
    modifier: Modifier = Modifier,
) {
    val sp = KptTheme.spacing
    Column(modifier = modifier.fillMaxSize().padding(sp.md), verticalArrangement = Arrangement.spacedBy(sp.sm)) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(sp.sm),
        ) {
            items(items, key = { it.id }) { item ->
                DraftPickerRow(
                    item = item,
                    resumeLabel = resumeLabel,
                    discardLabel = discardLabel,
                    onResume = { onResume(item.id) },
                    onDiscard = { onDiscard(item.id) },
                )
            }
        }
        OutlinedButton(onClick = onStartFresh, modifier = Modifier.fillMaxWidth()) {
            Text(startFreshLabel)
        }
    }
}

@Composable
private fun DraftPickerRow(
    item: DraftPickerItem,
    resumeLabel: String,
    discardLabel: String,
    onResume: () -> Unit,
    onDiscard: () -> Unit,
) {
    val sp = KptTheme.spacing
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(sp.md), verticalArrangement = Arrangement.spacedBy(sp.xs)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            item.subtitle?.takeIf { it.isNotBlank() }?.let { sub ->
                Text(
                    text = sub,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (item.isFailed) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(sp.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(onClick = onResume) { Text(resumeLabel) }
                TextButton(onClick = onDiscard) {
                    Text(discardLabel, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
