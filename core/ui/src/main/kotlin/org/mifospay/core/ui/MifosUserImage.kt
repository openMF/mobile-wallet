/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import org.mifospay.core.designsystem.theme.MifosTheme

@Composable
fun MifosUserImage(
    modifier: Modifier = Modifier,
    bitmap: Bitmap? = null,
) {
    if (bitmap == null) {
        Image(
            modifier = modifier
                .clip(CircleShape),
            painter = painterResource(id = R.drawable.core_ui_ic_dp_placeholder),
            contentDescription = "Empty profile Image",
            contentScale = ContentScale.Fit,
        )
    } else {
        Image(
            modifier = modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Profile Image",
            contentScale = ContentScale.Crop,
        )
    }
}

@DevicePreviews
@Composable
private fun MifosUserImagePreview(
    modifier: Modifier = Modifier,
) {
    MifosTheme {
        MifosUserImage(modifier)
    }
}
