/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.designsystem.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(name = "Finance palette — light", showBackground = true, widthDp = 360)
@Composable
private fun FinancePalettePreviewLight() {
    KptTheme(darkTheme = false) {
        Surface { FinancePaletteSwatches() }
    }
}

@Preview(name = "Finance palette — dark", showBackground = true, widthDp = 360, backgroundColor = 0xFF13131B)
@Composable
private fun FinancePalettePreviewDark() {
    KptTheme(darkTheme = true) {
        Surface { FinancePaletteSwatches() }
    }
}

@Preview(name = "Spacing scale", showBackground = true, widthDp = 360)
@Composable
private fun SpacingScalePreview() {
    KptTheme(darkTheme = false) {
        Surface { SpacingSwatches() }
    }
}

@Preview(name = "Elevation tiers", showBackground = true, widthDp = 360)
@Composable
private fun ElevationTiersPreview() {
    KptTheme(darkTheme = false) {
        Surface { ElevationSwatches() }
    }
}

@Composable
private fun FinancePaletteSwatches() {
    val f = MaterialTheme.finance
    Column(
        modifier = Modifier.padding(MaterialTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
    ) {
        SectionLabel("Money")
        SwatchRow("moneyPositive", f.moneyPositive)
        SwatchRow("moneyNegative", f.moneyNegative)
        SwatchRow("moneyNeutral", f.moneyNeutral)
        SectionLabel("Rate")
        SwatchRow("rateUp", f.rateUp)
        SwatchRow("rateDown", f.rateDown)
        SwatchRow("rateFlat", f.rateFlat)
        SectionLabel("Freshness")
        SwatchRow("freshnessFresh", f.freshnessFresh)
        SwatchRow("freshnessStale", f.freshnessStale)
        SwatchRow("freshnessUpdating", f.freshnessUpdating)
        SwatchRow("freshnessOffline", f.freshnessOffline)
        SectionLabel("Urgency")
        SwatchRow("urgencyOverdue", f.urgencyOverdue)
        SwatchRow("urgencyToday", f.urgencyToday)
        SwatchRow("urgencyUpcoming", f.urgencyUpcoming)
        SwatchRow("urgencyDistant", f.urgencyDistant)
    }
}

@Composable
private fun SpacingSwatches() {
    val s = MaterialTheme.spacing
    Column(
        modifier = Modifier.padding(MaterialTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
    ) {
        SectionLabel("Spacing scale")
        listOf(
            "xs (4dp)" to s.xs,
            "sm (8dp)" to s.sm,
            "md (12dp)" to s.md,
            "lg (16dp)" to s.lg,
            "xl (24dp)" to s.xl,
            "xxl (32dp)" to s.xxl,
            "xxxl (48dp)" to s.xxxl,
        ).forEach { (label, dp) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(label, style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(96.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.width(dp).height(20.dp),
                ) { }
            }
        }
    }
}

@Composable
private fun ElevationSwatches() {
    val e = MaterialTheme.elevation
    Column(
        modifier = Modifier.padding(MaterialTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
    ) {
        SectionLabel("Elevation tiers")
        listOf(
            "resting (0dp)" to e.resting,
            "low (1dp)" to e.low,
            "medium (3dp)" to e.medium,
            "high (6dp)" to e.high,
            "dragging (12dp)" to e.dragging,
        ).forEach { (label, dp) ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                tonalElevation = dp,
                shadowElevation = dp,
            ) {
                Text(
                    text = label,
                    modifier = Modifier.padding(MaterialTheme.spacing.md),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = MaterialTheme.spacing.sm),
    )
}

@Composable
private fun SwatchRow(name: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(color),
        )
        Text(name, style = MaterialTheme.typography.bodyMedium)
    }
}
