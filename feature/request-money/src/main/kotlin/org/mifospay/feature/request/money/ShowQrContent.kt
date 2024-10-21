/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.request.money

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosOutlinedButton
import org.mifospay.core.designsystem.theme.MifosBlue

@Composable
internal fun ShowQrContent(
    qrDataBitmap: Bitmap,
    showAmountDialog: () -> Unit,
    modifier: Modifier = Modifier,
    onShare: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                MifosBlue,
            )
            .padding(25.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(21.dp))
                    .background(Color.White)
                    .border(
                        BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                        RoundedCornerShape(21.dp),
                    )
                    .padding(vertical = 15.dp)
                    .aspectRatio(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = R.string.feature_request_money_title),
                    color = MifosBlue,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                AsyncImage(
                    model = qrDataBitmap,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(
                verticalArrangement = Arrangement.spacedBy(15.dp),
            ) {
                MifosButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    onClick = { showAmountDialog() },
                    color = Color.White,
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_request_money_set_amount),
                        color = MifosBlue,
                    )
                }
                MifosOutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    onClick = { onShare() },
                    border = BorderStroke(
                        1.dp,
                        Color.White.copy(alpha = 0.3f),
                    ),
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_request_money_share),
                        color = Color.White,
                    )
                }
            }
        }
    }
}
