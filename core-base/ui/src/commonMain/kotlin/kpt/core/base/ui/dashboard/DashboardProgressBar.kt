/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import kpt.core.base.designsystem.component.progress.KptProgress
import kpt.core.base.designsystem.theme.KptTheme

/**
 * Top-of-dashboard progress strip showing "X of Y cards loaded".
 *
 * Hides itself when the dashboard is empty (`total == 0`) or fully loaded
 * (`loaded == total`) — apps don't have to gate the call site themselves.
 *
 * Uses [KptProgress.Linear] for the determinate bar so the rhythm matches the rest
 * of the toolkit's progress UI.
 *
 * @param state Aggregate state from [aggregateDashboardProgress].
 * @param modifier Modifier applied to the wrapping [Column].
 */
@Composable
fun DashboardProgressBar(
    state: DashboardProgressState,
    modifier: Modifier = Modifier,
) {
    // Hide when there's nothing to show or everything's already loaded.
    if (state.total == 0 || state.loaded >= state.total) return

    val spacing = KptTheme.spacing
    val progressFraction = state.loaded.toFloat() / state.total.toFloat()
    val label = "${state.loaded} of ${state.total} loaded"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = label
                liveRegion = LiveRegionMode.Polite
            },
        verticalArrangement = Arrangement.spacedBy(spacing.xs),
    ) {
        KptProgress(
            variant = KptProgress.Linear(progress = progressFraction),
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
