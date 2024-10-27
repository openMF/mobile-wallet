/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.receipt

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import mobile_wallet.feature.receipt.generated.resources.Res
import mobile_wallet.feature.receipt.generated.resources.feature_receipt_loading
import mobile_wallet.feature.receipt.generated.resources.feature_receipt_receipt
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.MifosLoadingWheel
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.ui.EmptyContentScreen

@Composable
internal fun ReceiptScreenRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReceiptViewModel = koinViewModel(),
) {
    val receiptUiState by viewModel.receiptUiState.collectAsState()

    ReceiptScreen(
        uiState = receiptUiState,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Composable
@VisibleForTesting
internal fun ReceiptScreen(
    uiState: ReceiptUiState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosScaffold(
        backPress = onBackClick,
        topBarTitle = stringResource(Res.string.feature_receipt_receipt),
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(it),
        ) {
            when (uiState) {
                is ReceiptUiState.Loading -> {
                    MifosLoadingWheel(
                        contentDesc = stringResource(Res.string.feature_receipt_loading),
                    )
                }

                is ReceiptUiState.Error -> {
                    EmptyContentScreen(
                        title = "Oops!",
                        subTitle = uiState.message,
                    )
                }

                is ReceiptUiState.Success -> {
                    EmptyContentScreen(
                        title = "Oops!",
                        subTitle = "Not implemented yet",
                    )
                }
            }
        }
    }
}
