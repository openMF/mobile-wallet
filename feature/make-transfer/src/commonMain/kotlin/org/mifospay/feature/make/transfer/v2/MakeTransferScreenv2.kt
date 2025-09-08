package org.mifospay.feature.make.transfer.v2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mobile_wallet.feature.make_transfer.generated.resources.Res
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_amount
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_continue_button
import mobile_wallet.feature.make_transfer.generated.resources.feature_make_transfer_description_label
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
import org.mifospay.core.model.account.Account
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
                        onAction(MakeTransferV2Action.AmountChanged(it))
                    },
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
                ClientCard(
                    client = state.toClientData,
                    modifier = Modifier,
                )
            }

            item {
                MifosButton(
                    onClick = { onAction(MakeTransferV2Action.InitiateTransfer) },
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
    onAction: (MakeTransferV2Action) -> Unit,
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
                        onAction(MakeTransferV2Action.SelectAccount(it))
                    },
                )
            }
        }
    }
}