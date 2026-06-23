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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.component.TextComponent
import com.patrykandpatrick.vico.compose.common.vicoTheme
import com.patrykandpatrick.vico.compose.pie.PieChart
import com.patrykandpatrick.vico.compose.pie.PieChartHost
import com.patrykandpatrick.vico.compose.pie.PieSize
import com.patrykandpatrick.vico.compose.pie.data.PieChartModelProducer
import com.patrykandpatrick.vico.compose.pie.data.PieValueFormatter
import com.patrykandpatrick.vico.compose.pie.data.pieSeries
import com.patrykandpatrick.vico.compose.pie.rememberPieChart
import template.core.base.designsystem.theme.KptTheme

@Composable
actual fun MifosPieIncomeExpense(
    totalCredit: Double,
    totalDebit: Double,
) {
    val modelProducer = remember { PieChartModelProducer() }

    val total = totalCredit + totalDebit

    val creditPercentage =
        if (total > 0) (totalCredit / total) * 100 else 0.0

    val debitPercentage =
        if (total > 0) (totalDebit / total) * 100 else 0.0

    LaunchedEffect(totalCredit, totalDebit) {
        modelProducer.runTransaction {
            pieSeries {
                series(
                    creditPercentage,
                    debitPercentage,
                )
            }
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        MifosLegendItem(
            color = vicoTheme.pieChartColors[0],
            label = "Credits",
            value = totalCredit,
            percentage = creditPercentage,
        )

        MifosLegendItem(
            color = vicoTheme.pieChartColors[1],
            label = "Debits",
            value = totalDebit,
            percentage = debitPercentage,
        )
    }

    PieChartHost(
        chart = rememberPieChart(
            innerSize = PieSize.Inner.fixed(100.dp),
            sliceProvider = PieChart.SliceProvider.series(
                vicoTheme.pieChartColors.mapIndexed { index, color ->
                    PieChart.Slice(
                        fill = Fill(color),
                        label = PieChart.SliceLabel.Inside(
                            TextComponent(
                                TextStyle(
                                    color = if (index == 0) {
                                        KptTheme.colorScheme.onPrimary
                                    } else {
                                        KptTheme.colorScheme.onSecondary
                                    },
                                ),
                            ),
                        ),
                    )
                },
            ),
            valueFormatter = PieValueFormatter { _, value, _ ->
                "${value.toInt()}%"
            },
        ),
        modelProducer = modelProducer,
        modifier = Modifier.height(240.dp),
    )
}

@Composable
@Preview
fun ComposeBasicPieChartPreview() {
    MifosPieIncomeExpense(
        totalDebit = 1241.0,
        totalCredit = 1231.0,
    )
}
