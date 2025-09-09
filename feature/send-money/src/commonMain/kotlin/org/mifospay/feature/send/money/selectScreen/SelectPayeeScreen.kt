/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.send.money.selectScreen

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mobile_wallet.feature.send_money.generated.resources.Res
import mobile_wallet.feature.send_money.generated.resources.feature_select_account_placeholder
import mobile_wallet.feature.send_money.generated.resources.feature_select_payment_title
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_bottom_bar
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_close
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_loading
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_no_accounts_found
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_oops
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_proceed
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_selected
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_something_went_wrong
import mobile_wallet.feature.send_money.generated.resources.feature_send_money_to_account
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosGradientBackground
import org.mifospay.core.designsystem.component.MifosLoadingWheel
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.component.MifosTopBar
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.designsystem.theme.toRoundedCornerShape
import org.mifospay.core.network.model.entity.templates.account.AccountOption
import org.mifospay.core.ui.AvatarBox
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.core.ui.SimpleSearchBar
import org.mifospay.core.ui.utils.EventsEffect
import template.core.base.designsystem.theme.KptTheme

@Composable
fun SelectPayeeScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    navigateToMakeTransferV2Screen: (clientId: Long, clientName: String, accountNo: String, amount: Int, accountId: Long) -> Unit,
    viewModel: SelectScreenViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            is SelectScreenEvent.NavigateToTransferScreen -> {
//                navigateToMakeTransferV2Screen(
//                    state.selectedAccount?.parentId ?: 0,
//                    state.selectedAccount?.parentName ?: "",
//                    state.selectedAccount?.entityAccountNo ?: "",
//                    0,
//                    state.selectedAccount?.entityId ?: 0,
//                )
            }

            SelectScreenEvent.NavigateBack -> navigateBack()
        }
    }

    SelectAccountScreen(
        state = state,
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
    modifier: Modifier = Modifier,
    onAction: (SelectScreenAction) -> Unit,
) {
    MifosGradientBackground {
        MifosScaffold(
            modifier = modifier,
            topBar = {
                MifosTopBar(
                    topBarTitle = stringResource(Res.string.feature_select_payment_title),
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
            SelectAccountContent(
                state = state,
                modifier = Modifier.padding(paddingValues),
                onAction = onAction,
            )
        }
    }
}

@Composable
private fun SelectAccountContent(
    state: SelectScreenState,
    onAction: (SelectScreenAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state.state) {
        is SelectScreenState.State.Error -> {
            EmptyContentScreen(
                title = stringResource(Res.string.feature_send_money_oops),
                subTitle = stringResource(Res.string.feature_send_money_something_went_wrong),
                modifier = Modifier.fillMaxSize(),
                iconTint = KptTheme.colorScheme.error,
            )
        }
        SelectScreenState.State.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                MifosLoadingWheel(contentDesc = stringResource(Res.string.feature_send_money_loading))
            }
        }
        SelectScreenState.State.NoAccounts -> {
            EmptyContentScreen(
                title = stringResource(Res.string.feature_send_money_oops),
                subTitle = stringResource(Res.string.feature_send_money_no_accounts_found),
                modifier = Modifier.fillMaxSize(),
            )
        }
        SelectScreenState.State.Success -> {
            LazyColumn(
                modifier = modifier.padding(horizontal = KptTheme.spacing.md),
            ) {
                stickyHeader {
                    SimpleSearchBar(
                        query = state.accountNumber,
                        placeHolder = stringResource(Res.string.feature_select_account_placeholder),
                        onQueryChange = {
                            onAction(SelectScreenAction.AccountNumberChanged(it))
                        },
                        onClearQuery = {
                            onAction(SelectScreenAction.AccountNumberChanged(""))
                        },
                    )
                }
                accountListContent(
                    state = state,
                    onAction = onAction,
                    selected = { state.selectedAccount == it },
                )
                if (state.filteredToAccounts?.isEmpty() == true) {
                    item {
                        EmptyContentScreen(
                            title = stringResource(Res.string.feature_send_money_oops),
                            subTitle = "No Accounts Found for searched query",
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }
        }
    }
}

private fun LazyListScope.accountListContent(
    state: SelectScreenState,
    selected: (AccountOption?) -> Boolean,
    onAction: (SelectScreenAction.SelectAccount) -> Unit,
) {
    items(state.filteredToAccounts?.size ?: 0) { it ->
        AccountCard(
            account = state.filteredToAccounts?.get(it),
            selected = selected,
            onClick = remember(state.filteredToAccounts?.get(it)) {
                { onAction(SelectScreenAction.SelectAccount(it)) }
            },
        )
    }
}

@Composable
private fun AccountCard(
    account: AccountOption?,
    selected: (AccountOption?) -> Boolean,
    modifier: Modifier = Modifier,
    onClick: (AccountOption?) -> Unit,
) {
    ListItem(
        headlineContent = {
            Text(text = account?.clientName ?: "")
        },
        supportingContent = {
            Text(text = account?.accountNo ?: "")
        },
        leadingContent = {
            AvatarBox(
                icon = MifosIcons.Bank,
                backgroundColor = KptTheme.colorScheme.tertiary,
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
                    contentDescription = stringResource(Res.string.feature_send_money_selected),
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
private fun SendMoneyBottomBar(
    showDetails: Boolean,
    selectedAccount: AccountOption?,
    modifier: Modifier = Modifier,
    onClickProceed: () -> Unit,
    onDeselect: () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = KptTheme.shapes.toRoundedCornerShape(
            topStart = KptTheme.spacing.sm,
            topEnd = KptTheme.spacing.sm,
        ),
        color = KptTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KptTheme.spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
        ) {
            AnimatedVisibility(
                visible = showDetails && selectedAccount != null,
                label = stringResource(Res.string.feature_send_money_bottom_bar),
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
                Text(text = stringResource(Res.string.feature_send_money_proceed))
            }
        }
    }
}

@Composable
private fun SelectedAccountCard(
    account: AccountOption,
    modifier: Modifier = Modifier,
    onDeselect: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
    ) {
        Text(
            text = stringResource(Res.string.feature_send_money_to_account),
            style = KptTheme.typography.labelLarge,
        )

        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.outlinedCardColors(
                containerColor = Color.Transparent,
            ),
            border = BorderStroke(1.dp, KptTheme.colorScheme.primary),
        ) {
            ListItem(
                headlineContent = {
                    Text(
                        text = account.clientName ?: "",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                supportingContent = {
                    Text(text = account.accountNo ?: "")
                },
                leadingContent = {
                    AvatarBox(icon = MifosIcons.Bank)
                },
                trailingContent = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(
                            onClick = onDeselect,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(Res.string.feature_send_money_close),
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
