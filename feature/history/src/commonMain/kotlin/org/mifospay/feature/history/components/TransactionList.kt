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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mobile_wallet.core.ui.generated.resources.Res
import mobile_wallet.core.ui.generated.resources.core_ui_money_in
import mobile_wallet.core.ui.generated.resources.core_ui_money_out
import mobile_wallet.feature.history.generated.resources.arrow_outward
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource
import org.mifospay.core.common.CurrencyFormatter
import org.mifospay.core.designsystem.theme.NewUi
import org.mifospay.core.designsystem.theme.green
import org.mifospay.core.designsystem.theme.red
import org.mifospay.core.model.savingsaccount.Transaction
import org.mifospay.core.model.savingsaccount.TransactionType
import org.mifospay.feature.history.HistoryAction

@Composable
internal fun TransactionList(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    onAction: (HistoryAction.ViewTransaction) -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = lazyListState,
    ) {
        itemsIndexed(
            items = transactions,
            key = { _, it -> it.transactionId },
        ) { i, items ->
            TransactionItem(
                transaction = items,
                modifier = Modifier,
                onClick = {
                    onAction(HistoryAction.ViewTransaction(it))
                },
            )

            if (i != transactions.size - 1) {
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    thickness = 1.dp,
                    color = NewUi.onSurface.copy(alpha = 0.05f),
                )
            }
        }
    }
}

@Composable
internal fun TransactionItem(
    transaction: Transaction,
    modifier: Modifier = Modifier,
    showLeadingIcon: Boolean = true,
    onClick: (Long) -> Unit,
) {
    Surface(
        modifier = modifier,
        enabled = transaction.transferId != null,
        onClick = {
            transaction.transferId?.let { onClick(it) }
        },
        color = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AnimatedVisibility(showLeadingIcon) {
                    Image(
                        modifier = Modifier
                            .size(20.dp)
                            .padding(top = 2.dp),
                        painter = painterResource(
                            resource = when (transaction.transactionType) {
                                TransactionType.DEBIT -> Res.drawable.core_ui_money_out
                                TransactionType.CREDIT -> Res.drawable.core_ui_money_in
                                else -> Res.drawable.core_ui_money_in
                            },
                        ),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                    )
                }

                Column {
                    Text(
                        text = transaction.transactionType.name,
                        fontWeight = FontWeight(400),
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Text(
                        text = transaction.date,
                        fontWeight = FontWeight(300),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = vectorResource(mobile_wallet.feature.history.generated.resources.Res.drawable.arrow_outward),
                    modifier = when (transaction.transactionType) {
                        TransactionType.DEBIT -> Modifier.size(16.dp)
                        TransactionType.CREDIT -> Modifier.graphicsLayer(rotationZ = 180f)
                            .size(16.dp)

                        else -> Modifier.graphicsLayer(rotationZ = 180f).size(16.dp)
                    },
                    tint = when (transaction.transactionType) {
                        TransactionType.CREDIT -> green
                        TransactionType.DEBIT -> red
                        else -> Color.Black
                    },
                    contentDescription = null,
                )
                val amount = CurrencyFormatter.format(
                    balance = transaction.amount,
                    currencyCode = transaction.currency.code,
                    maximumFractionDigits = 2,
                )
                Text(
                    modifier = Modifier,
                    text = amount,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = when (transaction.transactionType) {
                            TransactionType.DEBIT -> red
                            TransactionType.CREDIT -> green
                            else -> Color.Black
                        },
                        textAlign = TextAlign.End,
                    ),
                )
            }
        }
    }
}
