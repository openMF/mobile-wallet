/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.history.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mobile_wallet.feature.history.generated.resources.Res
import mobile_wallet.feature.history.generated.resources.feature_history_error
import mobile_wallet.feature.history.generated.resources.feature_history_error_oops
import mobile_wallet.feature.history.generated.resources.feature_history_loading
import mobile_wallet.feature.history.generated.resources.feature_history_specific_transactions_history
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.MfOverlayLoadingWheel
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.ui.ErrorScreenContent
import org.mifospay.core.ui.utils.EventsEffect
import org.mifospay.feature.history.components.TransactionDetail
import org.mifospay.feature.history.components.TransactionItem

@Composable
internal fun SpecificTransactionsScreen(
    navigateBack: () -> Unit,
    viewTransaction: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SpecificTransactionsViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            is STEvent.OnNavigateBack -> navigateBack.invoke()
            is STEvent.OnViewTransaction -> viewTransaction.invoke(event.transferId)
        }
    }

    SpecificTransactionsScreenContent(
        state = state.viewState,
        onAction = viewModel::trySendAction,
        modifier = modifier,
    )
}

@Composable
internal fun SpecificTransactionsScreenContent(
    state: STState.ViewState,
    onAction: (STAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosScaffold(
        modifier = modifier,
        topBarTitle = stringResource(Res.string.feature_history_specific_transactions_history),
        backPress = {
            onAction(STAction.NavigateBack)
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center,
        ) {
            when (state) {
                is STState.ViewState.Loading -> {
                    MfOverlayLoadingWheel(
                        modifier = Modifier.align(Alignment.Center),
                        contentDesc = stringResource(Res.string.feature_history_loading),
                    )
                }

                is STState.ViewState.Error -> {
                    ErrorScreenContent(
                        modifier = Modifier.align(Alignment.Center),
                        title = stringResource(Res.string.feature_history_error_oops),
                        subTitle = stringResource(Res.string.feature_history_error),
                    )
                }

                is STState.ViewState.Content -> {
                    TransactionDetails(
                        state = state,
                        onAction = onAction,
                        modifier = Modifier,
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionDetails(
    state: STState.ViewState.Content,
    onAction: (STAction.ViewTransaction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TransactionItem(
            transaction = state.transaction,
            modifier = Modifier,
            onClick = {
                onAction(STAction.ViewTransaction(it))
            },
        )

        state.detail?.let {
            TransactionDetail(
                detail = it,
            )
        }
    }
}
