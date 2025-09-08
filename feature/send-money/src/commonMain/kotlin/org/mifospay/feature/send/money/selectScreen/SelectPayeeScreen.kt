package selectScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mobile_wallet.feature.send_money.generated.resources.Res
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_loading
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_no_accounts_found
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_oops
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_scan_qr
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_send
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_something_went_wrong
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.BasicDialogState.Shown
import org.mifospay.core.designsystem.component.LoadingDialogState
import org.mifospay.core.designsystem.component.MifosBasicDialog
import org.mifospay.core.designsystem.component.MifosGradientBackground
import org.mifospay.core.designsystem.component.MifosLoadingDialog
import org.mifospay.core.designsystem.component.MifosLoadingWheel
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.component.MifosTopBar
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.model.search.AccountResult
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.core.ui.MifosDivider
import org.mifospay.core.ui.MifosSearchBar
import org.mifospay.core.ui.utils.EventsEffect
import org.mifospay.feature.send.money.AccountCard
import org.mifospay.feature.send.money.SendMoneyAction
import org.mifospay.feature.send.money.SendMoneyBottomBar
import org.mifospay.feature.send.money.selectScreen.SelectScreenAction
import org.mifospay.feature.send.money.selectScreen.SelectScreenEvent
import org.mifospay.feature.send.money.selectScreen.SelectScreenState
import org.mifospay.feature.send.money.selectScreen.SelectScreenViewModel
import template.core.base.designsystem.theme.KptTheme
import v2.ViewState


@Composable
fun SelectPayeeScreen(
    modifier: Modifier = Modifier,
    navigateToMakeTransferV2Screen: (clientId: Long, clientName: String, accountNo: String, amount: Int, accountId: Long) -> Unit,
    viewModel: SelectScreenViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val accountState by viewModel.accountListState.collectAsStateWithLifecycle()

    EventsEffect(viewModel){ event->
        when(event){
            is SelectScreenEvent.NavigateToTransferScreen -> {
                navigateToMakeTransferV2Screen(
                    state.selectedAccount?.parentId ?: 0,
                    state.selectedAccount?.parentName ?: "",
                    state.selectedAccount?.entityAccountNo ?: "",
                    0,
                    state.selectedAccount?.entityId ?: 0,
                )
            }
        }
    }
    SelectPayeeDialogs(
        dialogState = state.dialogState,
        onDismissRequest = remember(viewModel) {
            {
                viewModel.trySendAction(SelectScreenAction.DismissDialog)
            }
        },
    )

    SelectAccountScreen(
        state = state,
        accountState=accountState,
        modifier = modifier,
        onAction = remember(viewModel) {
            { viewModel.trySendAction(it) }
        },
    )

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SelectAccountScreen(
    state: SelectScreenState,
    accountState: ViewState,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    onAction: (SelectScreenAction) -> Unit,
) {
    MifosGradientBackground {
        MifosScaffold(
            modifier = modifier,
            topBar = {
                    MifosTopBar(
                        topBarTitle = "Select Payment",
                        backPress = {
                            onAction(SelectScreenAction.NavigateBack)
                        },
                    )
            },
            bottomBar = {
                SendMoneyBottomBar(
                    showDetails = state.isProceedEnabled,
                    selectedAccount = state.selectedAccount,
                    onDeselect = {
                        onAction(SelectScreenAction.DeselectAccount)
                    },
                    onClickProceed = {
                        onAction(SelectScreenAction.OnProceedClicked)
                    },
                )
            },
        ) { paddingValues ->
            LazyColumn(
                state = lazyListState,
                modifier=Modifier.padding(paddingValues)
            ) {
                stickyHeader {
                    MifosSearchBar(
                        query = state.accountNumber,
                        placeHolder = "Select Account Number To Pay",
                        onQueryChange = {
                            onAction(SelectScreenAction.AccountNumberChanged(it))
                        },
                        onSearch = {
                            onAction(SelectScreenAction.AccountNumberChanged(it))
                        },
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
}



@Composable
private fun SelectPayeeDialogs(
    dialogState: SelectScreenState.DialogState?,
    onDismissRequest: () -> Unit,
) {
    when (dialogState) {
        is SelectScreenState.DialogState.Error.ResourceMessage -> MifosBasicDialog(
            visibilityState = Shown(
                message = stringResource(dialogState.message),
            ),
            onDismissRequest = onDismissRequest,
        )

        is SelectScreenState.DialogState.Error.GenericResourceMessage -> MifosBasicDialog(
            visibilityState = Shown(
                message = stringResource(
                    dialogState.message,
                    *dialogState.args.toTypedArray(),
                ),
            ),
            onDismissRequest = onDismissRequest,
        )

        is SelectScreenState.DialogState.Loading -> MifosLoadingDialog(
            visibilityState = LoadingDialogState.Shown,
        )

        null -> Unit
    }
}

private fun LazyListScope.accountListContent(
    state: ViewState,
    selected: (AccountResult) -> Boolean,
    onAction: (SelectScreenAction.SelectAccount) -> Unit,
) {
    when (state) {
        is ViewState.Loading -> {
            item {
                Box(
                    modifier = Modifier.fillParentMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    MifosLoadingWheel(contentDesc = stringResource(Res.string.feature_send_money_loading))
                }
            }
        }

        is ViewState.Error -> {
            item {
                EmptyContentScreen(
                    title = stringResource(Res.string.feature_send_money_oops),
                    subTitle = stringResource(Res.string.feature_send_money_something_went_wrong),
                    modifier = Modifier.fillParentMaxSize(),
                    iconTint = KptTheme.colorScheme.error,
                )
            }
        }

        is ViewState.Empty -> {
            item {
                EmptyContentScreen(
                    title = stringResource(Res.string.feature_send_money_oops),
                    subTitle = stringResource(Res.string.feature_send_money_no_accounts_found),
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
                        { onAction(SelectScreenAction.SelectAccount(it)) }
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