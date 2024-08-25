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

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun MifosTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedContentColor: Color = MaterialTheme.colorScheme.onSurface,
    unselectedContentColor: Color = Color.LightGray,
) {
    Tab(
        text = {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
        selected = selected,
        modifier = modifier,
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
        onClick = onClick,
    )
}
