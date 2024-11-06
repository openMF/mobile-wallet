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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mobile_wallet.core.ui.generated.resources.Res
import mobile_wallet.core.ui.generated.resources.artwork
import mobile_wallet.core.ui.generated.resources.core_ui_money_in
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.designsystem.theme.MifosTheme

@Composable
fun EmptyContentScreen(
    title: String,
    subTitle: String,
    imageContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
                .testTag("mifos:empty"),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            imageContent()

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = subTitle,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
fun EmptyContentScreen(
    title: String,
    subTitle: String,
    btnText: String,
    btnIcon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    imageContent: @Composable () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
                .testTag("mifos:empty"),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            imageContent()

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = subTitle,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(48.dp))

            MifosButton(
                text = {
                    Text(text = btnText)
                },
                leadingIcon = {
                    Icon(
                        imageVector = btnIcon,
                        contentDescription = "BtnIcon",
                    )
                },
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun EmptyContentScreen(
    title: String,
    subTitle: String,
    iconDrawable: DrawableResource,
    modifier: Modifier = Modifier,
    iconTint: Color = MaterialTheme.colorScheme.surfaceTint,
) {
    EmptyContentScreen(
        title = title,
        subTitle = subTitle,
        imageContent = {
            Image(
                modifier = Modifier.size(64.dp),
                painter = painterResource(iconDrawable),
                colorFilter = if (iconTint != Color.Unspecified) ColorFilter.tint(iconTint) else null,
                contentDescription = null,
            )
        },
        modifier = modifier,
    )
}

@Composable
fun EmptyContentScreen(
    title: String,
    subTitle: String,
    modifier: Modifier = Modifier,
    drawableResource: DrawableResource = Res.drawable.artwork,
    iconTint: Color = MaterialTheme.colorScheme.surfaceTint,
) {
    EmptyContentScreen(
        title = title,
        subTitle = subTitle,
        imageContent = {
            Image(
                modifier = Modifier.size(200.dp),
                painter = painterResource(drawableResource),
                colorFilter = if (iconTint != Color.Unspecified) ColorFilter.tint(iconTint) else null,
                contentDescription = null,
            )
        },
        modifier = modifier,
    )
}

@Composable
fun EmptyContentScreen(
    title: String,
    subTitle: String,
    btnText: String,
    btnIcon: ImageVector,
    modifier: Modifier = Modifier,
    icon: ImageVector = MifosIcons.Info,
    iconTint: Color = MaterialTheme.colorScheme.surfaceTint,
    onClick: () -> Unit,
) {
    EmptyContentScreen(
        title = title,
        subTitle = subTitle,
        imageContent = {
            Icon(
                modifier = Modifier.size(64.dp),
                imageVector = icon,
                tint = iconTint,
                contentDescription = null,
            )
        },
        btnText = btnText,
        btnIcon = btnIcon,
        onClick = onClick,
        modifier = modifier,
    )
}

@DevicePreviews
@Composable
fun EmptyContentScreenDrawableImagePreview() {
    MifosTheme {
        EmptyContentScreen(
            title = "No data found",
            subTitle = "Please check you connection or try again",
            iconDrawable = Res.drawable.core_ui_money_in,
            modifier = Modifier,
            iconTint = MaterialTheme.colorScheme.primary,
        )
    }
}

@DevicePreviews
@Composable
fun EmptyContentScreenImageVectorPreview() {
    MifosTheme {
        EmptyContentScreen(
            title = "No data found",
            subTitle = "Please check you connection or try again",
            modifier = Modifier,
            iconTint = MaterialTheme.colorScheme.primary,
        )
    }
}
