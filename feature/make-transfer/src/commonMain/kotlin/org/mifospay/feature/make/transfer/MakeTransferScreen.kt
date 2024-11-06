/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.make.transfer

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import org.mifospay.core.model.account.Account
import org.mifospay.core.model.utils.PaymentQrData
import org.mifospay.core.ui.AvatarBox
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.core.ui.utils.EventsEffect

@Composable
internal fun MakeTransferScreen(
    navigateBack: () -> Unit,
    onTransferSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MakeTransferViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val accountState by viewModel.accountsState.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            MakeTransferEvent.OnNavigateBack -> navigateBack.invoke()
            MakeTransferEvent.OnTransferSuccess -> onTransferSuccess.invoke()
        }
    }

    MakeTransferDialogs(
        dialogState = state.dialogState,
        onDismissRequest = remember(viewModel) {
            { viewModel.trySendAction(MakeTransferAction.DismissDialog) }
        },
    )

    MakeTransferScreen(
        state = state,
        accountState = accountState,
        modifier = modifier,
        onAction = remember(viewModel) {
            { viewModel.trySendAction(it) }
        },
    )
}

@Composable
internal fun MakeTransferScreen(
    state: MakeTransferState,
    accountState: ViewState,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    onAction: (MakeTransferAction) -> Unit,
) {
    MifosScaffold(
        topBar = {
            MifosTopBar(
                topBarTitle = "Review Transfer",
                backPress = {
                    onAction(MakeTransferAction.NavigateBack)
                },
            )
        },
        bottomBar = {
            AccountListState(
                state = accountState,
                selected = remember(state) {
                    { state.selectedAccount == it }
                },
                onAction = onAction,
            )
        },
        modifier = modifier,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            state = lazyListState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                MifosTextField(
                    label = "Amount",
                    value = state.amount,
                    isError = !state.amountIsValid,
                    onValueChange = {
                        onAction(MakeTransferAction.AmountChanged(it))
                    },
                )
            }

            item {
                MifosTextField(
                    label = "Description",
                    value = state.description,
                    isError = !state.descriptionIsValid,
                    onValueChange = {
                        onAction(MakeTransferAction.DescriptionChanged(it))
                    },
                )
            }

            item {
                ClientCard(
                    client = state.toClientData,
                    modifier = Modifier,
                )
            }
        }
    }
}

@Composable
private fun AccountListState(
    state: ViewState,
    modifier: Modifier = Modifier,
    selected: (Account) -> Boolean,
    onAction: (MakeTransferAction) -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 2.dp,
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        color = NewUi.containerColor,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                when (state) {
                    is ViewState.Loading -> {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            MifosLoadingWheel(
                                contentDesc = "Loading",
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(16.dp),
                            )
                        }
                    }

                    is ViewState.Empty -> {
                        EmptyContentScreen(
                            title = "Oops!",
                            subTitle = "No accounts found!",
                        )
                    }

                    is ViewState.Error -> {
                        EmptyContentScreen(
                            title = "Oops!",
                            subTitle = "No accounts found!",
                        )
                    }

                    is ViewState.Content -> {
                        AccountList(
                            accounts = state.data,
                            selected = selected,
                            onClick = {
                                onAction(MakeTransferAction.SelectAccount(it))
                            },
                        )
                    }
                }
            }

            MifosButton(
                onClick = {
                    onAction(MakeTransferAction.InitiateTransfer)
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Continue")
            }
        }
    }
}

@Composable
private fun AccountList(
    accounts: List<Account>,
    selected: (Account) -> Boolean,
    modifier: Modifier = Modifier,
    onClick: (Account) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "From Account",
            style = MaterialTheme.typography.labelLarge,
        )

        accounts.forEach { account ->
            AccountItem(
                account = account,
                selected = selected(account),
                onClick = remember(account) {
                    { onClick(account) }
                },
            )
        }
    }
}

@Composable
private fun AccountItem(
    account: Account,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(
            containerColor = Color.Transparent,
        ),
        onClick = onClick,
    ) {
        ListItem(
            headlineContent = {
                Text(text = account.name)
            },
            supportingContent = {
                Text(text = account.number)
            },
            leadingContent = {
                AvatarBox(
                    icon = MifosIcons.Bank,
                    backgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                )
            },
            trailingContent = {
                AnimatedContent(
                    targetState = selected,
                ) {
                    Icon(
                        imageVector = if (it) {
                            MifosIcons.RadioButtonChecked
                        } else {
                            MifosIcons.RadioButtonUnchecked
                        },
                        contentDescription = "check",
                        tint = if (it) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outlineVariant
                        },
                    )
                }
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent,
            ),
        )
    }
}

@Composable
private fun ClientCard(
    client: PaymentQrData,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "To Account",
            style = MaterialTheme.typography.labelLarge,
        )

        OutlinedCard(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.outlinedCardColors(
                containerColor = Color.Transparent,
            ),
        ) {
            ListItem(
                headlineContent = {
                    Text(text = client.clientName)
                },
                supportingContent = {
                    Text(text = maskString(client.accountNo))
                },
                leadingContent = {
                    AvatarBox(
                        icon = MifosIcons.Bank,
                        backgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    )
                },
                trailingContent = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AccountBadge(
                            text = "WALLET",
                        )

                        AccountBadge(
                            text = "SAVING",
                            borderColor = MaterialTheme.colorScheme.secondary,
                        )
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
private fun MakeTransferDialogs(
    dialogState: MakeTransferState.DialogState?,
    onDismissRequest: () -> Unit,
) {
    when (dialogState) {
        is MakeTransferState.DialogState.Error -> MifosBasicDialog(
            visibilityState = BasicDialogState.Shown(
                message = dialogState.message,
            ),
            onDismissRequest = onDismissRequest,
        )

        is MakeTransferState.DialogState.Loading -> MifosLoadingDialog(
            visibilityState = LoadingDialogState.Shown,
        )

        null -> Unit
    }
}
