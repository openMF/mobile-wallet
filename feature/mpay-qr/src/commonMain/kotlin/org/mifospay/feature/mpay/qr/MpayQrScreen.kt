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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.alexzhirkevich.qrose.ImageFormat
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import io.github.alexzhirkevich.qrose.toByteArray
import mobile_wallet.feature.mpay_qr.generated.resources.Res
import mobile_wallet.feature.mpay_qr.generated.resources.feature_mpay_qr_copied
import mobile_wallet.feature.mpay_qr.generated.resources.feature_mpay_qr_downloaded
import mobile_wallet.feature.mpay_qr.generated.resources.feature_mpay_qr_go_back
import mobile_wallet.feature.mpay_qr.generated.resources.feature_mpay_qr_receive_money
import mobile_wallet.feature.mpay_qr.generated.resources.feature_mpay_qr_scan_to_pay
import mobile_wallet.feature.mpay_qr.generated.resources.feature_mpay_qr_unable_to_generate
import mobile_wallet.feature.mpay_qr.generated.resources.feature_request_money_set_amount
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.LoadingDialogState
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosLoadingDialog
import org.mifospay.core.designsystem.component.MifosOutlinedButton
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.model.account.DefaultAccount
import org.mifospay.core.model.client.Client
import org.mifospay.core.ui.MifosProgressIndicator
import org.mifospay.core.ui.utils.EventsEffect
import org.mifospay.feature.mpay.qr.components.AccountIdSection
import org.mifospay.feature.mpay.qr.components.AccountSelectorCard
import org.mifospay.feature.mpay.qr.components.QrActionButtons
import org.mifospay.feature.mpay.qr.components.QrCodeCard
import org.mifospay.feature.mpay.qr.components.QrType
import template.core.base.designsystem.KptMaterialTheme
import template.core.base.designsystem.theme.KptTheme

@Composable
internal fun MpayQrScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MpayQrViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val currencyList by viewModel.currencyList.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val clipboardManager = LocalClipboardManager.current

    val copiedMessage = stringResource(Res.string.feature_mpay_qr_copied)
    val downloadedMessage = stringResource(Res.string.feature_mpay_qr_downloaded)

    EventsEffect(viewModel) { event ->
        when (event) {
            MpayQrEvent.OnNavigateBack -> navigateBack.invoke()
            MpayQrEvent.QrDownloaded -> snackbarHostState.showSnackbar(downloadedMessage)
            is MpayQrEvent.ShowSnackbar -> {
                clipboardManager.setText(AnnotatedString(event.message))
                snackbarHostState.showSnackbar(copiedMessage)
            }
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
        snackbarHostState = snackbarHostState,
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
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onAction: (MpayQrAction) -> Unit,
) {
    MifosScaffold(
        modifier = modifier,
        topBarTitle = stringResource(Res.string.feature_mpay_qr_receive_money),
        backPress = {
            onAction(MpayQrAction.NavigateBack)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) {
        AnimatedContent(
            targetState = state.viewState,
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith
                    fadeOut(animationSpec = tween(300))
            },
            contentAlignment = Alignment.Center,
            label = "QrScreenContent",
        ) { viewState ->
            when (viewState) {
                is MpayQrState.ViewState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        MifosProgressIndicator()
                    }
                }

                is MpayQrState.ViewState.Error -> {
                    MpayQrErrorContent(
                        message = viewState.message,
                        onNavigateBack = { onAction(MpayQrAction.NavigateBack) },
                    )
                }

                is MpayQrState.ViewState.Content -> {
                    MpayQrScreenContent(
                        state = state,
                        contentState = viewState,
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
    state: MpayQrState,
    contentState: MpayQrState.ViewState.Content,
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
        // Header text
        item {
            Text(
                text = stringResource(Res.string.feature_mpay_qr_scan_to_pay),
                style = KptTheme.typography.bodyMedium,
                color = KptTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        // Account selector card
        item {
            AccountSelectorCard(
                client = state.client,
                account = state.defaultAccount,
                isPrimary = true,
            )
        }

        // Swipeable QR codes with QrCodeCard component
        item {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
                key = { it },
            ) { page ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    val data = if (page == 0) contentState.intraBankData else contentState.interBankData
                    val qrType = if (page == 0) QrType.INTRA_BANK else QrType.INTER_BANK

                    QrCodeCard(
                        data = data,
                        options = contentState.options,
                        qrType = qrType,
                        modifier = Modifier.padding(horizontal = KptTheme.spacing.sm),
                    )
                }
            }
        }

        // Page indicator dots
        item {
            HorizontalPagerIndicator(
                pageCount = 2,
                currentPage = pagerState.currentPage,
                modifier = Modifier.padding(vertical = KptTheme.spacing.sm),
            )
        }

        // Set Amount button
        item {
            MifosButton(
                onClick = {
                    onAction(MpayQrAction.ShowSetAmountDialog)
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(Res.string.feature_request_money_set_amount))
            }
        }

        // Share and Download buttons
        item {
            val currentData = if (pagerState.currentPage == 0) {
                contentState.intraBankData
            } else {
                contentState.interBankData
            }
            val qrPainter = rememberQrCodePainter(
                data = currentData,
                options = contentState.options,
            )

            QrActionButtons(
                onShareClick = {
                    val bytes = qrPainter.toByteArray(1024, 1024, ImageFormat.PNG)
                    onAction(MpayQrAction.ShareQrCode(bytes))
                },
                onDownloadClick = {
                    val bytes = qrPainter.toByteArray(1024, 1024, ImageFormat.PNG)
                    onAction(MpayQrAction.DownloadQrCode(bytes))
                },
            )
        }

        // Account IDs section
        item {
            AccountIdSection(
                accountNumber = state.defaultAccount.accountNo,
                externalId = state.accountExternalId.ifBlank { null },
                onCopyAccountNumber = {
                    onAction(MpayQrAction.CopyToClipboard(state.defaultAccount.accountNo))
                },
                onCopyExternalId = {
                    onAction(MpayQrAction.CopyToClipboard(state.accountExternalId))
                },
            )
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
                text = stringResource(Res.string.feature_mpay_qr_unable_to_generate),
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = KptTheme.spacing.md),
            ) {
                Text(text = stringResource(Res.string.feature_mpay_qr_go_back))
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
        horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .size(
                        if (index == currentPage) {
                            KptTheme.spacing.sm + KptTheme.spacing.xs / 2
                        } else {
                            KptTheme.spacing.sm
                        },
                    )
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

@Preview
@Composable
private fun MpayQrScreenLoadingPreview() {
    KptMaterialTheme {
        MpayQrScreen(
            state = MpayQrState(
                client = Client(
                    id = 1,
                    accountNo = "000000001",
                    externalId = "",
                    active = true,
                    activationDate = emptyList(),
                    firstname = "John",
                    lastname = "Doe",
                    displayName = "John Doe",
                    mobileNo = "",
                    emailAddress = "",
                    dateOfBirth = emptyList(),
                    isStaff = false,
                    officeId = 1,
                    officeName = "Head Office",
                    savingsProductName = "",
                ),
                defaultAccount = DefaultAccount(
                    accountId = 1,
                    accountNo = "000000001",
                ),
                viewState = MpayQrState.ViewState.Loading,
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onAction = {},
        )
    }
}

@Preview
@Composable
private fun MpayQrScreenErrorPreview() {
    KptMaterialTheme {
        MpayQrErrorContent(
            message = "No default account set",
            onNavigateBack = {},
        )
    }
}

@Preview
@Composable
private fun HorizontalPagerIndicatorPreview() {
    KptMaterialTheme {
        HorizontalPagerIndicator(
            pageCount = 3,
            currentPage = 1,
        )
    }
}
