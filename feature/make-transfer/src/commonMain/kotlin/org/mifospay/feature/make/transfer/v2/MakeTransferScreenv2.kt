/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.make.transfer.v2

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mobile_wallet.feature.make_transfer.generated.resources.Res
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_amount
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_amount_error
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_available_balance
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_check_icon_description
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_continue_button
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_description_label
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_from_account
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_loading
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_no_accounts_found
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_oops_title
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_review_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.BasicDialogState
import org.mifospay.core.designsystem.component.LoadingDialogState
import org.mifospay.core.designsystem.component.MifosBasicDialog
import org.mifospay.core.designsystem.component.MifosBottomSheetScaffold
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosLoadingDialog
import org.mifospay.core.designsystem.component.MifosLoadingWheel
import org.mifospay.core.designsystem.component.MifosTextField
import org.mifospay.core.designsystem.component.MifosTopBar
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.network.model.entity.templates.account.AccountOption
import org.mifospay.core.ui.AvatarBox
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.core.ui.utils.EventsEffect
import template.core.base.designsystem.theme.KptTheme

@Composable
internal fun MakeTransferScreenV2(
    navigateBack: () -> Unit,
    onTransferSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MakeTransferV2ScreenV2ViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            MakeTransferV2Event.OnNavigateBack -> navigateBack.invoke()
            MakeTransferV2Event.OnTransferSuccess -> onTransferSuccess.invoke()
        }
    }

    MakeTransferDialogsV2(
        dialogState = state.dialogState,
        onDismissRequest = remember(viewModel) {
            { viewModel.trySendAction(MakeTransferV2Action.DismissDialog) }
        },
    )

    MakeTransferScreenV2(
        state = state,
        modifier = modifier,
        onAction = remember(viewModel) {
            { viewModel.trySendAction(it) }
        },
    )
}

@Composable
private fun MakeTransferDialogsV2(
    dialogState: MakeTransferV2State.DialogState?,
    onDismissRequest: () -> Unit,
) {
    when (dialogState) {
        is MakeTransferV2State.DialogState.Error -> {
            val message = when (dialogState) {
                is MakeTransferV2State.DialogState.Error.StringMessage -> dialogState.message
                is MakeTransferV2State.DialogState.Error.ResourceMessage -> stringResource(dialogState.message)
            }
            MifosBasicDialog(
                visibilityState = BasicDialogState.Shown(
                    message = message,
                ),
                onDismissRequest = onDismissRequest,
            )
        }
        is MakeTransferV2State.DialogState.Loading -> MifosLoadingDialog(
            visibilityState = LoadingDialogState.Shown,
        )
        null -> Unit
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MakeTransferScreenV2(
    state: MakeTransferV2State,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    onAction: (MakeTransferV2Action) -> Unit,
) {
    MifosBottomSheetScaffold(
        topBar = {
            MifosTopBar(
                topBarTitle = stringResource(Res.string.feature_make_transfer_review_title),
                backPress = {
                    onAction(MakeTransferV2Action.NavigateBack)
                },
            )
        },
        sheetPeekHeight = if (state.showBottomSheet) 200.dp else 0.dp,
        sheetContent = {
            if (state.showBottomSheet) {
                AccountList(
                    accounts = state.fromAccountOptions ?: emptyList(),
                    selected = remember(state) {
                        { state.selectedAccount == it }
                    },
                    onClick = {
                        onAction(MakeTransferV2Action.SelectAccount(it))
                    },
                    balanceMap = state.balanceMap,
                )
            }
        },
        modifier = modifier.imePadding(),
    ) { paddingValues ->
        when (state.state) {
            is MakeTransferV2State.State.Error -> {
                EmptyContentScreen(
                    title = stringResource(Res.string.feature_make_transfer_oops_title),
                    subTitle = stringResource(Res.string.feature_make_transfer_no_accounts_found),
                    iconTint = KptTheme.colorScheme.error,
                )
            }
            MakeTransferV2State.State.Loading -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    MifosLoadingWheel(
                        contentDesc = stringResource(Res.string.feature_make_transfer_loading),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(KptTheme.spacing.md),
                    )
                }
            }
            MakeTransferV2State.State.NoAccounts -> {
                EmptyContentScreen(
                    title = stringResource(Res.string.feature_make_transfer_oops_title),
                    subTitle = stringResource(Res.string.feature_make_transfer_no_accounts_found),
                )
            }
            MakeTransferV2State.State.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    state = lazyListState,
                    contentPadding = PaddingValues(KptTheme.spacing.md),
                    verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
                ) {
                    if (state.selectedAccount != null) {
                        item {
                            FromAccountCard(
                                account = state.selectedAccount,
                                modifier = Modifier,
                                onAction = onAction,
                                isOpened = state.showBottomSheet,
                                balance = state.selectedAccountBalance.toString(),
                            )
                        }
                    }

                    item {
                        EnterAmountCard(
                            state = state,
                            onAction = onAction,
                        )
                    }

                    item {
                        MifosTextField(
                            label = stringResource(Res.string.feature_make_transfer_description_label),
                            value = state.description,
                            isError = !state.descriptionIsValid,
                            onValueChange = {
                                onAction(MakeTransferV2Action.DescriptionChanged(it))
                            },
                        )
                    }

                    item {
                        MifosButton(
                            onClick = { onAction(MakeTransferV2Action.InitiateTransfer) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = state.amountIsValid && state.descriptionIsValid,
                        ) {
                            Text(text = stringResource(Res.string.feature_make_transfer_continue_button))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FromAccountCard(
    account: AccountOption,
    balance: String,
    isOpened: Boolean,
    onAction: (MakeTransferV2Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
    ) {
        Text(
            text = stringResource(Res.string.feature_make_transfer_from_account),
            style = KptTheme.typography.labelLarge,
        )

        OutlinedCard(
            modifier = modifier.fillMaxWidth()
                .clickable {
                    if (isOpened) {
                        onAction(MakeTransferV2Action.CloseBottomSheet)
                    } else {
                        onAction(MakeTransferV2Action.OpenBottomSheet)
                    }
                },
            colors = CardDefaults.outlinedCardColors(
                containerColor = Color.Transparent,
            ),
        ) {
            ListItem(
                headlineContent = {
                    Text(text = account.clientName ?: "")
                },
                supportingContent = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
                    ) {
                        Text(text = account.accountNo ?: "")
                        Text(text = stringResource(Res.string.feature_make_transfer_available_balance, balance))
                    }
                },
                leadingContent = {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center,
                    ) {
                        AvatarBox(
                            icon = MifosIcons.Bank,
                            backgroundColor = KptTheme.colorScheme.surfaceContainerHigh,
                        )
                    }
                },
                trailingContent = {
                    if (isOpened) {
                        Icon(
                            imageVector = MifosIcons.KeyboardArrowUp,
                            contentDescription = stringResource(Res.string.feature_make_transfer_check_icon_description),
                            tint = KptTheme.colorScheme.primary,
                        )
                    } else {
                        Icon(
                            imageVector = MifosIcons.KeyboardArrowDown,
                            contentDescription = stringResource(Res.string.feature_make_transfer_check_icon_description),
                            tint = KptTheme.colorScheme.primary,
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
private fun EnterAmountCard(
    state: MakeTransferV2State,
    onAction: (MakeTransferV2Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth().then(
            if (!state.amountIsValid) {
                Modifier.border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.error,
                    shape = MaterialTheme.shapes.medium,
                )
            } else {
                Modifier
            },
        ),
        colors = CardDefaults.outlinedCardColors(
            containerColor = Color.Transparent,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KptTheme.spacing.md),
        ) {
            Text(
                text = stringResource(Res.string.feature_make_transfer_amount),
                style = KptTheme.typography.labelLarge,
            )

            MifosTextField(
                label = "",
                value = state.amount,
                isError = !state.amountIsValid,
                onValueChange = {
                    onAction(MakeTransferV2Action.AmountChanged(it))
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                ),
            )

            if ((state.amount.toDoubleOrNull() ?: 0.0) > state.selectedAccountBalance) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
                ) {
                    Icon(
                        imageVector = MifosIcons.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                    )
                    Text(
                        text = stringResource(Res.string.feature_make_transfer_amount_error),
                        color = MaterialTheme.colorScheme.error,
                        style = KptTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun AccountList(
    accounts: List<AccountOption?>,
    balanceMap: Map<String, Double>,
    selected: (AccountOption?) -> Boolean,
    modifier: Modifier = Modifier,
    onClick: (AccountOption?) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(KptTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
    ) {
        Text(
            text = stringResource(Res.string.feature_make_transfer_from_account),
            style = KptTheme.typography.labelLarge,
        )
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
        ) {
            items(items = accounts, key = { account -> account?.accountId ?: account?.accountNo ?: -1 }) { account ->
                AccountItem(
                    account = account,
                    selected = selected(account),
                    onClick = remember(account) {
                        { onClick(account) }
                    },
                    balance = balanceMap[account?.accountNo ?: ""].toString(),
                )
            }
        }
    }
}

@Composable
private fun AccountItem(
    account: AccountOption?,
    balance: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    var revealBalance by remember { mutableStateOf(false) }

    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(
            containerColor = Color.Transparent,
        ),
        onClick = onClick,
    ) {
        ListItem(
            headlineContent = {
                Text(text = account?.clientName ?: "")
            },
            supportingContent = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(text = account?.accountNo ?: "")

                    if (revealBalance) {
                        Text(text = "Available Balance: $balance")
                    } else {
                        Text(
                            text = "Show Balance",
                            color = KptTheme.colorScheme.primary,
                            style = KptTheme.typography.bodySmall,
                            modifier = Modifier.clickable { revealBalance = true },
                        )
                    }
                }
            },
            leadingContent = {
                AvatarBox(
                    icon = MifosIcons.Bank,
                    backgroundColor = KptTheme.colorScheme.surfaceContainerHigh,
                )
            },
            trailingContent = {
                AnimatedContent(
                    targetState = selected,
                    label = "radioAnim",
                ) { isSelected ->
                    Icon(
                        imageVector = if (isSelected) {
                            MifosIcons.RadioButtonChecked
                        } else {
                            MifosIcons.RadioButtonUnchecked
                        },
                        contentDescription = stringResource(
                            Res.string.feature_make_transfer_check_icon_description,
                        ),
                        tint = if (isSelected) {
                            KptTheme.colorScheme.primary
                        } else {
                            KptTheme.colorScheme.outlineVariant
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
