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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mifospay.core.model.domain.Transaction
import com.mifospay.core.model.domain.TransactionType
import org.mifospay.common.Utils.getFormattedAccountBalance
import org.mifospay.core.designsystem.theme.green
import org.mifospay.core.designsystem.theme.red

@Composable
fun TransactionItemScreen(
    transaction: Transaction,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.Absolute.SpaceBetween,
    ) {
        Image(
            modifier = Modifier
                .size(20.dp)
                .padding(top = 2.dp),
            painter = painterResource(
                id = when (transaction.transactionType) {
                    TransactionType.DEBIT -> R.drawable.core_ui_money_out
                    TransactionType.CREDIT -> R.drawable.core_ui_money_in
                    else -> R.drawable.core_ui_money_in
                },
            ),
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
        )
        Column(
            modifier = Modifier
                .padding(start = 32.dp)
                .weight(.3f),
        ) {
            Text(
                text = transaction.transactionType.toString(),
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight(400),
                    color = MaterialTheme.colorScheme.onSurface,

                ),
            )
            Text(
                text = transaction.date.toString(),
                style = TextStyle(
                    fontSize = 10.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0x66000000),
                ),
            )
        }
        val formattedAmount = getFormattedAccountBalance(
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
            modifier = Modifier.weight(.3f),
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

@Preview
@Composable
fun ItemTransactionPreview() {
    TransactionItemScreen(Transaction(), modifier = Modifier)
}
