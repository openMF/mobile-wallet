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

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifospay.core.model.domain.Account
import com.mifospay.core.model.domain.Currency
import com.mifospay.core.model.domain.Transaction
import com.mifospay.core.model.domain.TransactionType
import org.koin.androidx.compose.koinViewModel
import org.mifospay.common.Utils
import org.mifospay.core.designsystem.component.MfLoadingWheel
import org.mifospay.core.designsystem.theme.NewUi
import org.mifospay.core.ui.ErrorScreenContent
import org.mifospay.core.ui.TransactionItemScreen

@Composable
internal fun HomeRoute(
    onRequest: (String) -> Unit,
    onPay: () -> Unit,
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = koinViewModel(),
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
            .background(MaterialTheme.colorScheme.surface),
        contentPadding = PaddingValues(horizontal = 20.dp),
    ) {
        item {
            MifosWalletCard(
                modifier = Modifier.padding(top = 20.dp),
                account = account,
            )
        }

        item {
            PayRequestScreen(
                modifier = Modifier.padding(vertical = 20.dp),
                onRequest = onRequest,
                onSend = onPay,
            )
        }

        item {
            MifosSendMoneyFreeCard()
        }

        if (transactions.isNotEmpty()) {
            item {
                TransactionHistoryCard(
                    modifier = Modifier.padding(vertical = 20.dp),
                    transactions = transactions,
                )
            }
        }
    }
}

@Composable
private fun MifosWalletCard(
    modifier: Modifier = Modifier,
    account: Account? = null,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        NewUi.walletColor1,
                        NewUi.walletColor2,
                    ),
                ),
                shape = RoundedCornerShape(16.dp),
            ),
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent,
            ),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Client Name",
                            fontWeight = FontWeight(300),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.surface,
                        )

                        Text(
                            text = account?.name ?: "Username",
                            fontWeight = FontWeight(400),
                            color = MaterialTheme.colorScheme.surface,
                        )
                    }

                    IconButton(onClick = { }, modifier = Modifier.padding(end = 12.dp)) {
                        Icon(
                            imageVector = Icons.Filled.MoreHoriz,
                            contentDescription = "more",
                            tint = MaterialTheme.colorScheme.surface,
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Column {
                        Text(
                            text = "Wallet Balance",
                            fontWeight = FontWeight(300),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.surface,
                        )

                        val accountBalance =
                            if (account != null) {
                                Utils.getNewCurrencyFormatter(
                                    account.balance,
                                    account.currency.displaySymbol,
                                )
                            } else {
                                "0"
                            }
                        Text(
                            text = accountBalance,
                            color = MaterialTheme.colorScheme.surface,
                            style = MaterialTheme.typography.headlineLarge,
                        )
                    }

                    Icon(
                        modifier = Modifier
                            .graphicsLayer(rotationZ = 90f)
                            .padding(4.dp),
                        imageVector = Icons.Filled.KeyboardArrowUp,
                        contentDescription = "arrow",
                        tint = MaterialTheme.colorScheme.surface,
                    )
                }
            }
        }
    }
}

@Composable
private fun PayRequestScreen(
    onRequest: () -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        PaymentButton(
            modifier = Modifier
                .weight(1f)
                .height(55.dp),
            text = "Request",
            onClick = onRequest,
            leadingIcon = {
                Icon(
                    modifier = Modifier
                        .size(26.dp)
                        .graphicsLayer(rotationZ = 180f),
                    imageVector = Icons.Filled.ArrowOutward,
                    contentDescription = "request money",
                )
            },
        )

        Spacer(modifier = Modifier.width(20.dp))

        PaymentButton(
            modifier = Modifier
                .weight(1f)
                .height(55.dp),
            text = "Send",
            onClick = onSend,
            leadingIcon = {
                Icon(
                    modifier = Modifier.size(26.dp),
                    imageVector = Icons.Filled.ArrowOutward,
                    contentDescription = "Send money",
                )
            },
        )
    }
}

@Composable
@Preview(showSystemUi = true)
fun MifosSendMoneyFreeCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 20.dp, end = 10.dp, top = 20.dp, bottom = 20.dp)
                    .weight(7.5f),
            ) {
                Text(
                    text = stringResource(id = R.string.start_sending_your_money_tax_free),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight(500),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = "Mifos Pay is the best place for users to receive and send money. Start saving money now!",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight(300),
                    modifier = Modifier.padding(top = 15.dp),
                )
            }

            Image(
                modifier = Modifier.weight(2.5f),
                contentScale = ContentScale.Fit,
                painter = painterResource(id = R.drawable.coin_image),
                contentDescription = "coin Image",
            )
        }
    }
}

@Composable
fun TransactionHistoryCard(transactions: List<Transaction>, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Transaction History",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight(500),
                )

                Box(
                    modifier = Modifier.clickable(
                        onClick = { },
                    ),
                ) {
                    Text(
                        text = "See All",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight(300),
                    )
                }
            }
            transactions.forEachIndexed { _, transaction ->
                TransactionItemScreen(transaction = transaction)
            }
        }
    }
}

@Composable
private fun PaymentButton(
    text: String,
    leadingIcon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            leadingIcon()
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                fontWeight = FontWeight(400),
            )
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
