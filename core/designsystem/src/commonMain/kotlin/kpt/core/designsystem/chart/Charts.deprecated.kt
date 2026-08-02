/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
@file:Suppress("unused")

package kpt.core.designsystem.chart

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kpt.core.base.designsystem.theme.motion
import kpt.core.designsystem.theme.finance

/**
 * Back-compat shims — chart composables were renamed from `Mifos*` → `Kpt*`
 * in place (same package, same file location). These forwarding functions
 * keep existing call sites compiling for one release while consumers migrate.
 *
 * `@Composable` functions cannot be `typealias`'d, so each is a forwarding
 * shim rather than a type alias. The signatures here mirror the renamed
 * composables exactly so call-site behaviour is byte-identical.
 *
 * New code should call the `Kpt*` variants directly.
 */

@Deprecated(
    "Renamed to KptSparkline",
    ReplaceWith("KptSparkline(values, modifier, color, strokeWidth, markerAtEnd)"),
    level = DeprecationLevel.WARNING,
)
@Composable
fun MifosSparkline(
    values: List<Double>,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    strokeWidth: Dp = ChartTokens.defaultStrokeWidth,
    markerAtEnd: Boolean = false,
) {
    KptSparkline(
        values = values,
        modifier = modifier,
        color = color,
        strokeWidth = strokeWidth,
        markerAtEnd = markerAtEnd,
    )
}

@Deprecated(
    "Renamed to KptAreaChart",
    ReplaceWith("KptAreaChart(values, modifier, lineColor, fillColor, strokeWidth)"),
    level = DeprecationLevel.WARNING,
)
@Composable
fun MifosAreaChart(
    values: List<Double>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    fillColor: Color = lineColor,
    strokeWidth: Dp = 2.dp,
) {
    KptAreaChart(
        values = values,
        modifier = modifier,
        lineColor = lineColor,
        fillColor = fillColor,
        strokeWidth = strokeWidth,
    )
}

@Deprecated(
    "Renamed to KptDonutChart",
    ReplaceWith("KptDonutChart(slices, modifier, strokeWidth, centerContent, animationSpec)"),
    level = DeprecationLevel.WARNING,
)
@Composable
fun MifosDonutChart(
    slices: List<DonutSlice>,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 24.dp,
    centerContent: @Composable (BoxScope.() -> Unit)? = null,
    animationSpec: AnimationSpec<Float> = tween(durationMillis = MaterialTheme.motion.durationMedium2),
) {
    KptDonutChart(
        slices = slices,
        modifier = modifier,
        strokeWidth = strokeWidth,
        centerContent = centerContent,
        animationSpec = animationSpec,
    )
}

@Deprecated(
    "Renamed to KptBarChart",
    ReplaceWith("KptBarChart(data, modifier, showLabels, barCornerRadius, animationSpec)"),
    level = DeprecationLevel.WARNING,
)
@Composable
fun MifosBarChart(
    data: List<BarDatum>,
    modifier: Modifier = Modifier,
    showLabels: Boolean = true,
    barCornerRadius: Dp = 4.dp,
    animationSpec: AnimationSpec<Float> = tween(durationMillis = MaterialTheme.motion.durationMedium2),
) {
    KptBarChart(
        data = data,
        modifier = modifier,
        showLabels = showLabels,
        barCornerRadius = barCornerRadius,
        animationSpec = animationSpec,
    )
}

@Deprecated(
    "Renamed to KptCandlestick",
    ReplaceWith("KptCandlestick(candles, modifier, upColor, downColor, wickWidth)"),
    level = DeprecationLevel.WARNING,
)
@Composable
fun MifosCandlestick(
    candles: List<Candle>,
    modifier: Modifier = Modifier,
    upColor: Color = MaterialTheme.finance.moneyPositive,
    downColor: Color = MaterialTheme.finance.moneyNegative,
    wickWidth: Dp = 1.dp,
) {
    KptCandlestick(
        candles = candles,
        modifier = modifier,
        upColor = upColor,
        downColor = downColor,
        wickWidth = wickWidth,
    )
}
