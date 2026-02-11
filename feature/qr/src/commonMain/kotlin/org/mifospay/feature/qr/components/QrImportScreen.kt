/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.qr.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.ImageLoader
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import mobile_wallet.feature.qr.generated.resources.Res
import mobile_wallet.feature.qr.generated.resources.feature_qr_drag_drop_hint
import mobile_wallet.feature.qr.generated.resources.feature_qr_drop_image_here
import mobile_wallet.feature.qr.generated.resources.feature_qr_import_description
import mobile_wallet.feature.qr.generated.resources.feature_qr_import_qr_code
import mobile_wallet.feature.qr.generated.resources.feature_qr_preview_image
import mobile_wallet.feature.qr.generated.resources.feature_qr_processing
import mobile_wallet.feature.qr.generated.resources.feature_qr_select_image
import mobile_wallet.feature.qr.generated.resources.feature_qr_select_qr_image
import mobile_wallet.feature.qr.generated.resources.feature_qr_supported_formats
import mobile_wallet.feature.qr.generated.resources.feature_qr_supported_transfers
import org.jetbrains.compose.resources.stringResource
import org.mifospay.core.designsystem.icon.MifosIcons
import template.core.base.designsystem.theme.KptTheme

@Composable
fun QrImportScreen(
    isProcessing: Boolean,
    onSelectImage: () -> Unit,
    modifier: Modifier = Modifier,
    imagePreviewBytes: ByteArray? = null,
    isDragging: Boolean = false,
) {
    val primaryColor = KptTheme.colorScheme.primary
    val importTitle = stringResource(Res.string.feature_qr_import_qr_code)
    val selectImageDescription = stringResource(Res.string.feature_qr_select_qr_image)
    val platformContext = LocalPlatformContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(KptTheme.colorScheme.background)
            .semantics { contentDescription = importTitle },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Header
            Icon(
                imageVector = MifosIcons.QrCode2,
                contentDescription = null,
                tint = primaryColor,
                modifier = Modifier.size(64.dp),
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = importTitle,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = KptTheme.colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(Res.string.feature_qr_supported_transfers),
                fontSize = 14.sp,
                color = KptTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Import area with drag & drop visual feedback
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        border = BorderStroke(
                            width = if (isDragging) 3.dp else 2.dp,
                            color = if (isDragging) {
                                primaryColor
                            } else {
                                primaryColor.copy(alpha = 0.3f)
                            },
                        ),
                        shape = RoundedCornerShape(16.dp),
                    )
                    .background(
                        if (isDragging) {
                            primaryColor.copy(alpha = 0.15f)
                        } else {
                            primaryColor.copy(alpha = 0.05f)
                        },
                    )
                    .padding(32.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    when {
                        isProcessing -> {
                            // Show image preview while processing
                            if (imagePreviewBytes != null) {
                                Box(
                                    modifier = Modifier
                                        .size(150.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Image(
                                        painter = rememberAsyncImagePainter(
                                            model = imagePreviewBytes,
                                            imageLoader = ImageLoader(platformContext),
                                        ),
                                        contentDescription = stringResource(Res.string.feature_qr_preview_image),
                                        modifier = Modifier
                                            .size(130.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Fit,
                                    )
                                }

                                Spacer(modifier = Modifier.height(24.dp))
                            }

                            CircularProgressIndicator(
                                color = primaryColor,
                                modifier = Modifier.size(48.dp),
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = stringResource(Res.string.feature_qr_processing),
                                fontSize = 16.sp,
                                color = KptTheme.colorScheme.onBackground,
                            )
                        }

                        isDragging -> {
                            Icon(
                                imageVector = MifosIcons.PhotoLibrary,
                                contentDescription = null,
                                tint = primaryColor,
                                modifier = Modifier.size(80.dp),
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = stringResource(Res.string.feature_qr_drop_image_here),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = primaryColor,
                                textAlign = TextAlign.Center,
                            )
                        }

                        else -> {
                            Icon(
                                imageVector = MifosIcons.PhotoLibrary,
                                contentDescription = null,
                                tint = primaryColor.copy(alpha = 0.6f),
                                modifier = Modifier.size(80.dp),
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = stringResource(Res.string.feature_qr_import_description),
                                fontSize = 16.sp,
                                color = KptTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center,
                                lineHeight = 24.sp,
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            Button(
                                onClick = onSelectImage,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = primaryColor,
                                ),
                                modifier = Modifier
                                    .fillMaxWidth(0.6f)
                                    .height(52.dp)
                                    .semantics {
                                        role = Role.Button
                                        contentDescription = selectImageDescription
                                    },
                            ) {
                                Icon(
                                    imageVector = MifosIcons.PhotoLibrary,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                                Text(
                                    text = stringResource(Res.string.feature_qr_select_image),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = stringResource(Res.string.feature_qr_drag_drop_hint),
                                fontSize = 14.sp,
                                color = KptTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Supported formats
            Text(
                text = stringResource(Res.string.feature_qr_supported_formats),
                fontSize = 12.sp,
                color = KptTheme.colorScheme.onBackground.copy(alpha = 0.4f),
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Footer branding
            Text(
                text = "Powered by Mifos",
                fontSize = 12.sp,
                color = KptTheme.colorScheme.onBackground.copy(alpha = 0.4f),
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
