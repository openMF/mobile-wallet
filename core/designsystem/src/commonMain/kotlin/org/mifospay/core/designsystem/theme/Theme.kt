/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import template.core.base.designsystem.KptMaterialTheme
import template.core.base.designsystem.theme.kptTheme

// private val LightDefaultColorScheme = lightColorScheme(
//    primary = md_theme_light_primary,
//    onPrimary = md_theme_light_onPrimary,
//    primaryContainer = md_theme_light_primaryContainer,
//    onPrimaryContainer = md_theme_light_onPrimaryContainer,
//    secondary = md_theme_light_secondary,
//    onSecondary = md_theme_light_onSecondary,
//    secondaryContainer = md_theme_light_secondaryContainer,
//    onSecondaryContainer = md_theme_light_onSecondaryContainer,
//    tertiary = md_theme_light_tertiary,
//    onTertiary = md_theme_light_onTertiary,
//    tertiaryContainer = md_theme_light_tertiaryContainer,
//    onTertiaryContainer = md_theme_light_onTertiaryContainer,
//    error = md_theme_light_error,
//    errorContainer = md_theme_light_errorContainer,
//    onError = md_theme_light_onError,
//    onErrorContainer = md_theme_light_onErrorContainer,
//    background = md_theme_light_background,
//    onBackground = md_theme_light_onBackground,
//    surface = md_theme_light_surface,
//    onSurface = md_theme_light_onSurface,
//    surfaceVariant = md_theme_light_surfaceVariant,
//    onSurfaceVariant = md_theme_light_onSurfaceVariant,
//    outline = md_theme_light_outline,
//    inverseOnSurface = md_theme_light_inverseOnSurface,
//    inverseSurface = md_theme_light_inverseSurface,
//    inversePrimary = md_theme_light_inversePrimary,
//    surfaceTint = md_theme_light_surfaceTint,
//    outlineVariant = md_theme_light_outlineVariant,
//    scrim = md_theme_light_scrim,
// )

private val LightDefaultColorScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

// private val DarkDefaultColorScheme = darkColorScheme(
//    primary = md_theme_dark_primary,
//    onPrimary = md_theme_dark_onPrimary,
//    primaryContainer = md_theme_dark_primaryContainer,
//    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
//    secondary = md_theme_dark_secondary,
//    onSecondary = md_theme_dark_onSecondary,
//    secondaryContainer = md_theme_dark_secondaryContainer,
//    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
//    tertiary = md_theme_dark_tertiary,
//    onTertiary = md_theme_dark_onTertiary,
//    tertiaryContainer = md_theme_dark_tertiaryContainer,
//    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
//    error = md_theme_dark_error,
//    errorContainer = md_theme_dark_errorContainer,
//    onError = md_theme_dark_onError,
//    onErrorContainer = md_theme_dark_onErrorContainer,
//    background = md_theme_dark_background,
//    onBackground = md_theme_dark_onBackground,
//    surface = md_theme_dark_surface,
//    onSurface = md_theme_dark_onSurface,
//    surfaceVariant = md_theme_dark_surfaceVariant,
//    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
//    outline = md_theme_dark_outline,
//    inverseOnSurface = md_theme_dark_inverseOnSurface,
//    inverseSurface = md_theme_dark_inverseSurface,
//    inversePrimary = md_theme_dark_inversePrimary,
//    surfaceTint = md_theme_dark_surfaceTint,
//    outlineVariant = md_theme_dark_outlineVariant,
//    scrim = md_theme_dark_scrim,
// )

private val DarkDefaultColorScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

@Composable
fun MifosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    // Color scheme
    val selectedColorScheme = when {
        else -> if (darkTheme) DarkDefaultColorScheme else LightDefaultColorScheme
    }
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

    val fontFamily = getFontFamily()

    // Composition locals
    CompositionLocalProvider(
        LocalGradientColors provides gradientColors,
        LocalBackgroundTheme provides backgroundTheme,
        LocalTintTheme provides tintTheme,
    ) {
        KptMaterialTheme(
            darkTheme = darkTheme,
            themeBuilder = { isDark ->
                kptTheme {
                    colors {
                        primary = selectedColorScheme.primary
                        onPrimary = selectedColorScheme.onPrimary
                        primaryContainer = selectedColorScheme.primaryContainer
                        onPrimaryContainer = selectedColorScheme.onPrimaryContainer
                        secondary = selectedColorScheme.secondary
                        onSecondary = selectedColorScheme.onSecondary
                        secondaryContainer = selectedColorScheme.secondaryContainer
                        onSecondaryContainer = selectedColorScheme.onSecondaryContainer
                        tertiary = selectedColorScheme.tertiary
                        onTertiary = selectedColorScheme.onTertiary
                        tertiaryContainer = selectedColorScheme.tertiaryContainer
                        onTertiaryContainer = selectedColorScheme.onTertiaryContainer
                        error = selectedColorScheme.error
                        onError = selectedColorScheme.onError
                        errorContainer = selectedColorScheme.errorContainer
                        onErrorContainer = selectedColorScheme.onErrorContainer
                        background = selectedColorScheme.background
                        onBackground = selectedColorScheme.onBackground
                        surface = selectedColorScheme.surface
                        onSurface = selectedColorScheme.onSurface
                        surfaceVariant = selectedColorScheme.surfaceVariant
                        onSurfaceVariant = selectedColorScheme.onSurfaceVariant
                        outline = selectedColorScheme.outline
                        outlineVariant = selectedColorScheme.outlineVariant
                    }
                    typography {
                        mifosTypography.displayLarge.copy(fontFamily = fontFamily)
                        mifosTypography.displayMedium.copy(fontFamily = fontFamily)
                        mifosTypography.displaySmall.copy(fontFamily = fontFamily)

                        mifosTypography.headlineLarge.copy(fontFamily = fontFamily)
                        mifosTypography.headlineMedium.copy(fontFamily = fontFamily)
                        mifosTypography.headlineSmall.copy(fontFamily = fontFamily)

                        mifosTypography.titleLarge.copy(fontFamily = fontFamily)
                        mifosTypography.titleMedium.copy(fontFamily = fontFamily)
                        mifosTypography.titleSmall.copy(fontFamily = fontFamily)

                        mifosTypography.bodyLarge.copy(fontFamily = fontFamily)
                        mifosTypography.bodyMedium.copy(fontFamily = fontFamily)
                        mifosTypography.bodySmall.copy(fontFamily = fontFamily)

                        mifosTypography.labelLarge.copy(fontFamily = fontFamily)
                        mifosTypography.labelMedium.copy(fontFamily = fontFamily)
                        mifosTypography.labelSmall.copy(fontFamily = fontFamily)
                    }
                }
            },
            content = content,
        )
    }
}
