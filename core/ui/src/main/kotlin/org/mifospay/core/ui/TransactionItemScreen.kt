/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mifospay.core.model.domain.Transaction
import com.mifospay.core.model.domain.TransactionType
import org.mifospay.common.Utils.getNewCurrencyFormatter
import org.mifospay.core.designsystem.theme.NewUi
import org.mifospay.core.designsystem.theme.green
import org.mifospay.core.designsystem.theme.red

@Preview
@Composable
fun ItemTransactionPreview() {
    TransactionItemScreen(Transaction(), modifier = Modifier)
}

@Composable
fun TransactionItemScreen(
    transaction: Transaction,
    modifier: Modifier = Modifier,
) {
    Column {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = transaction.transactionType.toString(),
                    fontWeight = FontWeight(400),
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text = transaction.date.toString(),
                    fontWeight = FontWeight(300),
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = when (transaction.transactionType) {
                        TransactionType.DEBIT -> Icons.Filled.ArrowOutward
                        TransactionType.CREDIT -> Icons.Filled.ArrowOutward
                        else -> Icons.Filled.ArrowOutward
                    },
                    modifier = when (transaction.transactionType) {
                        TransactionType.DEBIT -> Modifier.size(16.dp)
                        TransactionType.CREDIT -> Modifier.graphicsLayer(rotationZ = 180f).size(16.dp)
                        else -> Modifier.graphicsLayer(rotationZ = 180f).size(16.dp)
                    },
                    tint = when (transaction.transactionType) {
                        TransactionType.CREDIT -> green
                        TransactionType.DEBIT -> red
                        else -> Color.Black
                    },
                    contentDescription = null,
                )
                val amount = getNewCurrencyFormatter(
                    balance = transaction.amount,
                    currencySymbol = transaction.currency.displaySymbol,
                    minimumFractionDigit = 2,
                )
                Text(
                    text = " $amount",
                    fontWeight = FontWeight(400),
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            thickness = 1.dp,
            color = NewUi.onSurface.copy(alpha = 0.05f),
        )
    }
}
