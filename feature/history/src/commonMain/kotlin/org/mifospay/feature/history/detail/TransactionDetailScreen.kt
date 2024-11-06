/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.history.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mobile_wallet.feature.history.generated.resources.Res
import mobile_wallet.feature.history.generated.resources.feature_history_error
import mobile_wallet.feature.history.generated.resources.feature_history_error_oops
import mobile_wallet.feature.history.generated.resources.feature_history_loading
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.MifosLoadingWheel
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.core.ui.utils.EventsEffect
import org.mifospay.feature.history.components.TransactionDetail

@Composable
internal fun TransactionDetailScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TransactionDetailViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            TransactionDetailEvent.OnNavigateBack -> onNavigateBack.invoke()
            TransactionDetailEvent.OnShareTransaction -> {
                // TODO:: Configure Share Image Functionality
            }
        }
    }

    TransactionDetailScreenContent(
        state = state.viewState,
        modifier = modifier,
        onAction = remember(viewModel) {
            { action -> viewModel.trySendAction(action) }
        },
    )
}

@Composable
internal fun TransactionDetailScreenContent(
    state: TransactionDetailState.ViewState,
    modifier: Modifier = Modifier,
    onAction: (TransactionDetailAction) -> Unit,
) {
    MifosScaffold(
        backPress = { onAction(TransactionDetailAction.NavigateBack) },
        topBarTitle = "Transaction Details",
        actions = {
            IconButton(
                onClick = {
                    onAction(TransactionDetailAction.ShareTransaction)
                },
            ) {
                Icon(
                    imageVector = MifosIcons.OutlinedShare,
                    contentDescription = "Share",
                )
            }
        },
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center,
        ) {
            when (state) {
                is TransactionDetailState.ViewState.Loading -> {
                    MifosLoadingWheel(
                        modifier = Modifier.align(Alignment.Center),
                        contentDesc = stringResource(Res.string.feature_history_loading),
                    )
                }

                is TransactionDetailState.ViewState.Error -> {
                    EmptyContentScreen(
                        title = stringResource(Res.string.feature_history_error_oops),
                        subTitle = stringResource(Res.string.feature_history_error),
                        modifier = Modifier.align(Alignment.Center),
                        iconTint = MaterialTheme.colorScheme.primary,
                    )
                }

                is TransactionDetailState.ViewState.Content -> {
                    TransactionDetailScreenContent(
                        state = state,
                        modifier = Modifier,
                    )
                }
            }
        }
    }
}

@Composable
internal fun TransactionDetailScreenContent(
    state: TransactionDetailState.ViewState.Content,
    modifier: Modifier = Modifier,
) {
    TransactionDetail(
        detail = state.transaction,
        modifier = modifier,
    )
}
