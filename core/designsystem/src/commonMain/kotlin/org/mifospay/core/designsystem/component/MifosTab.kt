/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MifosTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedContentColor: Color = MaterialTheme.colorScheme.primary,
    unselectedContentColor: Color = MaterialTheme.colorScheme.primaryContainer,
) {
    Tab(
        text = {
            Text(
                text = text,
                color = if (selected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
        },
        selected = selected,
        modifier = modifier
            .clip(RoundedCornerShape(25.dp))
            .background(if (selected) selectedContentColor else unselectedContentColor)
            .padding(horizontal = 20.dp),
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
        onClick = onClick,
    )
}
