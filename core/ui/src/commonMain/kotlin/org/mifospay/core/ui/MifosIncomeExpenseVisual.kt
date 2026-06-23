/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.mifospay.core.common.CurrencyFormatter
import template.core.base.designsystem.theme.KptTheme

@Composable
expect fun MifosPieIncomeExpense(
    totalCredit: Double,
    totalDebit: Double,
)

@Composable
fun MifosLegendItem(
    color: Color,
    label: String,
    value: Double,
    percentage: Double,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color),
        )

        Text(
            text = "$label ₹${CurrencyFormatter.format(value, 2)} (${CurrencyFormatter.format(percentage, 2)}%)",
            style = KptTheme.typography.bodySmall,
        )
    }
}
