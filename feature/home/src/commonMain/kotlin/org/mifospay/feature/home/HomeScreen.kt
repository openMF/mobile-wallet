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
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import mobile_wallet.feature.home.generated.resources.Res
import mobile_wallet.feature.home.generated.resources.arrow_backward
import mobile_wallet.feature.home.generated.resources.coin_image
import mobile_wallet.feature.home.generated.resources.feature_home_desc
import mobile_wallet.feature.home.generated.resources.feature_home_loading
import mobile_wallet.feature.home.generated.resources.start_sending_your_money_tax_free
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.common.CurrencyFormatter
import org.mifospay.core.designsystem.component.BasicDialogState
import org.mifospay.core.designsystem.component.LoadingDialogState
import org.mifospay.core.designsystem.component.MfLoadingWheel
import org.mifospay.core.designsystem.component.MifosBasicDialog
import org.mifospay.core.designsystem.component.MifosLoadingDialog
import org.mifospay.core.designsystem.component.scrollbar.DraggableScrollbar
import org.mifospay.core.designsystem.component.scrollbar.rememberDraggableScroller
import org.mifospay.core.designsystem.component.scrollbar.scrollbarState
import org.mifospay.core.designsystem.theme.NewUi
import org.mifospay.core.model.account.Account
import org.mifospay.core.model.client.Client
import org.mifospay.core.model.client.ClientStatus
import org.mifospay.core.model.client.ClientTimeline
import org.mifospay.core.model.savingsaccount.Currency
import org.mifospay.core.model.savingsaccount.Transaction
import org.mifospay.core.model.savingsaccount.TransactionType
import org.mifospay.core.ui.ErrorScreenContent
import org.mifospay.core.ui.TransactionItemScreen
import org.mifospay.core.ui.utils.EventsEffect

/*
 * Feature Enhancement
 * Show all saving accounts as stacked card
 * Show transaction history of selected account
 */
@Composable
internal fun HomeRoute(
    onNavigateBack: () -> Unit,
    onRequest: (String) -> Unit,
    onPay: () -> Unit,
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = koinViewModel(),
) {
    val snackbarState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val homeUIState by homeViewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(homeViewModel) { event ->
        when (event) {
            is HomeEvent.NavigateBack -> onNavigateBack.invoke()
            is HomeEvent.NavigateToRequestScreen -> onRequest(event.vpa)
            is HomeEvent.NavigateToSendScreen -> onPay.invoke()
            is HomeEvent.NavigateToClientDetailScreen -> {}
            is HomeEvent.NavigateToTransactionDetail -> {}
            is HomeEvent.NavigateToTransactionScreen -> {}
            is HomeEvent.ShowToast -> {
                scope.launch {
                    snackbarState.showSnackbar(event.message)
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        HomeDialogs(
            dialogState = homeUIState.dialogState,
            onDismissRequest = remember(homeViewModel) {
                { homeViewModel.trySendAction(HomeAction.OnDismissDialog) }
            },
        )

        when (homeUIState.viewState) {
            is HomeState.ViewState.Loading -> {
                MfLoadingWheel(
                    contentDesc = stringResource(Res.string.feature_home_loading),
                    backgroundColor = MaterialTheme.colorScheme.surface,
                )
            }

            is HomeState.ViewState.Content -> {
                val successState = homeUIState.viewState as HomeState.ViewState.Content

                HomeScreen(
                    account = successState.account,
                    transactions = successState.transactions,
                    client = homeUIState.client,
                    onRequest = remember(homeViewModel) {
                        { homeViewModel.trySendAction(HomeAction.RequestClicked) }
                    },
                    onPay = remember(homeViewModel) {
                        { homeViewModel.trySendAction(HomeAction.SendClicked) }
                    },
                    onClickViewAll = remember(homeViewModel) {
                        { homeViewModel.trySendAction(HomeAction.OnClickSeeAllTransactions) }
                    },
                    modifier = Modifier,
                )
            }

            is HomeState.ViewState.Error -> {
                ErrorScreenContent(
                    onClickRetry = remember(homeViewModel) {
                        { homeViewModel.trySendAction(HomeAction.Internal.LoadAccounts) }
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
private fun HomeScreen(
    account: Account,
    transactions: List<Transaction>,
    client: Client,
    onRequest: () -> Unit,
    onPay: () -> Unit,
    onClickViewAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val windowSizeClass = calculateWindowSizeClass()
    val state = rememberLazyListState()

    val scrollbarState = state.scrollbarState(itemsAvailable = state.layoutInfo.totalItemsCount)
    val showScrollBar = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact

    Box(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize(),
            state = state,
            contentPadding = PaddingValues(8.dp),
        ) {
            item {
                MifosWalletCard(
                    account = account,
                    clientName = client.displayName,
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

            item {
                TransactionHistoryCard(
                    modifier = Modifier.padding(vertical = 20.dp),
                    transactions = transactions,
                    onClickViewAll = onClickViewAll,
                )
            }
        }

        if (showScrollBar) {
            state.DraggableScrollbar(
                modifier = Modifier
                    .fillMaxHeight()
                    .windowInsetsPadding(WindowInsets.systemBars)
                    .padding(horizontal = 1.dp)
                    .align(Alignment.CenterEnd),
                state = scrollbarState,
                orientation = Orientation.Vertical,
                onThumbMoved = state.rememberDraggableScroller(
                    itemsAvailable = state.layoutInfo.totalItemsCount,
                ),
            )
        }
    }
}

@Composable
private fun MifosWalletCard(
    clientName: String,
    account: Account,
    modifier: Modifier = Modifier,
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
                            text = clientName,
                            fontWeight = FontWeight(400),
                            color = MaterialTheme.colorScheme.surface,
                        )
                    }

                    IconButton(
                        onClick = { },
                        modifier = Modifier.padding(end = 12.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
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

                        val accountBalance = CurrencyFormatter.format(
                            balance = account.balance,
                            currencyCode = account.currency.code,
                            maximumFractionDigits = null,
                        )

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
                        .size(26.dp),
                    imageVector = vectorResource(Res.drawable.arrow_backward),
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
                    modifier = Modifier
                        .size(26.dp)
                        .graphicsLayer(rotationZ = 180f),
                    imageVector = vectorResource(Res.drawable.arrow_backward),
                    contentDescription = "Send money",
                )
            },
        )
    }
}

@Composable
@Preview
fun MifosSendMoneyFreeCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
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
                    text = stringResource(Res.string.start_sending_your_money_tax_free),
                    color = NewUi.primaryColor,
                    fontWeight = FontWeight(500),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = stringResource(Res.string.feature_home_desc),
                    color = NewUi.onSurface,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight(300),
                )
            }

            Image(
                modifier = Modifier.weight(2.5f),
                contentScale = ContentScale.Fit,
                painter = painterResource(Res.drawable.coin_image),
                contentDescription = "coin Image",
            )
        }
    }
}

@Composable
fun TransactionHistoryCard(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier,
    onClickViewAll: () -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Transaction History",
                    color = NewUi.primaryColor,
                    fontWeight = FontWeight(500),
                )

                Box(
                    modifier = Modifier.clickable(
                        onClick = onClickViewAll,
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
            containerColor = MaterialTheme.colorScheme.surface,
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

@Composable
private fun HomeDialogs(
    dialogState: HomeState.DialogState?,
    onDismissRequest: () -> Unit,
) {
    when (dialogState) {
        is HomeState.DialogState.Error -> MifosBasicDialog(
            visibilityState = BasicDialogState.Shown(
                message = dialogState.message,
            ),
            onDismissRequest = onDismissRequest,
        )

        is HomeState.DialogState.Loading -> MifosLoadingDialog(
            visibilityState = LoadingDialogState.Shown,
        )

        null -> Unit
    }
}

@Preview
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
        client = Client(
            id = 8858,
            accountNo = "dignissim",
            externalId = "sonet",
            active = false,
            activationDate = listOf(),
            firstname = "Hollis Tyler",
            lastname = "Lindsay Salazar",
            displayName = "Janell Howell",
            mobileNo = "principes",
            emailAddress = "vicky.dominguez@example.com",
            dateOfBirth = listOf(),
            isStaff = false,
            officeId = 2628,
            officeName = "Enrique Dickson",
            savingsProductName = "Lamont Brady",
            timeline = ClientTimeline(
                submittedOnDate = listOf(),
                activatedOnDate = listOf(),
                activatedByUsername = null,
                activatedByFirstname = null,
                activatedByLastname = null,
            ),
            status = ClientStatus(
                id = 6242,
                code = "possim",
                value = "accommodare",
            ),
            legalForm = ClientStatus(
                id = 2235,
                code = "unum",
                value = "laudem",
            ),
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
                clientId = 2455,
                accountId = 4898,
                date = "putent",
                transferId = 8928,
                transferDetail = null,
                receiptId = null,
            )
        },
        onPay = {},
        onRequest = {},
        onClickViewAll = {},
    )
}

@Preview
@Composable
private fun PayRequestScreenPreview() {
    PayRequestScreen(
        onRequest = {},
        onSend = {},
    )
}
