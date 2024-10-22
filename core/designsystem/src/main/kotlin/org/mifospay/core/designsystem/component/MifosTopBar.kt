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

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import org.mifospay.core.designsystem.icon.MifosIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MifosTopBar(
    topBarTitle: Int,
    backPress: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    titleColor: Color? = null,
    iconTint: Color? = null,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(id = topBarTitle),
                style = MaterialTheme.typography.titleMedium,
                color = titleColor ?: MaterialTheme.colorScheme.onSurface,
            )
        },
        navigationIcon = {
            IconBox(
                icon = MifosIcons.ArrowBack2,
                onClick = backPress,
                tint = iconTint,
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
        ),
        actions = actions,
        modifier = modifier,
    )
}
