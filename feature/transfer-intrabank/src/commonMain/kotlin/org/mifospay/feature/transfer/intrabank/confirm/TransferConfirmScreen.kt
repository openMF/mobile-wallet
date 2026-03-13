/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.transfer.intrabank.confirm

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mobile_wallet.feature.transfer_intrabank.generated.resources.Res
import mobile_wallet.feature.transfer_intrabank.generated.resources.feature_make_transfer_amount
import mobile_wallet.feature.transfer_intrabank.generated.resources.feature_make_transfer_amount_error
import mobile_wallet.feature.transfer_intrabank.generated.resources.feature_make_transfer_available_balance
import mobile_wallet.feature.transfer_intrabank.generated.resources.feature_make_transfer_check_icon_description
import mobile_wallet.feature.transfer_intrabank.generated.resources.feature_make_transfer_continue_button
import mobile_wallet.feature.transfer_intrabank.generated.resources.feature_make_transfer_description_label
import mobile_wallet.feature.transfer_intrabank.generated.resources.feature_make_transfer_from_account
import mobile_wallet.feature.transfer_intrabank.generated.resources.feature_make_transfer_from_account_title
import mobile_wallet.feature.transfer_intrabank.generated.resources.feature_make_transfer_no_accounts_found
import mobile_wallet.feature.transfer_intrabank.generated.resources.feature_make_transfer_oops_title
import mobile_wallet.feature.transfer_intrabank.generated.resources.feature_make_transfer_review_title
import mobile_wallet.feature.transfer_intrabank.generated.resources.feature_make_transfer_show_balance
import mobile_wallet.feature.transfer_intrabank.generated.resources.feature_make_transfer_to_account
import mobile_wallet.feature.transfer_intrabank.generated.resources.feature_transfer_cancel
import mobile_wallet.feature.transfer_intrabank.generated.resources.feature_transfer_retry
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.MifosBottomSheetScaffold
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosTextField
import org.mifospay.core.designsystem.component.MifosTopBar
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.network.model.entity.templates.account.AccountOption
import org.mifospay.core.ui.AvatarBox
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.core.ui.ErrorBottomSheet
import org.mifospay.core.ui.ErrorType
import org.mifospay.core.ui.MifosProgressIndicator
import org.mifospay.core.ui.MifosProgressIndicatorOverlay
import org.mifospay.core.ui.utils.EventsEffect
import template.core.base.designsystem.KptTheme
import template.core.base.designsystem.theme.KptTheme

@Composable
internal fun TransferConfirmScreen(
    navigateBack: () -> Unit,
    onTransferSuccess: (TransferResult) -> Unit,
    navigateForPasscodeVerification: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TransferConfirmViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            TransferConfirmEvent.OnNavigateBack -> navigateBack.invoke()
            is TransferConfirmEvent.OnTransferSuccess -> onTransferSuccess.invoke(event.transferResult)
            TransferConfirmEvent.NavigateForPasscodeVerification -> {
                navigateForPasscodeVerification(INTRA_BANK_TRANSFER_VERIFICATION_KEY)
            }
        }
    }

    TransferConfirmDialogs(
        dialogState = state.dialogState,
        onDismissRequest = remember(viewModel) {
            { viewModel.trySendAction(TransferConfirmAction.DismissDialog) }
        },
        onRetry = remember(viewModel) {
            { viewModel.trySendAction(TransferConfirmAction.RetryTransfer) }
        },
    )

    TransferConfirmScreen(
        state = state,
        modifier = modifier,
        onAction = remember(viewModel) {
            { viewModel.trySendAction(it) }
        },
    )
}

@Composable
private fun TransferConfirmDialogs(
    dialogState: TransferConfirmState.DialogState?,
    onDismissRequest: () -> Unit,
    onRetry: () -> Unit,
) {
    when (dialogState) {
        is TransferConfirmState.DialogState.Error.ValidationError -> {
            ErrorBottomSheet(
                title = stringResource(Res.string.feature_make_transfer_oops_title),
                message = stringResource(dialogState.message),
                errorType = ErrorType.VALIDATION,
                onDismiss = onDismissRequest,
                dismissButtonText = stringResource(Res.string.feature_transfer_cancel),
            )
        }

        is TransferConfirmState.DialogState.Error.ApiError -> {
            val error = dialogState.error
            val errorType = when {
                error.errorCode in 500..599 -> ErrorType.SERVER
                error.errorCode in 400..499 -> ErrorType.CLIENT
                error.errorCode == null && error.canRetry -> ErrorType.NETWORK
                else -> ErrorType.GENERIC
            }

            ErrorBottomSheet(
                error = error,
                errorType = errorType,
                onDismiss = onDismissRequest,
                onRetry = onRetry,
                dismissButtonText = stringResource(Res.string.feature_transfer_cancel),
                retryButtonText = stringResource(Res.string.feature_transfer_retry),
            )
        }

        is TransferConfirmState.DialogState.Loading -> MifosProgressIndicatorOverlay()

        null -> Unit
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TransferConfirmScreen(
    state: TransferConfirmState,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    onAction: (TransferConfirmAction) -> Unit,
) {
    MifosBottomSheetScaffold(
        topBar = {
            MifosTopBar(
                topBarTitle = stringResource(Res.string.feature_make_transfer_review_title),
                backPress = {
                    onAction(TransferConfirmAction.NavigateBack)
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
                        onAction(TransferConfirmAction.SelectAccount(it))
                    },
                    balanceMap = state.balanceMap,
                )
            }
        },
        modifier = modifier,
    ) { paddingValues ->
        when (state.state) {
            is TransferConfirmState.State.Error -> {
                EmptyContentScreen(
                    title = stringResource(Res.string.feature_make_transfer_oops_title),
                    subTitle = stringResource(Res.string.feature_make_transfer_no_accounts_found),
                    iconTint = KptTheme.colorScheme.error,
                )
            }
            TransferConfirmState.State.Loading -> {
                MifosProgressIndicator()
            }
            TransferConfirmState.State.NoAccounts -> {
                EmptyContentScreen(
                    title = stringResource(Res.string.feature_make_transfer_oops_title),
                    subTitle = stringResource(Res.string.feature_make_transfer_no_accounts_found),
                )
            }
            TransferConfirmState.State.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(KptTheme.spacing.md)
                        .imePadding(),
                    state = lazyListState,
                    verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
                ) {
                    item {
                        ClientCard(
                            name = state.toAccountName,
                            account = state.toAccountNo,
                            onEdit = { onAction(TransferConfirmAction.NavigateBack) },
                        )
                    }

                    if (state.selectedAccount != null) {
                        item {
                            FromAccountCard(
                                account = state.selectedAccount,
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
                            onFocusChanged = {
                                onAction(TransferConfirmAction.CloseBottomSheet)
                            },
                        )
                    }

                    item {
                        MifosTextField(
                            label = stringResource(Res.string.feature_make_transfer_description_label),
                            value = state.description,
                            isError = !state.descriptionIsValid,
                            onValueChange = { onAction(TransferConfirmAction.DescriptionChanged(it)) },
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done,
                            ),
                            onFocusChanged = {
                                onAction(TransferConfirmAction.CloseBottomSheet)
                            },
                        )
                    }

                    item {
                        MifosButton(
                            onClick = { onAction(TransferConfirmAction.InitiateTransfer) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = state.amountIsValid && state.descriptionIsValid
                                    && state.dialogState != TransferConfirmState.DialogState.Loading,
                        ) {
//                            if (state.isProcessing) {
//                                CircularProgressIndicator(
//                                    modifier = Modifier.size(20.dp),
//                                    color = MaterialTheme.colorScheme.onPrimary,
//                                    strokeWidth = 2.dp,
//                                )
//                                Spacer(modifier = Modifier.width(8.dp))
//                            }
                            Text(text = stringResource(Res.string.feature_make_transfer_continue_button))
                        }
                    }
                }

                // Show overlay when processing
//                if (state.isProcessing) {
//                    MifosProgressIndicatorOverlay()
//                }
            }
        }
    }
}

@Composable
private fun FromAccountCard(
    account: AccountOption,
    balance: String,
    isOpened: Boolean,
    onAction: (TransferConfirmAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth()
            .clickable {
                if (isOpened) {
                    onAction(TransferConfirmAction.CloseBottomSheet)
                } else {
                    onAction(TransferConfirmAction.OpenBottomSheet)
                }
            },
        colors = CardDefaults.outlinedCardColors(
            containerColor = Color.Transparent,
        ),
    ) {
        ListItem(
            headlineContent = {
                BasicText(
                    text = stringResource(
                        Res.string.feature_make_transfer_from_account,
                        account.clientName ?: "",
                    ),
                    autoSize = TextAutoSize.StepBased(
                        minFontSize = 6.sp,
                        maxFontSize = 16.sp,
                        stepSize = 1.sp,
                    ),
                    style = LocalTextStyle.current.copy(
                        color = KptTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    maxLines = 1,
                )
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
                AvatarBox(
                    icon = MifosIcons.Bank,
                    backgroundColor = KptTheme.colorScheme.surfaceContainerHigh,
                )
            },
            trailingContent = {
                if (isOpened) {
                    Icon(
                        imageVector = MifosIcons.KeyboardArrowUp,
                        contentDescription = stringResource(Res.string.feature_make_transfer_check_icon_description),
                    )
                } else {
                    Icon(
                        imageVector = MifosIcons.KeyboardArrowDown,
                        contentDescription = stringResource(Res.string.feature_make_transfer_check_icon_description),
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
private fun EnterAmountCard(
    state: TransferConfirmState,
    onAction: (TransferConfirmAction) -> Unit,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onFocusChanged: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val isFocused by interactionSource.collectIsFocusedAsState()

    LaunchedEffect(isFocused) {
        onFocusChanged(isFocused)
    }
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

            TextField(
                leadingIcon = {
                    Text(
                        text = "$",
                        style = KptTheme.typography.headlineMedium,
                    )
                },
                value = state.amount,
                onValueChange = {
                    onAction(TransferConfirmAction.AmountChanged(it))
                },
                interactionSource = interactionSource,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next,
                ),
                isError = !state.amountIsValid,
                textStyle = KptTheme.typography.headlineMedium,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    focusedIndicatorColor = KptTheme.colorScheme.primary,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
            )

            if ((state.amount.toDoubleOrNull() ?: 0.0) > state.selectedAccountBalance) {
                Spacer(modifier = Modifier.height(KptTheme.spacing.sm))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
                ) {
                    Icon(
                        imageVector = MifosIcons.Info,
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
            text = stringResource(Res.string.feature_make_transfer_from_account_title),
            style = KptTheme.typography.labelLarge,
        )
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
        ) {
            items(items = accounts) { account ->
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
                        Text(text = stringResource(Res.string.feature_make_transfer_available_balance, balance))
                    } else {
                        Text(
                            text = stringResource(Res.string.feature_make_transfer_show_balance),
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

@Composable
fun ClientCard(
    name: String,
    account: String,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(
            containerColor = Color.Transparent,
        ),
    ) {
        ListItem(
            headlineContent = {
                BasicText(
                    text = stringResource(
                        Res.string.feature_make_transfer_to_account,
                        name,
                    ),
                    autoSize = TextAutoSize.StepBased(
                        minFontSize = 6.sp,
                        maxFontSize = 16.sp,
                        stepSize = 1.sp,
                    ),
                    maxLines = 1,
                    style = LocalTextStyle.current.copy(
                        color = KptTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
            },
            supportingContent = {
                Text(text = account)
            },
            leadingContent = {
                AvatarBox(
                    icon = MifosIcons.Bank,
                    backgroundColor = KptTheme.colorScheme.surfaceContainerHigh,
                )
            },
            trailingContent = {
                Icon(
                    imageVector = MifosIcons.Edit,
                    contentDescription = stringResource(
                        Res.string.feature_make_transfer_check_icon_description,
                    ),
                    modifier = Modifier.clickable { onEdit() },
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent,
            ),
        )
    }
}

@Preview
@Composable
private fun PreviewMakeTransferLoading() {
    KptTheme {
        TransferConfirmScreen(
            state = TransferConfirmState(
                state = TransferConfirmState.State.Loading,
            ),
            onAction = {},
        )
    }
}

@Preview
@Composable
private fun PreviewMakeTransferNoAccounts() {
    KptTheme {
        TransferConfirmScreen(
            state = TransferConfirmState(
                state = TransferConfirmState.State.NoAccounts,
            ),
            onAction = {},
        )
    }
}

@Preview
@Composable
private fun PreviewMakeTransferSuccess() {
    KptTheme {
        TransferConfirmScreen(
            state = TransferConfirmState(
                state = TransferConfirmState.State.Success,
                toAccountName = "John Doe",
                toAccountNo = "123456789",
                amount = "250",
                description = "Payment for groceries",
                selectedAccount = AccountOption(
                    clientName = "Jane Smith",
                    accountNo = "987654321",
                ),
                selectedAccountBalance = 1000.0,
                balanceMap = mapOf("987654321" to 1000.0),
            ),
            onAction = {},
        )
    }
}
