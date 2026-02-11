/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.qr

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.launch
import mobile_wallet.feature.qr.generated.resources.Res
import mobile_wallet.feature.qr.generated.resources.feature_qr_no_qr_found
import mobile_wallet.feature.qr.generated.resources.feature_qr_scan_success
import mobile_wallet.feature.qr.generated.resources.feature_qr_torch
import mobile_wallet.feature.qr.generated.resources.feature_qr_upload_qr
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.feature.qr.components.QrActionButton
import org.mifospay.feature.qr.components.QrHelpDialog
import org.mifospay.feature.qr.components.QrPermissionDenied
import org.mifospay.feature.qr.components.QrProcessingOverlay
import org.mifospay.feature.qr.components.QrScanFooter
import org.mifospay.feature.qr.components.QrScanTopBar
import org.mifospay.feature.qr.components.QrViewfinder

@Composable
internal fun ScanQrCodeScreen(
    navigateBack: () -> Unit,
    navigateToSendScreen: (String) -> Unit,
    navigateToAddBeneficiaryScreen: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ScanQrViewModel = koinViewModel(),
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val eventFlow by viewModel.eventFlow.collectAsStateWithLifecycle(null)
    val isTorchEnabled by viewModel.isTorchEnabled.collectAsStateWithLifecycle()
    val showHelpDialog by viewModel.showHelpDialog.collectAsStateWithLifecycle()
    val isProcessingImage by viewModel.isProcessingImage.collectAsStateWithLifecycle()
    val selectedImageBytes by viewModel.selectedImageBytes.collectAsStateWithLifecycle()

    val scanSuccessMessage = stringResource(Res.string.feature_qr_scan_success)
    val noQrFoundMessage = stringResource(Res.string.feature_qr_no_qr_found)

    LaunchedEffect(key1 = eventFlow) {
        when (eventFlow) {
            is ScanQrEvent.OnNavigateToSendScreen -> {
                navigateToSendScreen.invoke((eventFlow as ScanQrEvent.OnNavigateToSendScreen).data)
            }

            is ScanQrEvent.ShowToast -> {
                scope.launch {
                    snackbarHostState.showSnackbar((eventFlow as ScanQrEvent.ShowToast).message)
                }
            }

            is ScanQrEvent.OnNavigateToAddBeneficiary -> navigateToAddBeneficiaryScreen.invoke(
                (eventFlow as ScanQrEvent.OnNavigateToAddBeneficiary).beneficiary,
            )

            is ScanQrEvent.OnScanSuccess -> {
                scope.launch {
                    snackbarHostState.showSnackbar(scanSuccessMessage)
                }
            }

            is ScanQrEvent.OnNoQrFound -> {
                scope.launch {
                    snackbarHostState.showSnackbar(noQrFoundMessage)
                }
            }

            null -> Unit
        }
    }

    ScanQrCodeScreenContent(
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        isTorchEnabled = isTorchEnabled,
        showHelpDialog = showHelpDialog,
        isProcessingImage = isProcessingImage,
        selectedImageBytes = selectedImageBytes,
        onScanned = viewModel::onScanned,
        onImageQrScanned = viewModel::onImageQrScanned,
        onSetProcessingImage = viewModel::setProcessingImage,
        onSetSelectedImageBytes = viewModel::setSelectedImageBytes,
        onToggleTorch = viewModel::toggleTorch,
        onShowHelpDialog = viewModel::showHelpDialog,
        onDismissHelpDialog = viewModel::hideHelpDialog,
        navigateBack = navigateBack,
    )
}

@Composable
fun ScanQrCodeScreenContent(
    snackbarHostState: SnackbarHostState,
    isTorchEnabled: Boolean,
    showHelpDialog: Boolean,
    isProcessingImage: Boolean,
    selectedImageBytes: ByteArray?,
    onScanned: (String) -> Boolean,
    onImageQrScanned: (String?) -> Unit,
    onSetProcessingImage: (Boolean) -> Unit,
    onSetSelectedImageBytes: (ByteArray?) -> Unit,
    onToggleTorch: () -> Unit,
    onShowHelpDialog: () -> Unit,
    onDismissHelpDialog: () -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isTorchAvailable by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // FileKit image picker launcher
    val imagePicker = rememberFilePickerLauncher(
        type = FileKitType.Image,
    ) { file ->
        if (file != null) {
            scope.launch {
                onSetProcessingImage(true)
                val imageBytes = file.readBytes()
                onSetSelectedImageBytes(imageBytes)
                val qrData = decodeQrFromFile(file)
                onImageQrScanned(qrData)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        // Camera preview (full screen)
        QrScannerWithPermissions(
            types = listOf(CodeType.QR),
            modifier = Modifier.fillMaxSize(),
            isTorchEnabled = isTorchEnabled,
            onTorchAvailabilityChanged = { available ->
                isTorchAvailable = available
            },
            onScanned = onScanned,
            onUploadQr = { imagePicker.launch() },
        )

        // Viewfinder overlay with animation
        QrViewfinder(
            modifier = Modifier.fillMaxSize(),
        )

        // Top bar (top aligned)
        QrScanTopBar(
            onCloseClick = navigateBack,
            onHelpClick = onShowHelpDialog,
            modifier = Modifier.align(Alignment.TopCenter),
        )

        // Action buttons and footer (bottom aligned)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(48.dp),
            ) {
                QrActionButton(
                    icon = MifosIcons.PhotoLibrary,
                    label = stringResource(Res.string.feature_qr_upload_qr),
                    onClick = { imagePicker.launch() },
                )

                QrActionButton(
                    icon = if (isTorchEnabled) MifosIcons.FlashOn else MifosIcons.FlashOff,
                    label = stringResource(Res.string.feature_qr_torch),
                    onClick = onToggleTorch,
                    enabled = isTorchAvailable,
                )
            }

            QrScanFooter()
        }

        // Processing overlay
        if (isProcessingImage) {
            QrProcessingOverlay(
                imageBytes = selectedImageBytes,
                modifier = Modifier.fillMaxSize(),
            )
        }

        // Snackbar host
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 120.dp),
        )

        // Help dialog
        if (showHelpDialog) {
            QrHelpDialog(
                onDismiss = onDismissHelpDialog,
            )
        }
    }
}
