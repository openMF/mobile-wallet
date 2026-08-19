/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
@file:Suppress("unused")

package kpt.core.designsystem.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kpt.core.designsystem.theme.spacing
import kpt.core.base.designsystem.component.AppCard as KptAppCard

/**
 * Back-compat shim — `AppCard` was promoted to `core-base/designsystem`.
 *
 * Forward to [kpt.core.base.designsystem.component.AppCard]; this overload is kept
 * only so existing imports of `kpt.core.designsystem.component.AppCard` keep
 * compiling for one release. New code should import from `core-base/designsystem`.
 *
 * `@Composable` functions cannot be `typealias`'d, so this is a forwarding shim rather
 * than a type alias.
 */
@Deprecated(
    "Promoted to core-base/designsystem; import kpt.core.base.designsystem.component.AppCard",
    ReplaceWith(
        "AppCard(modifier, contentPadding, containerColor, accentColor, cornerRadius, elevation, content)",
        "kpt.core.base.designsystem.component.AppCard",
    ),
    level = DeprecationLevel.WARNING,
)
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(MaterialTheme.spacing.lg),
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLowest,
    accentColor: Color? = null,
    cornerRadius: Dp = 20.dp,
    elevation: Dp = 6.dp,
    content: @Composable () -> Unit,
) {
    KptAppCard(
        modifier = modifier,
        contentPadding = contentPadding,
        containerColor = containerColor,
        accentColor = accentColor,
        cornerRadius = cornerRadius,
        elevation = elevation,
        content = content,
    )
}
