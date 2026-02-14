/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.mpay.qr

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.alexzhirkevich.qrose.ImageFormat
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import io.github.alexzhirkevich.qrose.toByteArray
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.LoadingDialogState
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosLoadingDialog
import org.mifospay.core.designsystem.component.MifosOutlinedButton
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.ui.MifosProgressIndicator
import org.mifospay.core.ui.utils.EventsEffect
import template.core.base.designsystem.theme.KptTheme

@Composable
internal fun MpayQrScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MpayQrViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val currencyList by viewModel.currencyList.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            MpayQrEvent.OnNavigateBack -> navigateBack.invoke()
        }
    }

    MpayQrDialogs(
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

    MpayQrScreen(
        state = state,
        modifier = modifier,
        onAction = remember(viewModel) {
            { viewModel.trySendAction(it) }
        },
    )
}

@Composable
private fun MpayQrDialogs(
    dialogState: MpayQrState.DialogState?,
    showAmountDialog: @Composable () -> Unit,
) {
    when (dialogState) {
        is MpayQrState.DialogState.ShowSetAmountDialog -> showAmountDialog.invoke()

        is MpayQrState.DialogState.Loading -> MifosLoadingDialog(
            visibilityState = LoadingDialogState.Shown,
        )

        null -> Unit
    }
}

@Composable
internal fun MpayQrScreen(
    state: MpayQrState,
    modifier: Modifier = Modifier,
    onAction: (MpayQrAction) -> Unit,
) {
    MifosScaffold(
        modifier = modifier,
        topBarTitle = "Request Money",
        backPress = {
            onAction(MpayQrAction.NavigateBack)
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center,
        ) {
            when (state.viewState) {
                is MpayQrState.ViewState.Loading -> MifosProgressIndicator()

                is MpayQrState.ViewState.Error -> {
                    MpayQrErrorContent(
                        message = state.viewState.message,
                        onNavigateBack = { onAction(MpayQrAction.NavigateBack) },
                    )
                }

                is MpayQrState.ViewState.Content -> {
                    MpayQrScreenContent(
                        state = state.viewState,
                        selectedPage = state.selectedPage,
                        modifier = Modifier,
                        onAction = onAction,
                    )
                }
            }
        }
    }
}

@Composable
private fun MpayQrScreenContent(
    state: MpayQrState.ViewState.Content,
    selectedPage: Int,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    onAction: (MpayQrAction) -> Unit,
) {
    val pagerState = rememberPagerState(
        initialPage = selectedPage,
        pageCount = { 2 },
    )

    // Sync pager state with viewmodel
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onAction(MpayQrAction.PageChanged(page))
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = lazyListState,
        contentPadding = PaddingValues(KptTheme.spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
    ) {
        item {
            // Page title
            Text(
                text = if (pagerState.currentPage == 0) "Intra-bank QR" else "Inter-bank QR",
                style = KptTheme.typography.titleMedium,
                color = KptTheme.colorScheme.onSurface,
            )
        }

        item {
            // Swipeable QR codes
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
                key = { it },
            ) { page ->
                val data = if (page == 0) state.intraBankData else state.interBankData
                QrDataContent(
                    data = data,
                    options = state.options,
                )
            }
        }

        item {
            // Page indicator dots
            HorizontalPagerIndicator(
                pageCount = 2,
                currentPage = pagerState.currentPage,
                modifier = Modifier.padding(vertical = KptTheme.spacing.sm),
            )
        }

        item {
            MifosButton(
                onClick = {
                    onAction(MpayQrAction.ShowSetAmountDialog)
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Set Amount")
            }
        }

        item {
            val currentData = if (pagerState.currentPage == 0) {
                state.intraBankData
            } else {
                state.interBankData
            }
            val sharePainter = rememberQrCodePainter(
                data = currentData,
                options = state.options,
            )

            MifosOutlinedButton(
                onClick = {
                    val bytes = sharePainter.toByteArray(1024, 1024, ImageFormat.PNG)
                    onAction(MpayQrAction.ShareQrCode(bytes))
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Share")
            }
        }
    }
}

@Composable
private fun MpayQrErrorContent(
    message: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(KptTheme.spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        item {
            Text(
                text = "Unable to Generate QR Code",
                style = KptTheme.typography.titleMedium,
                color = KptTheme.colorScheme.error,
            )
        }

        item {
            Text(
                text = message,
                style = KptTheme.typography.bodyMedium,
                color = KptTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = KptTheme.spacing.md),
            )
        }

        item {
            MifosOutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Go Back")
            }
        }
    }
}

@Composable
private fun HorizontalPagerIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .size(if (index == currentPage) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (index == currentPage) {
                            KptTheme.colorScheme.primary
                        } else {
                            KptTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        },
                    ),
            )
        }
    }
}

@Composable
private fun QrDataContent(
    data: String,
    options: io.github.alexzhirkevich.qrose.options.QrOptions,
    modifier: Modifier = Modifier,
) {
    val painter = rememberQrCodePainter(
        data = data,
        options = options,
    )

    Box(
        modifier = modifier
            .size(300.dp)
            .background(Color.White, shape = KptTheme.shapes.large),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.size(260.dp),
        )
    }
}
