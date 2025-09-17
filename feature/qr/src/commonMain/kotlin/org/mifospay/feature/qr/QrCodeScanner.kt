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

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mobile_wallet.feature.qr.generated.resources.Res
import mobile_wallet.feature.qr.generated.resources.feature_qr_instruction
import mobile_wallet.feature.qr.generated.resources.feature_qr_warning_message
import mobile_wallet.feature.qr.generated.resources.feature_qr_warning_title
import org.jetbrains.compose.resources.stringResource
import org.mifospay.core.designsystem.component.MifosCard
import org.mifospay.core.designsystem.icon.MifosIcons
import template.core.base.designsystem.theme.KptTheme

@Composable
expect fun QrCodeScanner(
    types: List<CodeType>,
    modifier: Modifier = Modifier,
    onScanned: (String) -> Boolean,
)

@Composable
fun QrScannerWithPermissions(
    types: List<CodeType>,
    modifier: Modifier = Modifier,
    permissionText: String = "Camera is required for QR Code scanning",
    openSettingsLabel: String = "Open Settings",
    onScanned: (String) -> Boolean,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(KptTheme.spacing.lg)
            .padding(top = KptTheme.spacing.lg),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 56.dp + 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(Res.string.feature_qr_instruction),
                textAlign = TextAlign.Center,
                color = Color.Black,
            )

            Spacer(modifier = Modifier.height(KptTheme.spacing.md))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(KptTheme.shapes.medium)
                    .drawQrCorners()
                    .border(1.dp, Color.Transparent, KptTheme.shapes.medium),
                contentAlignment = Alignment.Center,
            ) {
                QrScannerWithPermissions(
                    types = types,
                    modifier = modifier.clipToBounds(),
                    onScanned = onScanned,
                    permissionDeniedContent = { permissionState ->
                        Column(
                            modifier = modifier,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                modifier = Modifier.padding(KptTheme.spacing.sm),
                                text = permissionText,
                            )
                            Button(
                                onClick = permissionState::goToSettings,
                            ) {
                                Text(openSettingsLabel)
                            }
                        }
                    },
                )

                MifosCard(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(KptTheme.spacing.sm),
                    shape = KptTheme.shapes.medium,
                ) {
                    Column(
                        modifier = Modifier.padding(KptTheme.spacing.lg),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
                        ) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                imageVector = MifosIcons.Warning,
                                contentDescription = "Warning",
                                tint = Color.Unspecified,
                            )
                            Text(
                                text = stringResource(Res.string.feature_qr_warning_title),
                                color = Color.Black,
                            )
                        }
                        Text(
                            text = stringResource(Res.string.feature_qr_warning_message),
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QrScannerWithPermissions(
    types: List<CodeType>,
    modifier: Modifier = Modifier,
    onScanned: (String) -> Boolean,
    permissionDeniedContent: @Composable (CameraPermissionState) -> Unit,
) {
    val permissionState = rememberCameraPermissionState()

    LaunchedEffect(Unit) {
        if (permissionState.status == CameraPermissionStatus.Denied) {
            permissionState.requestCameraPermission()
        }
    }

    if (permissionState.status == CameraPermissionStatus.Granted) {
        QrCodeScanner(types = types, modifier, onScanned = onScanned)
    } else {
        permissionDeniedContent(permissionState)
    }
}

private fun Modifier.drawQrCorners(): Modifier = drawWithContent {
    drawContent()

    val strokeWidth = 5.dp.toPx()
    val lineLength = 40.dp.toPx()

    val horizontalPadding = 50.dp.toPx() // for left & right
    val verticalPaddingTop = 50.dp.toPx() // for top corners
    val verticalPaddingBottom = 300.dp.toPx() // more padding for bottom corners

    val color = Color.White

    // Top-left
    drawLine(
        color,
        Offset(horizontalPadding, verticalPaddingTop),
        Offset(horizontalPadding + lineLength, verticalPaddingTop),
        strokeWidth,
    )
    drawLine(
        color,
        Offset(horizontalPadding, verticalPaddingTop),
        Offset(horizontalPadding, verticalPaddingTop + lineLength),
        strokeWidth,
    )

    // Top-right
    drawLine(
        color,
        Offset(size.width - horizontalPadding, verticalPaddingTop),
        Offset(size.width - horizontalPadding - lineLength, verticalPaddingTop),
        strokeWidth,
    )
    drawLine(
        color,
        Offset(size.width - horizontalPadding, verticalPaddingTop),
        Offset(size.width - horizontalPadding, verticalPaddingTop + lineLength),
        strokeWidth,
    )

    // Bottom-left
    drawLine(
        color,
        Offset(horizontalPadding, size.height - verticalPaddingBottom),
        Offset(horizontalPadding + lineLength, size.height - verticalPaddingBottom),
        strokeWidth,
    )
    drawLine(
        color,
        Offset(horizontalPadding, size.height - verticalPaddingBottom),
        Offset(horizontalPadding, size.height - verticalPaddingBottom - lineLength),
        strokeWidth,
    )

    // Bottom-right
    drawLine(
        color,
        Offset(size.width - horizontalPadding, size.height - verticalPaddingBottom),
        Offset(size.width - horizontalPadding - lineLength, size.height - verticalPaddingBottom),
        strokeWidth,
    )
    drawLine(
        color,
        Offset(size.width - horizontalPadding, size.height - verticalPaddingBottom),
        Offset(size.width - horizontalPadding, size.height - verticalPaddingBottom - lineLength),
        strokeWidth,
    )
}
