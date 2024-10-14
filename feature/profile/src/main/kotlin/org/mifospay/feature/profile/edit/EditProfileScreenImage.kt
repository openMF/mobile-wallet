/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.profile.edit

import android.net.Uri
import androidx.compose.foundation.Image
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.designsystem.theme.MifosBlue
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.core.ui.DevicePreviews
import org.mifospay.feature.profile.R

@Composable
fun EditProfileScreenImage(
    modifier: Modifier = Modifier,
    imageUri: Uri? = null,
    onCameraIconClick: () -> Unit,
) {
    Box(
        modifier = modifier.size(150.dp),
    ) {
        if (imageUri != null) {
            AsyncImage(
                placeholder = painterResource(id = R.drawable.checker),
                error = painterResource(id = R.drawable.checker),
                model = imageUri,
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .border(4.dp, MifosBlue, CircleShape),
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.core_ui_ic_dp_placeholder),
                contentDescription = "Empty UI",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .border(4.dp, MifosBlue, CircleShape),
                contentScale = ContentScale.Crop,
            )
        }

        IconButton(
            onClick = onCameraIconClick,
            modifier = Modifier
                .offset(y = 12.dp)
                .size(36.dp)
                .clip(CircleShape)
                .align(Alignment.BottomCenter),
            colors = IconButtonDefaults.iconButtonColors(MaterialTheme.colorScheme.onPrimaryContainer),
        ) {
            Icon(
                painter = rememberVectorPainter(MifosIcons.Edit2),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@DevicePreviews
@Composable
private fun EditProfileScreenImagePreview(
    modifier: Modifier = Modifier,
) {
    MifosTheme {
        EditProfileScreenImage(
            modifier = modifier,
            onCameraIconClick = {},
        )
    }
}
