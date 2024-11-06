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

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.component.scrollbar.DraggableScrollbar
import org.mifospay.core.designsystem.component.scrollbar.rememberDraggableScroller
import org.mifospay.core.designsystem.component.scrollbar.scrollbarState
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.designsystem.theme.NewUi
import org.mifospay.core.model.account.Account
import org.mifospay.core.ui.ErrorScreenContent
import org.mifospay.core.ui.MifosSmallChip
import org.mifospay.core.ui.TransactionHistoryCard
import org.mifospay.core.ui.utils.EventsEffect

/*
 * Feature Enhancement
 * Show all saving accounts as stacked card
 * Show transaction history of selected account
 */
@Composable
internal fun HomeScreen(
    onNavigateBack: () -> Unit,
    onRequest: (String) -> Unit,
    onPay: () -> Unit,
    navigateToTransactionDetail: (Long, Long) -> Unit,
    navigateToAccountDetail: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val snackbarState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val homeUIState by viewModel.stateFlow.collectAsStateWithLifecycle()
    val accountState by viewModel.accountState.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            is HomeEvent.NavigateBack -> onNavigateBack.invoke()
            is HomeEvent.NavigateToRequestScreen -> onRequest(event.vpa)
            is HomeEvent.NavigateToSendScreen -> onPay.invoke()
            is HomeEvent.NavigateToClientDetailScreen -> {}
            is HomeEvent.NavigateToTransactionDetail -> {
                navigateToTransactionDetail(event.accountId, event.transactionId)
            }

            is HomeEvent.NavigateToTransactionScreen -> {}
            is HomeEvent.ShowToast -> {
                scope.launch {
                    snackbarState.showSnackbar(event.message)
                }
            }

            is HomeEvent.NavigateToAccountDetail -> {
                navigateToAccountDetail(event.accountId)
            }
        }
    }

    HomeScreenDialog(
        dialogState = homeUIState.dialogState,
        onDismissRequest = remember(viewModel) {
            { viewModel.trySendAction(HomeAction.OnDismissDialog) }
        },
    )

    HomeScreenContent(
        viewState = accountState,
        defaultAccountId = homeUIState.defaultAccountId,
        snackbarHostState = snackbarState,
        modifier = modifier,
        onAction = remember(viewModel) {
            { viewModel.trySendAction(it) }
        },
    )
}

@Composable
fun HomeScreenContent(
    viewState: ViewState,
    defaultAccountId: Long?,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onAction: (HomeAction) -> Unit,
) {
    MifosScaffold(
        modifier = modifier,
        snackbarHostState = snackbarHostState,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center,
        ) {
            when (viewState) {
                is ViewState.Loading -> {
                    MfLoadingWheel(
                        contentDesc = stringResource(Res.string.feature_home_loading),
                        backgroundColor = MaterialTheme.colorScheme.surface,
                    )
                }

                is ViewState.Content -> {
                    HomeScreenContent(
                        viewState = viewState,
                        defaultAccountId = defaultAccountId,
                        onAction = onAction,
                        modifier = Modifier,
                    )
                }

                is ViewState.Error -> {
                    ErrorScreenContent()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
private fun HomeScreenContent(
    viewState: ViewState.Content,
    defaultAccountId: Long?,
    onAction: (HomeAction) -> Unit,
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
            contentPadding = PaddingValues(12.dp),
        ) {
            item {
                AccountList(
                    accounts = viewState.accounts,
                    defaultAccountId = defaultAccountId,
                    onClick = {
                        onAction(HomeAction.AccountDetailsClicked(it))
                    },
                    onMarkAsDefault = { accId, accNo ->
                        onAction(HomeAction.MarkAsDefault(accId, accNo))
                    },
                )
            }

            item {
                PayRequestScreen(
                    modifier = Modifier.padding(vertical = 20.dp),
                    onRequest = {
                        onAction(HomeAction.RequestClicked)
                    },
                    onSend = {
                        onAction(HomeAction.SendClicked)
                    },
                )
            }

            item {
                MifosSendMoneyFreeCard()
            }

            item {
                TransactionHistoryCard(
                    modifier = Modifier.padding(vertical = 20.dp),
                    transactions = viewState.transactions,
                    onClickViewAll = {
                        onAction(HomeAction.OnClickSeeAllTransactions)
                    },
                    onViewTransaction = { accountId, transactionId ->
                        onAction(HomeAction.TransactionClicked(accountId, transactionId))
                    },
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
private fun AccountList(
    accounts: List<Account>,
    defaultAccountId: Long?,
    modifier: Modifier = Modifier,
    onMarkAsDefault: (Long, String) -> Unit,
    onClick: (Long) -> Unit,
) {
    val pagerState = rememberPagerState { accounts.size }

    HorizontalPager(
        state = pagerState,
        modifier = modifier,
    ) {
        AccountCard(
            account = accounts[it],
            defaultAccountId = defaultAccountId,
            onMarkAsDefault = onMarkAsDefault,
            onClick = onClick,
        )
    }
}

@Composable
private fun AccountCard(
    account: Account,
    defaultAccountId: Long?,
    onMarkAsDefault: (Long, String) -> Unit,
    modifier: Modifier = Modifier,
    onClick: (Long) -> Unit,
) {
    val brush = remember {
        Brush.linearGradient(
            colors = listOf(NewUi.walletColor1, NewUi.walletColor2),
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                brush = brush,
                shape = RoundedCornerShape(16.dp),
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                onClick(account.id)
            },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "Account Type",
                        fontWeight = FontWeight(300),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.surface,
                    )

                    Text(
                        text = account.name,
                        fontWeight = FontWeight(400),
                        color = MaterialTheme.colorScheme.surface,
                    )
                }

                AnimatedContent(
                    targetState = account.id == defaultAccountId,
                ) {
                    if (it) {
                        MifosSmallChip(
                            label = "Default",
                            containerColor = MaterialTheme.colorScheme.primary,
                        )
                    } else {
                        CardDropdownBox(
                            onClickDefault = {
                                onMarkAsDefault(account.id, account.number)
                            },
                        )
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = account.number,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.surface,
                    style = MaterialTheme.typography.headlineMedium,
                    letterSpacing = 0.50.sp,
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
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

@Composable
fun CardDropdownBox(
    onClickDefault: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDropdown by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(
            onClick = {
                showDropdown = !showDropdown
            },
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.surface,
            ),
        ) {
            Icon(
                imageVector = MifosIcons.MoreVert,
                contentDescription = "View More",
            )
        }

        DropdownMenu(
            expanded = showDropdown,
            onDismissRequest = { showDropdown = false },
        ) {
            DropdownMenuItem(
                text = { Text("Mark as Default") },
                onClick = {
                    onClickDefault()
                    showDropdown = false
                },
            )
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
private fun MifosSendMoneyFreeCard(
    modifier: Modifier = Modifier,
) {
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
private fun HomeScreenDialog(
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
