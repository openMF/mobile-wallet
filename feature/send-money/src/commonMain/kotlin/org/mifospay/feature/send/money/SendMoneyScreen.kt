/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.send.money

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mobile_wallet.feature.send_money.generated.resources.Res
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_amount
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_send
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.common.utils.maskString
import org.mifospay.core.designsystem.component.BasicDialogState
import org.mifospay.core.designsystem.component.LoadingDialogState
import org.mifospay.core.designsystem.component.MifosBasicDialog
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosLoadingDialog
import org.mifospay.core.designsystem.component.MifosLoadingWheel
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.component.MifosTextField
import org.mifospay.core.designsystem.component.MifosTopBar
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.designsystem.theme.NewUi
import org.mifospay.core.model.search.AccountResult
import org.mifospay.core.ui.AvatarBox
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.core.ui.MifosDivider
import org.mifospay.core.ui.utils.EventsEffect

@Composable
fun SendMoneyScreen(
    onBackClick: () -> Unit,
    navigateToTransferScreen: (String) -> Unit,
    navigateToScanQrScreen: () -> Unit,
    showTopBar: Boolean = true,
    modifier: Modifier = Modifier,
    viewModel: SendMoneyViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val accountState by viewModel.accountListState.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            SendMoneyEvent.OnNavigateBack -> onBackClick.invoke()

            is SendMoneyEvent.NavigateToTransferScreen -> {
                navigateToTransferScreen(event.data)
            }

            is SendMoneyEvent.NavigateToScanQrScreen -> navigateToScanQrScreen.invoke()
        }
    }

    SendMoneyDialogs(
        dialogState = state.dialogState,
        onDismissRequest = remember(viewModel) {
            { viewModel.trySendAction(SendMoneyAction.DismissDialog) }
        },
    )

    SendMoneyScreen(
        state = state,
        accountState = accountState,
        showTopBar = showTopBar,
        modifier = modifier,
        onAction = remember(viewModel) {
            { viewModel.trySendAction(it) }
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SendMoneyScreen(
    state: SendMoneyState,
    accountState: ViewState,
    showTopBar: Boolean,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    onAction: (SendMoneyAction) -> Unit,
) {
    MifosScaffold(
        modifier = modifier,
        topBar = {
            AnimatedVisibility(
                visible = showTopBar,
            ) {
                MifosTopBar(
                    topBarTitle = stringResource(Res.string.feature_send_money_send),
                    backPress = {
                        onAction(SendMoneyAction.NavigateBack)
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                onAction(SendMoneyAction.OnClickScan)
                            },
                        ) {
                            Icon(
                                imageVector = MifosIcons.Scan,
                                contentDescription = "Scan QR",
                            )
                        }
                    },
                )
            }
        },
        bottomBar = {
            SendMoneyBottomBar(
                showDetails = state.isProceedEnabled,
                selectedAccount = state.selectedAccount,
                onDeselect = {
                    onAction(SendMoneyAction.DeselectAccount)
                },
                onClickProceed = {
                    onAction(SendMoneyAction.OnProceedClicked)
                },
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues),
            state = lazyListState,
            contentPadding = PaddingValues(bottom = 12.dp),
        ) {
            stickyHeader {
                SendMoneyCard(
                    state = state,
                    onAction = onAction,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }

            accountListContent(
                state = accountState,
                onAction = onAction,
                selected = { state.selectedAccount == it },
            )
        }
    }
}

@Composable
private fun SendMoneyBottomBar(
    showDetails: Boolean,
    selectedAccount: AccountResult?,
    modifier: Modifier = Modifier,
    onClickProceed: () -> Unit,
    onDeselect: () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        color = NewUi.tertiaryContainer,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AnimatedVisibility(
                visible = showDetails && selectedAccount != null,
                label = "BottomBar",
                enter = fadeIn() + slideInVertically(
                    initialOffsetY = { fullHeight ->
                        fullHeight / 4
                    },
                ),
                exit = fadeOut(tween(300)) + slideOutVertically(
                    targetOffsetY = { fullHeight ->
                        fullHeight / 4
                    },
                ),
            ) {
                selectedAccount?.let {
                    SelectedAccountCard(
                        account = selectedAccount,
                        onDeselect = onDeselect,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            MifosButton(
                onClick = onClickProceed,
                enabled = showDetails,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Proceed")
            }
        }
    }
}

@Composable
private fun SelectedAccountCard(
    account: AccountResult,
    modifier: Modifier = Modifier,
    onDeselect: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "To Account",
            style = MaterialTheme.typography.labelLarge,
        )

        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.outlinedCardColors(
                containerColor = Color.Transparent,
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        ) {
            ListItem(
                headlineContent = {
                    Text(
                        text = account.parentName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                supportingContent = {
                    Text(text = maskString(account.entityAccountNo))
                },
                leadingContent = {
                    AvatarBox(icon = MifosIcons.Bank)
                },
                trailingContent = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AccountBadge(
                            text = account.entityName,
                        )

                        AccountBadge(
                            text = account.entityType,
                            borderColor = MaterialTheme.colorScheme.secondary,
                        )

                        IconButton(
                            onClick = onDeselect,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                            )
                        }
                    }
                },
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent,
                ),
            )
        }
    }
}

@Composable
private fun AccountBadge(
    text: String,
    modifier: Modifier = Modifier,
    borderColor: Color = MaterialTheme.colorScheme.primary,
) {
    OutlinedCard(
        modifier = modifier,
        colors = CardDefaults.outlinedCardColors(
            containerColor = Color.Transparent,
        ),
        border = BorderStroke(1.dp, borderColor),
        shape = RoundedCornerShape(2.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(4.dp),
        )
    }
}

@Composable
private fun SendMoneyCard(
    state: SendMoneyState,
    modifier: Modifier = Modifier,
    onAction: (SendMoneyAction) -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = NewUi.containerColor,
        ),
        shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp),
    ) {
        Column(
            modifier = modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            MifosTextField(
                label = stringResource(Res.string.feature_send_money_amount),
                value = state.amount,
                isError = !state.amountIsValid,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                ),
                onValueChange = remember(onAction) {
                    { onAction(SendMoneyAction.AmountChanged(it)) }
                },
            )

            MifosTextField(
                label = "VPA/Mobile/Account Number",
                value = state.accountNumber,
                onValueChange = remember(onAction) {
                    { onAction(SendMoneyAction.AccountNumberChanged(it)) }
                },
            )
        }
    }
}

private fun LazyListScope.accountListContent(
    state: ViewState,
    selected: (AccountResult) -> Boolean,
    onAction: (SendMoneyAction.SelectAccount) -> Unit,
) {
    when (state) {
        is ViewState.Loading -> {
            item {
                Box(
                    modifier = Modifier.fillParentMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    MifosLoadingWheel(contentDesc = "Loading")
                }
            }
        }

        is ViewState.Error -> {
            item {
                EmptyContentScreen(
                    title = "Oops!",
                    subTitle = "Something went wrong!",
                    modifier = Modifier.fillParentMaxSize(),
                )
            }
        }

        is ViewState.Empty -> {
            item {
                EmptyContentScreen(
                    title = "Oops!",
                    subTitle = "No accounts found!",
                    modifier = Modifier.fillParentMaxSize(),
                )
            }
        }

        is ViewState.Content -> {
            itemsIndexed(
                items = state.data,
                key = { _, it -> it.entityId },
            ) { i, account ->
                AccountCard(
                    account = account,
                    selected = selected,
                    onClick = remember(account) {
                        { onAction(SendMoneyAction.SelectAccount(it)) }
                    },
                )

                if (i < state.data.lastIndex) {
                    MifosDivider()
                }
            }
        }

        is ViewState.InitialEmpty -> Unit
    }
}

@Composable
private fun AccountCard(
    account: AccountResult,
    selected: (AccountResult) -> Boolean,
    modifier: Modifier = Modifier,
    onClick: (AccountResult) -> Unit,
) {
    ListItem(
        headlineContent = {
            Text(text = account.parentName)
        },
        supportingContent = {
            Text(text = maskString(account.entityAccountNo))
        },
        leadingContent = {
            AvatarBox(
                icon = MifosIcons.Bank,
                backgroundColor = NewUi.tertiaryContainer,
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent,
        ),
        trailingContent = {
            AnimatedVisibility(
                visible = selected(account),
            ) {
                Icon(
                    imageVector = MifosIcons.Check,
                    contentDescription = "Selected",
                )
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick(account)
            },
    )
}

@Composable
private fun SendMoneyDialogs(
    dialogState: SendMoneyState.DialogState?,
    onDismissRequest: () -> Unit,
) {
    when (dialogState) {
        is SendMoneyState.DialogState.Error -> MifosBasicDialog(
            visibilityState = BasicDialogState.Shown(
                message = dialogState.message,
            ),
            onDismissRequest = onDismissRequest,
        )

        is SendMoneyState.DialogState.Loading -> MifosLoadingDialog(
            visibilityState = LoadingDialogState.Shown,
        )

        null -> Unit
    }
}
