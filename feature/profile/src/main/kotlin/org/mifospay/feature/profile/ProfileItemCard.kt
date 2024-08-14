/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.profile

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.designsystem.theme.MifosTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ProfileItemCard(
    icon: ImageVector,
    text: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val combinedModifier = modifier
        .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(8.dp))
        .padding(16.dp)
        .clickable { onClick.invoke() }

    FlowRow(modifier = combinedModifier) {
        Icon(
            painter = rememberVectorPainter(icon),
            modifier = Modifier.size(32.dp),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            modifier = if (text == R.string.feature_profile_edit_profile ||
                text == R.string.feature_profile_settings
            ) {
                Modifier
                    .padding(start = 18.dp)
                    .align(Alignment.CenterVertically)
            } else {
                Modifier
            },
            text = stringResource(id = text),
            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (text == R.string.feature_profile_edit_profile || text == R.string.feature_profile_settings) {
            Spacer(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
internal fun DetailItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "$label: $value",
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier.padding(bottom = 12.dp),
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewProfileItemCard() {
    MifosTheme {
        ProfileItemCard(
            icon = MifosIcons.Profile,
            text = R.string.feature_profile_edit_profile,
            onClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewDetailItem() {
    MifosTheme {
        DetailItem(label = "Email", value = "john.doe@example.com")
    }
}
