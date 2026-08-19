/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.ui.freshness

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import kpt.core.base.store.error.categorize
import kpt.core.base.store.freshness.FreshnessBand
import kpt.core.base.store.freshness.FreshnessSignal

/**
 * Inline dismissible "we're showing previous data" chip for **input-type screens**
 * (Rate History period/currency picker, etc.) where the previous selection's data
 * is preserved as a stale fallback while the new key's fetch is in-flight or has
 * failed.
 *
 * Renders nothing unless [signal].band is [FreshnessBand.VeryStale] — i.e. the
 * preserved data is significantly out of date OR a fetch error has occurred. The
 * chip's label categorises the error via [categorize] when [signal].lastError is
 * non-null ("No network · Showing previous data ↺", "Server error · Showing
 * previous data ↺", etc.); falls back to a generic age-based label otherwise.
 *
 * Pair this with the per-card [FreshnessIndicator] on the title: the indicator
 * surfaces the staleness, the chip explains it inline next to the content.
 *
 * @param signal pure-staleness signal from `ViewModel.freshness.collectAsStateWithLifecycle()`
 * @param onRetry tapping the chip re-fires the in-flight fetch
 * @param modifier propagates to the [SuggestionChip]
 */
@Composable
fun RefreshStateChip(
    signal: FreshnessSignal,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (signal.band != FreshnessBand.VeryStale) return

    val label: String = signal.lastError?.let { err ->
        "${categorize(err).toShortMessage()} · Showing previous data"
    } ?: "Showing previous data"

    SuggestionChip(
        onClick = onRetry,
        label = { Text(label) },
        icon = {
            Icon(
                imageVector = if (signal.lastError != null) {
                    Icons.Outlined.Warning
                } else {
                    Icons.Outlined.Refresh
                },
                contentDescription = null,
                tint = if (signal.lastError != null) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.tertiary
                },
                modifier = Modifier.size(16.dp),
            )
        },
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = if (signal.lastError != null) {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f)
            } else {
                Color.Transparent
            },
            labelColor = if (signal.lastError != null) {
                MaterialTheme.colorScheme.onErrorContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
        ),
        modifier = modifier.testTag(REFRESH_STATE_CHIP_TEST_TAG),
    )
}

/** Test tag for the [RefreshStateChip] root SuggestionChip. */
const val REFRESH_STATE_CHIP_TEST_TAG: String = "refresh_state_chip"
