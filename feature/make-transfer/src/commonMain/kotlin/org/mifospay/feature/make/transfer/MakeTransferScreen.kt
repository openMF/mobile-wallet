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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mobile_wallet.feature.make_transfer.generated.resources.Res
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_amount
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_badge_saving
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_badge_wallet
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_check_icon_description
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_continue_button
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_description_label
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_from_account
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_loading
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_no_accounts_found
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_oops_title
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_review_title
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_to_account
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.common.utils.maskString
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
import org.mifospay.core.model.utils.PaymentQrData
import org.mifospay.core.ui.AvatarBox
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.core.ui.utils.EventsEffect
import template.core.base.designsystem.theme.KptTheme

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MakeTransferScreen(
    state: MakeTransferState,
    accountState: ViewState,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    onAction: (MakeTransferAction) -> Unit,
) {
    MifosBottomSheetScaffold(
        topBar = {
            MifosTopBar(
                topBarTitle = stringResource(Res.string.feature_make_transfer_review_title),
                backPress = {
                    onAction(MakeTransferAction.NavigateBack)
                },
            )
        },
        sheetContent = {
            AccountListState(
                state = accountState,
                selected = remember(state) {
                    { state.selectedAccount == it }
                },
                onAction = onAction,
            )
        },
        modifier = modifier,
        sheetPeekHeight = 200.dp,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            state = lazyListState,
            contentPadding = PaddingValues(KptTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
        ) {
            item {
                MifosTextField(
                    label = stringResource(Res.string.feature_make_transfer_amount),
                    value = state.amount,
                    isError = !state.amountIsValid,
                    onValueChange = {
                        onAction(MakeTransferAction.AmountChanged(it))
                    },
                )
            }

            item {
                MifosTextField(
                    label = stringResource(Res.string.feature_make_transfer_description_label),
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

            item {
                MifosButton(
                    onClick = { onAction(MakeTransferAction.InitiateTransfer) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = stringResource(Res.string.feature_make_transfer_continue_button))
                }
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
    Box(
        modifier = modifier.fillMaxWidth()
            .padding(KptTheme.spacing.md),
        contentAlignment = Alignment.Center,
    ) {
        when (state) {
            is ViewState.Loading -> {
                Box(modifier = Modifier.fillMaxWidth()) {
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
}

@Composable
fun AccountList(
    accounts: List<Account>,
    selected: (Account) -> Boolean,
    modifier: Modifier = Modifier,
    onClick: (Account) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
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
            items(items = accounts, key = { account -> account.id }) { account ->
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
                    backgroundColor = KptTheme.colorScheme.surfaceContainerHigh,
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
                        contentDescription = stringResource(Res.string.feature_make_transfer_check_icon_description),
                        tint = if (it) {
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

@Composable
fun ClientCard(
    client: PaymentQrData,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
    ) {
        Text(
            text = stringResource(Res.string.feature_make_transfer_to_account),
            style = KptTheme.typography.labelLarge,
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
                        backgroundColor = KptTheme.colorScheme.surfaceContainerHigh,
                    )
                },
                trailingContent = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AccountBadge(
                            text = stringResource(Res.string.feature_make_transfer_badge_wallet),
                        )

                        AccountBadge(
                            text = stringResource(Res.string.feature_make_transfer_badge_saving),
                            borderColor = KptTheme.colorScheme.secondary,
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
    borderColor: Color = KptTheme.colorScheme.primary,
) {
    OutlinedCard(
        modifier = modifier,
        colors = CardDefaults.outlinedCardColors(
            containerColor = Color.Transparent,
        ),
        border = BorderStroke(1.dp, borderColor),
        shape = KptTheme.shapes.extraSmall,
    ) {
        Text(
            text = text,
            style = KptTheme.typography.labelSmall,
            modifier = Modifier.padding(KptTheme.spacing.xs),
        )
    }
}

@Composable
private fun MakeTransferDialogs(
    dialogState: MakeTransferState.DialogState?,
    onDismissRequest: () -> Unit,
) {
    when (dialogState) {
        is MakeTransferState.DialogState.Error -> {
            val message = when (dialogState) {
                is MakeTransferState.DialogState.Error.StringMessage -> dialogState.message
                is MakeTransferState.DialogState.Error.ResourceMessage -> stringResource(dialogState.message)
            }
            MifosBasicDialog(
                visibilityState = BasicDialogState.Shown(
                    message = message,
                ),
                onDismissRequest = onDismissRequest,
            )
        }
        is MakeTransferState.DialogState.Loading -> MifosLoadingDialog(
            visibilityState = LoadingDialogState.Shown,
        )
        null -> Unit
    }
}
