/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifospay.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import template.core.base.designsystem.KptMaterialTheme
import template.core.base.designsystem.theme.KptThemeProviderImpl
import template.core.base.designsystem.toKptTypography

@Composable
fun MifosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    // Color scheme
    val selectedColorScheme = when {
        else -> if (darkTheme) darkKptColorScheme else lightKptColorScheme
    }
    val typography = getTypography().toKptTypography()
    val theme = KptThemeProviderImpl(
        colors = selectedColorScheme,
        typography = typography,
        // optionally shapes, spacing, elevation if you want to override defaults
    )

    val lightGradientColors = GradientColors(
        top = surfaceContainerLowestLight,
        bottom = surfaceContainerHighestLight,
        container = Color.Transparent,
    )
    val darkGradientColors = GradientColors(
        top = surfaceContainerLowestDark,
        bottom = surfaceContainerHighDark,
        container = Color.Transparent,
    )
    val gradientColors = when (darkTheme) {
        true -> darkGradientColors
        false -> lightGradientColors
    }
    // Background theme
    val defaultBackgroundTheme = BackgroundTheme(
        color = Color.Transparent,
        tonalElevation = 2.dp,
    )
    val backgroundTheme = when {
        else -> defaultBackgroundTheme
    }
    val tintTheme = when {
        else -> TintTheme()
    }

    // Composition locals
    CompositionLocalProvider(
        LocalGradientColors provides gradientColors,
        LocalBackgroundTheme provides backgroundTheme,
        LocalTintTheme provides tintTheme,
    ) {
        KptMaterialTheme(
            theme = theme,
            content = content,
        )
    }
}
