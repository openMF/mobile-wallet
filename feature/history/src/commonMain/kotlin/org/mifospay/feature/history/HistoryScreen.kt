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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mobile_wallet.feature.history.generated.resources.Res
import mobile_wallet.feature.history.generated.resources.feature_history_empty
import mobile_wallet.feature.history.generated.resources.feature_history_error
import mobile_wallet.feature.history.generated.resources.feature_history_error_oops
import mobile_wallet.feature.history.generated.resources.feature_history_loading
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.MifosLoadingWheel
import org.mifospay.core.model.savingsaccount.TransactionType
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.core.ui.utils.EventsEffect
import org.mifospay.feature.history.components.HistoryScreenFilter
import org.mifospay.feature.history.components.TransactionList

@Composable
fun HistoryScreen(
    viewTransferDetail: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = koinViewModel(),
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
    )
}

@Composable
internal fun HistoryScreenContent(
    state: HistoryState,
    modifier: Modifier = Modifier,
    onAction: (HistoryAction) -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when (state.viewState) {
            is HistoryState.ViewState.Loading -> {
                MifosLoadingWheel(
                    modifier = Modifier.align(Alignment.Center),
                    contentDesc = stringResource(Res.string.feature_history_loading),
                )
            }

            is HistoryState.ViewState.Error -> {
                EmptyContentScreen(
                    title = stringResource(Res.string.feature_history_error_oops),
                    subTitle = stringResource(Res.string.feature_history_error),
                    modifier = Modifier.align(Alignment.Center),
                    iconTint = MaterialTheme.colorScheme.primary,
                )
            }

            is HistoryState.ViewState.Empty -> {
                EmptyContentScreen(
                    title = stringResource(Res.string.feature_history_error_oops),
                    subTitle = stringResource(Res.string.feature_history_empty),
                    modifier = Modifier.fillMaxSize().align(Alignment.Center),
                    iconTint = MaterialTheme.colorScheme.primary,
                )
            }

            is HistoryState.ViewState.Content -> {
                HistoryScreenContent(
                    state = state.viewState,
                    selectedTransactionType = state.transactionType,
                    onAction = onAction,
                )
            }
        }
    }
}

@Composable
private fun HistoryScreenContent(
    state: HistoryState.ViewState.Content,
    selectedTransactionType: TransactionType,
    modifier: Modifier = Modifier,
    onAction: (HistoryAction) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        HistoryScreenFilter(
            selectedTransactionType = selectedTransactionType,
            onAction = onAction,
            modifier = Modifier.padding(top = 8.dp),
        )

        TransactionList(
            transactions = state.list,
            onAction = onAction,
        )
    }
}
