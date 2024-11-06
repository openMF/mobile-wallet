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

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.mifospay.core.designsystem.theme.NewUi

@Composable
fun MifosDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    color: Color = NewUi.onSurface.copy(alpha = 0.05f),
) {
    HorizontalDivider(
        modifier = modifier
            .fillMaxWidth(),
        thickness = thickness,
        color = color,
    )
}
