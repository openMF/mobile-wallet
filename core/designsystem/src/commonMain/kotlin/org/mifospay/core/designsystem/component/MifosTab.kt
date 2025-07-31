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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import template.core.base.designsystem.theme.KptTheme

@Composable
fun MifosTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedColor: Color = KptTheme.colorScheme.primary,
    unselectedColor: Color = KptTheme.colorScheme.primaryContainer,
    unselectedBorderColor: Color = Color.Unspecified,
) {
    Tab(
        text = {
            Text(text = text)
        },
        selected = selected,
        onClick = onClick,
        selectedContentColor = contentColorFor(selectedColor),
        unselectedContentColor = contentColorFor(unselectedColor),
        modifier = modifier
            .height(40.dp)
            .padding(horizontal = KptTheme.spacing.xs)
            .clip(KptTheme.shapes.extraLarge)
            .background(if (selected) selectedColor else unselectedColor)
            .border(
                width = 1.dp,
                color = if (!selected) unselectedBorderColor else Color.Transparent,
                shape = KptTheme.shapes.extraLarge,
            ),
    )
}
