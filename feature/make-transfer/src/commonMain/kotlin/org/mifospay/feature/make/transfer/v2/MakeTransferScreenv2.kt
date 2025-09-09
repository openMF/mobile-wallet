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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_hide_balance
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_loading
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_no_accounts_found
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_oops_title
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_review_title
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_show_balance
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
import org.mifospay.core.model.account.Account
import org.mifospay.core.ui.AvatarBox
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.core.ui.utils.EventsEffect
import org.mifospay.feature.make.transfer.AccountList
import org.mifospay.feature.make.transfer.ClientCard
import template.core.base.designsystem.theme.KptTheme

@Composable
internal fun MakeTransferScreenV2(
    navigateBack: () -> Unit,
    onTransferSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MakeTransferV2ScreenV2ViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val accountState by viewModel.accountsState.collectAsStateWithLifecycle()

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
        accountState = accountState,
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
    accountState: ViewState,
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
            if (state.showBottomSheet && accountState is ViewState.Content) {
                AccountList(
                    accounts = accountState.data,
                    selected = remember(state) {
                        { state.selectedAccount == it }
                    },
                    onClick = {
                        onAction(MakeTransferV2Action.SelectAccount(it))
                    },
                )
            }
        },
        modifier = modifier,
    ) { paddingValues ->
        when (accountState) {
            is ViewState.Loading -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    MifosLoadingWheel(
                        contentDesc = stringResource(Res.string.feature_make_transfer_loading),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(KptTheme.spacing.md),
                    )
                }
            }

            is ViewState.Empty -> {
                EmptyContentScreen(
                    title = stringResource(Res.string.feature_make_transfer_oops_title),
                    subTitle = stringResource(Res.string.feature_make_transfer_no_accounts_found),
                )
            }

            is ViewState.Error -> {
                EmptyContentScreen(
                    title = stringResource(Res.string.feature_make_transfer_oops_title),
                    subTitle = stringResource(Res.string.feature_make_transfer_no_accounts_found),
                    iconTint = KptTheme.colorScheme.error,
                )
            }

            is ViewState.Content -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    state = lazyListState,
                    contentPadding = PaddingValues(KptTheme.spacing.md),
                    verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
                ) {
                    item {
                        ClientCard(
                            client = state.toClientData,
                            modifier = Modifier,
                        )
                    }

                    if (state.selectedAccount != null) {
                        item {
                            FromAccountCard(
                                account = state.selectedAccount,
                                modifier = Modifier,
                                onAction = onAction,
                                isOpened = state.showBottomSheet,
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
    account: Account,
    isOpened: Boolean,
    onAction: (MakeTransferV2Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    var revealBalance by remember { mutableStateOf(false) }

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
                    Text(text = account.name)
                },
                supportingContent = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
                    ) {
                        Text(text = account.number)

                        if (revealBalance) {
                            Text(text = stringResource(Res.string.feature_make_transfer_available_balance, account.balance))
                            Text(
                                text = stringResource(Res.string.feature_make_transfer_hide_balance),
                                color = KptTheme.colorScheme.primary,
                                modifier = Modifier.clickable { revealBalance = false },
                            )
                        } else {
                            Text(
                                text = stringResource(Res.string.feature_make_transfer_show_balance),
                                color = KptTheme.colorScheme.primary,
                                modifier = Modifier.clickable { revealBalance = true },
                            )
                        }
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

            if ((state.amount.toDoubleOrNull() ?: 0.0) > (state.selectedAccount?.balance ?: 0.0)) {
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
