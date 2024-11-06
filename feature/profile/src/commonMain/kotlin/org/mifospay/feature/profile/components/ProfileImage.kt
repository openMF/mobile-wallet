/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.profile.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import kotlinx.coroutines.launch
import mobile_wallet.feature.profile.generated.resources.Res
import mobile_wallet.feature.profile.generated.resources.placeholder
import org.jetbrains.compose.resources.painterResource
import org.mifospay.core.designsystem.icon.MifosIcons
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun ProfileImage(
    modifier: Modifier = Modifier,
    bitmap: String? = null,
) {
    val context = LocalPlatformContext.current

    Box(
        modifier = modifier
            .size(150.dp),
        contentAlignment = Alignment.Center,
    ) {
        val image = bitmap?.let {
            try {
                Base64.decode(it)
            } catch (e: Exception) {
                byteArrayOf()
            }
        }

        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(image)
                .build(),
            error = painterResource(Res.drawable.placeholder),
            fallback = painterResource(Res.drawable.placeholder),
            imageLoader = ImageLoader(context),
            contentDescription = "Profile Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .border(4.dp, MaterialTheme.colorScheme.primary, CircleShape),
        )
    }
}

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun EditableProfileImage(
    modifier: Modifier = Modifier,
    serverImage: String? = null,
    onChooseImage: (String) -> Unit,
) {
    val context = LocalPlatformContext.current
    val scope = rememberCoroutineScope()

    var bytes by remember(serverImage) { mutableStateOf<ByteArray?>(null) }

    LaunchedEffect(serverImage) {
        if (serverImage != null) {
            bytes = try {
                Base64.decode(serverImage)
            } catch (e: Exception) {
                null
            }
        }
    }

    // Pick files from Compose
    val launcher = rememberFilePickerLauncher(mode = PickerMode.Single) { file ->
        scope.launch {
            if (file != null) {
                bytes = if (file.supportsStreams()) {
                    val size = file.getSize()
                    if (size != null && size > 0L) {
                        val buffer = ByteArray(size.toInt())
                        val tmpBuffer = ByteArray(1000)
                        var totalBytesRead = 0
                        file.getStream().use {
                            while (it.hasBytesAvailable()) {
                                val numRead = it.readInto(tmpBuffer, 1000)
                                tmpBuffer.copyInto(
                                    buffer,
                                    destinationOffset = totalBytesRead,
                                    endIndex = numRead,
                                )
                                totalBytesRead += numRead
                            }
                        }
                        buffer
                    } else {
                        file.readBytes()
                    }
                } else {
                    file.readBytes()
                }
                bytes?.let {
                    onChooseImage(Base64.encode(it))
                }
            }
        }
    }

    Box(
        modifier = modifier
            .size(150.dp),
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            model = bytes,
            error = painterResource(Res.drawable.placeholder),
            fallback = painterResource(Res.drawable.placeholder),
            imageLoader = ImageLoader(context),
            contentDescription = "Profile Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .border(4.dp, MaterialTheme.colorScheme.primary, CircleShape),
        )

        IconButton(
            onClick = {
                launcher.launch()
            },
            modifier = Modifier
                .offset(y = 12.dp)
                .size(36.dp)
                .clip(CircleShape)
                .align(Alignment.BottomCenter),
            colors = IconButtonDefaults.iconButtonColors(Color.White),
        ) {
            Icon(
                imageVector = MifosIcons.Edit2,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp),
            )
        }
    }
}
