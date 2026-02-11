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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import org.mifospay.feature.qr.components.QrPermissionDenied

@Composable
expect fun QrCodeScanner(
    types: List<CodeType>,
    modifier: Modifier = Modifier,
    isTorchEnabled: Boolean = false,
    onTorchAvailabilityChanged: (Boolean) -> Unit = {},
    onScanned: (String) -> Boolean,
)

@Composable
fun QrScannerWithPermissions(
    types: List<CodeType>,
    modifier: Modifier = Modifier,
    isTorchEnabled: Boolean = false,
    onTorchAvailabilityChanged: (Boolean) -> Unit = {},
    onScanned: (String) -> Boolean,
    onUploadQr: () -> Unit = {},
    permissionDeniedContent: @Composable (CameraPermissionState, () -> Unit) -> Unit =
        { permissionState, uploadQr ->
            QrPermissionDenied(
                onOpenSettings = permissionState::goToSettings,
                onUploadQr = uploadQr,
                modifier = modifier,
            )
        },
) {
    val permissionState = rememberCameraPermissionState()

    LaunchedEffect(Unit) {
        if (permissionState.status == CameraPermissionStatus.Denied) {
            permissionState.requestCameraPermission()
        }
    }

    if (permissionState.status == CameraPermissionStatus.Granted) {
        QrCodeScanner(
            types = types,
            modifier = modifier.fillMaxSize(),
            isTorchEnabled = isTorchEnabled,
            onTorchAvailabilityChanged = onTorchAvailabilityChanged,
            onScanned = onScanned,
        )
    } else {
        permissionDeniedContent(permissionState, onUploadQr)
    }
}
