/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.history.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mobile_wallet.feature.history.generated.resources.Res
import mobile_wallet.feature.history.generated.resources.feature_history_all
import mobile_wallet.feature.history.generated.resources.feature_history_credits
import mobile_wallet.feature.history.generated.resources.feature_history_debits
import org.jetbrains.compose.resources.stringResource
import org.mifospay.core.model.savingsaccount.TransactionType
import org.mifospay.feature.history.HistoryAction

@Composable
internal fun HistoryScreenFilter(
    selectedTransactionType: TransactionType,
    modifier: Modifier = Modifier,
    onAction: (HistoryAction.SetFilter) -> Unit,
) {
    Box(modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TransactionType.entries.forEach { transactionType ->
                FilterItem(
                    transactionType = transactionType,
                    isSelected = transactionType == selectedTransactionType,
                    onAction = onAction,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun FilterItem(
    transactionType: TransactionType,
    isSelected: Boolean,
    onAction: (HistoryAction.SetFilter) -> Unit,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    unSelectedColor: Color = MaterialTheme.colorScheme.surface,
    modifier: Modifier = Modifier,
) {
    val containerColor = if (isSelected) selectedColor else unSelectedColor
    val contentColor = if (isSelected) unSelectedColor else selectedColor

    Surface(
        modifier = modifier,
        shape = CircleShape,
        contentColor = contentColor,
        color = containerColor,
        onClick = {
            onAction(HistoryAction.SetFilter(transactionType))
        },
    ) {
        Row(
            modifier = Modifier
                .defaultMinSize(
                    minWidth = ButtonDefaults.MinWidth,
                    minHeight = ButtonDefaults.MinHeight,
                )
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = when (transactionType) {
                    TransactionType.OTHER -> stringResource(Res.string.feature_history_all)
                    TransactionType.DEBIT -> stringResource(Res.string.feature_history_debits)
                    TransactionType.CREDIT -> stringResource(Res.string.feature_history_credits)
                },
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
    }
}
