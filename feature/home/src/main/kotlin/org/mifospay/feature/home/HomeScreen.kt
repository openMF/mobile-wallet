/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifospay.core.model.domain.Account
import com.mifospay.core.model.domain.Currency
import com.mifospay.core.model.domain.Transaction
import com.mifospay.core.model.domain.TransactionType
import org.mifospay.common.Utils
import org.mifospay.core.designsystem.component.MfLoadingWheel
import org.mifospay.core.designsystem.theme.border
import org.mifospay.core.designsystem.theme.lightGrey
import org.mifospay.core.ui.ErrorScreenContent
import org.mifospay.core.ui.TransactionItemScreen

@Composable
internal fun HomeRoute(
    onRequest: (String) -> Unit,
    onPay: () -> Unit,
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = hiltViewModel(),
) {
    val homeUIState by homeViewModel
        .homeUIState
        .collectAsStateWithLifecycle()

    when (homeUIState) {
        is HomeUiState.Loading -> {
            MfLoadingWheel(
                contentDesc = stringResource(R.string.feature_home_loading),
                backgroundColor = MaterialTheme.colorScheme.surface,
            )
        }

        is HomeUiState.Success -> {
            val successState = homeUIState as HomeUiState.Success
            HomeScreen(
                successState.account,
                successState.transactions,
                onRequest = {
                    onRequest.invoke(successState.vpa ?: "")
                },
                onPay = onPay,
                modifier = modifier,
            )
        }

        is HomeUiState.Error -> {
            ErrorScreenContent(
                onClickRetry = {
                    homeViewModel.fetchAccountDetails()
                },
            )
        }
    }
}

@Composable
private fun HomeScreen(
    account: Account?,
    transactions: List<Transaction>,
    onRequest: () -> Unit,
    onPay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surface),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
    ) {
        item {
            MifosWalletCardScreen(account = account)
        }
        item {
            PayRequestScreen(
                onRequest = onRequest,
                onPay = onPay,
            )
        }
        if (transactions.isNotEmpty()) {
            item {
                Text(
                    modifier = Modifier.padding(top = 32.dp),
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        items(transactions) { transaction ->
            TransactionItemScreen(transaction = transaction)
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun MifosWalletCardScreen(
    modifier: Modifier = Modifier,
    account: Account? = null,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(225.dp)
            .padding(top = 20.dp, bottom = 32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSurface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 36.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            val walletBalanceLabel =
                if (account != null) "(${account.currency.displayLabel})" else ""
            Text(
                text = "Wallet Balance $walletBalanceLabel",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W400,
                    color = lightGrey,
                ),
            )
            Spacer(modifier = Modifier.height(10.dp))
            val accountBalance =
                if (account != null) {
                    Utils.getFormattedAccountBalance(
                        account.balance,
                        account.currency.code,
                    )
                } else {
                    "0"
                }
            Text(
                text = accountBalance,
                style = TextStyle(
                    fontSize = 42.sp,
                    fontWeight = FontWeight(600),
                    color = MaterialTheme.colorScheme.surface,
                ),
            )
            Spacer(modifier = Modifier.height(10.dp))
            val currencyEqual = if (account != null) {
                "${account.currency.code}1 ${account.currency.displayLabel}"
            } else {
                ""
            }
            Text(
                text = currencyEqual,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight(500),
                    color = lightGrey,
                ),
            )
        }
    }
}

@Composable
private fun PayRequestScreen(
    onRequest: () -> Unit,
    onPay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        PayCard(
            title = "Request",
            icon = R.drawable.core_ui_money_in,
            {
                onRequest.invoke()
            },
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(16.dp))
        PayCard(
            title = "Pay",
            icon = R.drawable.core_ui_money_out,
            {
                onPay.invoke()
            },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun PayCard(
    title: String,
    icon: Int,
    onClickCard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .height(144.dp)
            .clickable { onClickCard.invoke() },
        border = BorderStroke(1.dp, border),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(top = 20.dp, bottom = 20.dp, start = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        MaterialTheme.colorScheme.onSurface,
                        shape = RoundedCornerShape(4.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surface),
                )
            }
            Text(text = title)
        }
    }
}

@Preview(showSystemUi = true, device = "id:pixel_5")
@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        account = Account(
            image = "",
            name = "Mifos",
            number = "1234567890",
            balance = 10000.0,
            id = 1L,
            currency = Currency(
                code = "USD",
                displayLabel = "$",
                displaySymbol = "$",
            ),
            productId = 1223,
        ),
        transactions = List(25) { index ->
            Transaction(
                transactionId = index.toString(),
                amount = 23004.0,
                currency = Currency(
                    code = "USD",
                    displayLabel = "$",
                    displaySymbol = "$",
                ),
                transactionType = TransactionType.CREDIT,
            )
        },
        onPay = {},
        onRequest = {},
    )
}

@Preview
@Composable
private fun PayRequestScreenPreview() {
    PayRequestScreen({}, {})
}

@Preview
@Composable
private fun PayCardPreview() {
    PayCard(
        "Request",
        R.drawable.feature_home_ic_arrow_back_black_24dp,
        { },
        Modifier.width(150.dp),
    )
}
