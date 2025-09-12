/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.history

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mobile_wallet.feature.history.generated.resources.Res
import mobile_wallet.feature.history.generated.resources.feature_history_empty
import mobile_wallet.feature.history.generated.resources.feature_history_empty_filter
import mobile_wallet.feature.history.generated.resources.feature_history_error
import mobile_wallet.feature.history.generated.resources.feature_history_error_oops
import mobile_wallet.feature.history.generated.resources.feature_history_filter_apply
import mobile_wallet.feature.history.generated.resources.feature_history_filter_by_account
import mobile_wallet.feature.history.generated.resources.feature_history_filter_clear_all
import mobile_wallet.feature.history.generated.resources.feature_history_filter_content_desc
import mobile_wallet.feature.history.generated.resources.feature_history_filter_title
import mobile_wallet.feature.history.generated.resources.feature_history_filter_transaction_type
import mobile_wallet.feature.history.generated.resources.feature_history_header_account
import mobile_wallet.feature.history.generated.resources.feature_history_header_all
import mobile_wallet.feature.history.generated.resources.feature_history_header_credit
import mobile_wallet.feature.history.generated.resources.feature_history_header_debit
import mobile_wallet.feature.history.generated.resources.feature_history_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.MifosBottomSheet
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.component.MifosTopBar
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.model.savingsaccount.TransactionType
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.core.ui.MifosProgressIndicator
import org.mifospay.core.ui.utils.EventsEffect
import org.mifospay.feature.history.components.FilterAccountDropDown
import org.mifospay.feature.history.components.HistoryScreenFilter
import org.mifospay.feature.history.components.TransactionList
import template.core.base.designsystem.theme.KptTheme

@Composable
fun HistoryScreen(
    viewTransferDetail: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = koinViewModel(),
    showTopBar: Boolean = true,
    onBackClick: (() -> Unit)? = null,
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            is HistoryEvent.OnTransactionDetail -> {
                viewTransferDetail.invoke(event.transferId)
            }
        }
    }

    HistoryScreenContent(
        modifier = modifier,
        state = state,
        onAction = remember(viewModel) {
            { action -> viewModel.trySendAction(action) }
        },
        showTopBar = showTopBar,
        onBackClick = onBackClick,
    )
}

@Composable
internal fun HistoryScreenContent(
    state: HistoryState,
    modifier: Modifier = Modifier,
    onAction: (HistoryAction) -> Unit,
    showTopBar: Boolean = true,
    onBackClick: (() -> Unit)? = null,
) {
    MifosScaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            if (showTopBar && onBackClick != null) {
                MifosTopBar(
                    topBarTitle = stringResource(Res.string.feature_history_title),
                    backPress = onBackClick,
                )
            }
        },
    ) { paddingValues ->
        when (state.viewState) {
            is HistoryState.ViewState.Loading -> MifosProgressIndicator()

            is HistoryState.ViewState.Error -> {
                EmptyContentScreen(
                    title = stringResource(Res.string.feature_history_error_oops),
                    subTitle = stringResource(Res.string.feature_history_error),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    iconTint = KptTheme.colorScheme.error,
                )
            }

            is HistoryState.ViewState.Empty -> {
                EmptyContentScreen(
                    title = stringResource(Res.string.feature_history_error_oops),
                    subTitle = stringResource(Res.string.feature_history_empty),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                )
            }

            is HistoryState.ViewState.Content -> {
                Column(
                    Modifier.fillMaxWidth().padding(paddingValues),
                ) {
                    HistoryScreenHeader(
                        accountNo = state.selectedAccount?.number ?: "",
                        selectedTransactionType = state.transactionType,
                        onFilterClick = {
                            onAction(HistoryAction.OnFilterClick)
                        },
                    )
                    if (state.filteredEmpty) {
                        EmptyContentScreen(
                            title = stringResource(Res.string.feature_history_error_oops),
                            subTitle = stringResource(Res.string.feature_history_empty_filter),
                            modifier = Modifier
                                .fillMaxSize(),
                        )
                    } else {
                        TransactionList(
                            transactions = state.viewState.list,
                            onAction = onAction,
                            modifier = modifier,
                        )
                    }
                }
                if (state.showFilter) {
                    HistoryScreenBottomSheet(
                        onAction = onAction,
                        state = state,
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryScreenHeader(
    accountNo: String,
    selectedTransactionType: TransactionType,
    modifier: Modifier = Modifier,
    onFilterClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(KptTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            Modifier.weight(1f),
        ) {
            Text(
                text = stringResource(Res.string.feature_history_header_account, accountNo),
                style = KptTheme.typography.titleSmall,
            )
            Spacer(Modifier.height(KptTheme.spacing.xs))
            Text(
                text = when (selectedTransactionType) {
                    TransactionType.OTHER -> stringResource(Res.string.feature_history_header_all)
                    TransactionType.DEBIT -> stringResource(Res.string.feature_history_header_debit)
                    TransactionType.CREDIT -> stringResource(Res.string.feature_history_header_credit)
                },
            )
        }
        IconButton(
            onClick = onFilterClick,
            modifier = modifier
                .size(40.dp)
                .border(
                    width = 1.dp,
                    color = KptTheme.colorScheme.outline,
                    shape = RoundedCornerShape(8.dp),
                )
                .clip(RoundedCornerShape(8.dp)),
        ) {
            Icon(
                imageVector = MifosIcons.Filter,
                contentDescription = stringResource(Res.string.feature_history_filter_content_desc),
                tint = KptTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
fun HistoryScreenBottomSheet(
    modifier: Modifier = Modifier,
    onAction: (HistoryAction) -> Unit,
    state: HistoryState,
) {
    MifosBottomSheet(
        modifier = modifier,
        onDismiss = {
            onAction(HistoryAction.OnFilterClick)
        },
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(KptTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(Res.string.feature_history_filter_title),
                    style = KptTheme.typography.titleMedium,
                    modifier = Modifier.padding(end = KptTheme.spacing.md),
                )
                Text(
                    text = stringResource(Res.string.feature_history_filter_clear_all),
                    color = KptTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        onAction(HistoryAction.SetFilter(TransactionType.OTHER))
                        onAction(HistoryAction.OnFilterClick)
                    },
                )
            }
            Text(
                text = stringResource(Res.string.feature_history_filter_by_account),
                style = KptTheme.typography.titleSmall,
            )
            FilterAccountDropDown(
                selectedAccount = state.currentSelectedAccount,
                accounts = state.accounts,
                onAccountSelected = {
                    onAction(HistoryAction.SetSelectedAccount(it))
                },
            )
            Text(
                text = stringResource(Res.string.feature_history_filter_transaction_type),
                style = KptTheme.typography.titleSmall,
            )
            HistoryScreenFilter(
                selectedTransactionType = state.currentSelectedTransactionType,
                onAction = onAction,
                modifier = modifier.padding(top = KptTheme.spacing.sm),
            )

            Spacer(Modifier.height(KptTheme.spacing.lg))
            MifosButton(
                onClick = {
                    onAction(HistoryAction.OnApplyFilterClick)
                },
                text = {
                    Text(text = stringResource(Res.string.feature_history_filter_apply))
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
