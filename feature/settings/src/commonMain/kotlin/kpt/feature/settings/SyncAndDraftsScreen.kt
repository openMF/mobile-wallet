/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kpt.core.base.designsystem.component.AppCard
import kpt.core.base.store.infra.DraftRecord
import kpt.core.base.store.submit.SubmitOutboxStatus
import kpt.core.designsystem.icon.AppIcons
import kpt.core.designsystem.theme.spacing
import kpt.core.ui.scaffold.KptScaffold
import kpt.feature.settings.generated.resources.Res
import kpt.feature.settings.generated.resources.feature_settings_sync_drafts_discard
import kpt.feature.settings.generated.resources.feature_settings_sync_drafts_discard_confirm_cancel
import kpt.feature.settings.generated.resources.feature_settings_sync_drafts_discard_confirm_message
import kpt.feature.settings.generated.resources.feature_settings_sync_drafts_discard_confirm_title
import kpt.feature.settings.generated.resources.feature_settings_sync_drafts_empty_message
import kpt.feature.settings.generated.resources.feature_settings_sync_drafts_empty_title
import kpt.feature.settings.generated.resources.feature_settings_sync_drafts_prune
import kpt.feature.settings.generated.resources.feature_settings_sync_drafts_retry
import kpt.feature.settings.generated.resources.feature_settings_sync_drafts_section_drafts
import kpt.feature.settings.generated.resources.feature_settings_sync_drafts_section_failed
import kpt.feature.settings.generated.resources.feature_settings_sync_drafts_section_syncing
import kpt.feature.settings.generated.resources.feature_settings_sync_drafts_status_failed
import kpt.feature.settings.generated.resources.feature_settings_sync_drafts_status_pending
import kpt.feature.settings.generated.resources.feature_settings_sync_drafts_status_retrying
import kpt.feature.settings.generated.resources.feature_settings_sync_drafts_title
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Template-level **Sync & Drafts** screen (Settings sub-screen). A live cross-form preview of every
 * draft the app is holding — grouped into Drafts / Syncing / Failed — with per-row Discard + Retry
 * and a manual "Prune expired" footer. See [SyncAndDraftsViewModel].
 */
@Composable
internal fun SyncAndDraftsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SyncAndDraftsViewModel = koinViewModel(),
) {
    val uiState by viewModel.stateFlow.collectAsStateWithLifecycle()
    SyncAndDraftsScreenContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onRetry = viewModel::retry,
        onDiscard = viewModel::discard,
        onPrune = viewModel::pruneExpired,
        modifier = modifier.testTag(TestTags.SyncAndDrafts.SCREEN),
    )
}

@Composable
internal fun SyncAndDraftsScreenContent(
    uiState: SyncAndDraftsUiState,
    onBackClick: () -> Unit,
    onRetry: (Long) -> Unit,
    onDiscard: (Long) -> Unit,
    onPrune: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sp = MaterialTheme.spacing

    // Discard is irreversible → confirm. Holds the row id awaiting confirmation (null = no dialog).
    var pendingDiscardId by remember { mutableStateOf<Long?>(null) }
    pendingDiscardId?.let { id ->
        AlertDialog(
            onDismissRequest = { pendingDiscardId = null },
            title = { Text(stringResource(Res.string.feature_settings_sync_drafts_discard_confirm_title)) },
            text = { Text(stringResource(Res.string.feature_settings_sync_drafts_discard_confirm_message)) },
            confirmButton = {
                TextButton(onClick = {
                    onDiscard(id)
                    pendingDiscardId = null
                }) { Text(stringResource(Res.string.feature_settings_sync_drafts_discard)) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDiscardId = null }) {
                    Text(stringResource(Res.string.feature_settings_sync_drafts_discard_confirm_cancel))
                }
            },
        )
    }

    KptScaffold(
        title = stringResource(Res.string.feature_settings_sync_drafts_title),
        onNavigationIconClick = onBackClick,
        modifier = modifier,
    ) {
        when (uiState) {
            SyncAndDraftsUiState.Loading -> LoadingState()
            is SyncAndDraftsUiState.Success -> if (uiState.isEmpty) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(sp.lg),
                    verticalArrangement = Arrangement.spacedBy(sp.sm),
                ) {
                    draftSection(
                        titleRes = Res.string.feature_settings_sync_drafts_section_failed,
                        rows = uiState.failed,
                        onRetry = onRetry,
                        onDiscardRequest = { pendingDiscardId = it },
                    )
                    draftSection(
                        titleRes = Res.string.feature_settings_sync_drafts_section_syncing,
                        rows = uiState.syncing,
                        onRetry = null,
                        onDiscardRequest = { pendingDiscardId = it },
                    )
                    draftSection(
                        titleRes = Res.string.feature_settings_sync_drafts_section_drafts,
                        rows = uiState.drafts,
                        onRetry = null,
                        onDiscardRequest = { pendingDiscardId = it },
                    )
                    item {
                        Spacer(Modifier.padding(sp.sm))
                        OutlinedButton(
                            onClick = onPrune,
                            modifier = Modifier.fillMaxWidth().testTag(TestTags.SyncAndDrafts.PRUNE),
                        ) { Text(stringResource(Res.string.feature_settings_sync_drafts_prune)) }
                    }
                }
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.draftSection(
    titleRes: StringResource,
    rows: List<DraftRecord>,
    onRetry: ((Long) -> Unit)?,
    onDiscardRequest: (Long) -> Unit,
) {
    if (rows.isEmpty()) return
    item(key = "header_$titleRes") {
        Text(
            text = "${stringResource(titleRes)} · ${rows.size}",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = MaterialTheme.spacing.sm),
        )
    }
    items(rows, key = { it.id }) { record ->
        DraftRow(
            record = record,
            onRetry = onRetry?.let { { it(record.id) } },
            onDiscard = { onDiscardRequest(record.id) },
        )
    }
}

@Composable
private fun DraftRow(record: DraftRecord, onRetry: (() -> Unit)?, onDiscard: () -> Unit) {
    val sp = MaterialTheme.spacing
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(sp.md), verticalArrangement = Arrangement.spacedBy(sp.xs)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(sp.sm)) {
                Text(
                    text = record.formTitle(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                StatusChip(record.status)
            }
            record.errorMessage?.takeIf { it.isNotBlank() }?.let { err ->
                Text(
                    text = err,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(sp.sm)) {
                if (onRetry != null) {
                    TextButton(onClick = onRetry) {
                        Icon(AppIcons.Payment, contentDescription = null, modifier = Modifier.padding(end = sp.xs))
                        Text(stringResource(Res.string.feature_settings_sync_drafts_retry))
                    }
                }
                TextButton(onClick = onDiscard) {
                    Icon(
                        AppIcons.OutlinedDelete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(end = sp.xs),
                    )
                    Text(
                        stringResource(Res.string.feature_settings_sync_drafts_discard),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusChip(status: SubmitOutboxStatus) {
    val (labelRes, container, content) = when (status) {
        SubmitOutboxStatus.FAILED -> Triple(
            Res.string.feature_settings_sync_drafts_status_failed,
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
        )
        SubmitOutboxStatus.RETRYING -> Triple(
            Res.string.feature_settings_sync_drafts_status_retrying,
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer,
        )
        else -> Triple(
            Res.string.feature_settings_sync_drafts_status_pending,
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }
    Surface(color = container, contentColor = content, shape = RoundedCornerShape(50)) {
        Text(
            text = stringResource(labelRes),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.sm, vertical = MaterialTheme.spacing.xs),
        )
    }
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyState() {
    val sp = MaterialTheme.spacing
    Column(
        modifier = Modifier.fillMaxSize().padding(sp.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = AppIcons.OutlinedDoneAll,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.padding(sp.sm))
        Text(
            text = stringResource(Res.string.feature_settings_sync_drafts_empty_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.padding(sp.xs))
        Text(
            text = stringResource(Res.string.feature_settings_sync_drafts_empty_message),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}

/**
 * Human-facing label for a draft's owning form. Falls back to the raw `formKey` (+ `uniqueKey`)
 * so a fork that adds a new form gets a sensible label with zero wiring; forks can override by
 * mapping known formKeys to display strings.
 */
private fun DraftRecord.formTitle(): String =
    if (uniqueKey.isNullOrBlank()) formKey else "$formKey · $uniqueKey"
