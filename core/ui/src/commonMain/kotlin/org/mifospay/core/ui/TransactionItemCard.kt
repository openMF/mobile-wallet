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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mobile_wallet.core.ui.generated.resources.Res
import mobile_wallet.core.ui.generated.resources.core_ui_money_in
import mobile_wallet.core.ui.generated.resources.core_ui_money_out
import org.jetbrains.compose.resources.painterResource
import org.mifospay.core.common.CurrencyFormatter
import org.mifospay.core.designsystem.theme.green
import org.mifospay.core.designsystem.theme.red
import org.mifospay.core.model.savingsaccount.Transaction
import org.mifospay.core.model.savingsaccount.TransactionType

@Composable
fun TransactionItemCard(
    transaction: Transaction,
    onClick: (Long, Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        onClick = {
            onClick(transaction.accountId, transaction.transactionId)
        },
        color = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Absolute.SpaceBetween,
        ) {
            Column(
                modifier = Modifier,
            ) {
                Text(
                    text = transaction.transactionType.toString(),
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight(500),
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                )
                Text(
                    text = transaction.date,
                    style = TextStyle(
                        fontSize = 10.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0x66000000),
                    ),
                )
            }
            val formattedAmount = CurrencyFormatter.format(
                balance = transaction.amount,
                currencyCode = transaction.currency.code,
                maximumFractionDigits = 2,
            )
            val amount = when (transaction.transactionType) {
                TransactionType.DEBIT -> "- $formattedAmount"
                TransactionType.CREDIT -> "+ $formattedAmount"
                else -> formattedAmount
            }
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

@Composable
fun TransactionItem(
    transaction: Transaction,
    modifier: Modifier = Modifier,
    showLeadingIcon: Boolean = true,
    onClick: (Long, Long) -> Unit,
) {
    Surface(
        modifier = modifier,
        onClick = {
            onClick(transaction.accountId, transaction.transactionId)
        },
        color = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
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
                        text = transaction.transactionType.name.uppercase(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight(500),
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
                val formattedAmount = CurrencyFormatter.format(
                    balance = transaction.amount,
                    currencyCode = transaction.currency.code,
                    maximumFractionDigits = 2,
                )
                val amount = when (transaction.transactionType) {
                    TransactionType.DEBIT -> "- $formattedAmount"
                    TransactionType.CREDIT -> "+ $formattedAmount"
                    else -> formattedAmount
                }

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
