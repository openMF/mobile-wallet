/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.standing.instruction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun SIContent(
    fromClientName: String,
    toClientName: String,
    validTill: String,
    amount: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(10.dp)) {
        Text(
            text = fromClientName,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 20.dp),
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = toClientName,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 4.dp),
            )
            Text(
                text = amount,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                modifier = Modifier.padding(end = 8.dp, bottom = 8.dp),
            )
        }

        Text(
            text = validTill,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp),
        )

        HorizontalDivider(
            color = MaterialTheme.colorScheme.onSurface,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SIContentPreview() {
    SIContent("From Client", "To Client", "Date", "Amount")
}
