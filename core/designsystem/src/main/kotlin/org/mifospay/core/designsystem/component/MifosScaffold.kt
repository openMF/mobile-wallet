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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun MifosScaffold(
    backPress: () -> Unit,
    modifier: Modifier = Modifier,
    topBarTitle: Int? = null,
    titleColor: Color? = MaterialTheme.colorScheme.onSurface,
    iconTint: Color? = null,
    floatingActionButtonContent: FloatingActionButtonContent? = null,
    snackbarHost: @Composable () -> Unit = {},
    scaffoldContent: @Composable (PaddingValues) -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
) {
    Scaffold(
        topBar = {
            if (topBarTitle != null) {
                MifosTopBar(
                    topBarTitle = topBarTitle,
                    backPress = backPress,
                    actions = actions,
                    titleColor = titleColor,
                    iconTint = iconTint,
                )
            }
        },
        floatingActionButton = {
            floatingActionButtonContent?.let { content ->
                FloatingActionButton(
                    onClick = content.onClick,
                    contentColor = content.contentColor,
                    content = content.content,
                    containerColor = MaterialTheme.colorScheme.primary,
                )
            }
        },
        snackbarHost = snackbarHost,
        content = scaffoldContent,
        modifier = modifier,
        containerColor = Color.Transparent,
    )
}

data class FloatingActionButtonContent(
    val onClick: (() -> Unit),
    val contentColor: Color,
    val content: (@Composable () -> Unit),
)
