/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.designsystem.chart

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import kpt.core.designsystem.theme.finance

/**
 * Shared visual tokens for every chart in `core/designsystem/chart/`.
 *
 * Reads from `MaterialTheme.colorScheme`, `MaterialTheme.typography`, and
 * `MaterialTheme.finance`. Changing a theme token propagates here without
 * touching individual chart composables.
 *
 * Multi-series palette rotates through 6 distinguishable hues — primary →
 * up → down → fresh → urgency → offline. Caller-supplied colors override
 * (see [DonutSlice.color], [BarDatum.color]).
 */
object ChartTokens {

    @Composable
    @ReadOnlyComposable
    fun multiSeriesColors(): List<Color> {
        val finance = MaterialTheme.finance
        return listOf(
            MaterialTheme.colorScheme.primary,
            finance.rateUp,
            finance.rateDown,
            finance.freshnessFresh,
            finance.urgencyToday,
            finance.freshnessOffline,
        )
    }

    @Composable
    @ReadOnlyComposable
    fun axisLabelStyle(): TextStyle = MaterialTheme.typography.bodySmall.copy(
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    @Composable
    @ReadOnlyComposable
    fun gridlineColor(): Color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)

    fun areaFillBrush(strokeColor: Color): Brush = Brush.verticalGradient(
        colors = listOf(
            strokeColor.copy(alpha = 0.24f),
            strokeColor.copy(alpha = 0.0f),
        ),
    )

    val defaultStrokeWidth = 1.5.dp
    val defaultAxisStrokeWidth = 1.0.dp
}
