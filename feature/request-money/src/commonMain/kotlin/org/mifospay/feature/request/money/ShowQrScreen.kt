/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.request.money

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.alexzhirkevich.qrose.ImageFormat
import io.github.alexzhirkevich.qrose.QrCodePainter
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import io.github.alexzhirkevich.qrose.toByteArray
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.LoadingDialogState
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosLoadingDialog
import org.mifospay.core.designsystem.component.MifosLoadingWheel
import org.mifospay.core.designsystem.component.MifosOutlinedButton
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.ui.utils.EventsEffect

@Composable
internal fun ShowQrScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ShowQrViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val currencyList by viewModel.currencyList.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            ShowQrEvent.OnNavigateBack -> navigateBack.invoke()
        }
    }

    ShowQRDialogs(
        dialogState = state.dialogState,
        showAmountDialog = {
            SetAmountDialog(
                amount = state.qrData.amount,
                currency = state.qrData.currency,
                currencyList = currencyList,
                onAction = viewModel::trySendAction,
            )
        },
    )

    ShowQrScreen(
        state = state,
        modifier = modifier,
        onAction = remember(viewModel) {
            { viewModel.trySendAction(it) }
        },
    )
}

@Composable
private fun ShowQRDialogs(
    dialogState: ShowQrState.DialogState?,
    showAmountDialog: @Composable () -> Unit,
) {
    when (dialogState) {
        is ShowQrState.DialogState.ShowSetAmountDialog -> showAmountDialog.invoke()

        is ShowQrState.DialogState.Loading -> MifosLoadingDialog(
            visibilityState = LoadingDialogState.Shown,
        )

        null -> Unit
    }
}

@Composable
internal fun ShowQrScreen(
    state: ShowQrState,
    modifier: Modifier = Modifier,
    onAction: (ShowQrAction) -> Unit,
) {
    MifosScaffold(
        modifier = modifier,
        topBarTitle = "Request Money",
        backPress = {
            onAction(ShowQrAction.NavigateBack)
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center,
        ) {
            when (state.viewState) {
                is ShowQrState.ViewState.Loading -> {
                    MifosLoadingWheel(contentDesc = "Loading")
                }

                is ShowQrState.ViewState.Content -> {
                    ShowQrScreenContent(
                        state = state.viewState,
                        modifier = Modifier,
                        onAction = onAction,
                    )
                }
            }
        }
    }
}

@Composable
private fun ShowQrScreenContent(
    state: ShowQrState.ViewState.Content,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    onAction: (ShowQrAction) -> Unit,
) {
    val painter = rememberQrCodePainter(
        data = state.data,
        options = state.options,
    )

    val bytes: ByteArray = remember(painter) {
        painter.toByteArray(1024, 1024, ImageFormat.PNG)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        state = lazyListState,
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            QrDataContent(
                painter = painter,
                modifier = Modifier,
            )
        }

        item {
            MifosButton(
                onClick = {
                    onAction(ShowQrAction.ShowSetAmountDialog)
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Set Amount")
            }
        }

        item {
            MifosOutlinedButton(
                onClick = {
                    onAction(ShowQrAction.ShareQrCode(bytes))
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Share")
            }
        }
    }
}

@Composable
private fun QrDataContent(
    painter: QrCodePainter,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(350.dp, 381.dp)
            .background(Color.White, shape = RoundedCornerShape(15.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
        ) {
            Text(
                text = "Mifos Pay",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )

            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .padding(bottom = 45.dp)
                    .size(260.dp),
            )
        }
    }
}
