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

import androidx.compose.ui.graphics.Color

// val md_theme_light_primary = Color(0xFF0673BA) // primary
// val md_theme_light_onPrimary = Color(0xFFFFFFFF) // gradientOne
// val md_theme_light_primaryContainer = Color(0xFFF5F5F5) // container color
// val md_theme_light_onPrimaryContainer = Color(0xFF3E001D)
//
// val md_theme_light_secondary = Color(0xFF984061)
// val md_theme_light_onSecondary = Color(0xFFFFFFFF)
// val md_theme_light_secondaryContainer = Color(0xFFFFD9E2)
// val md_theme_light_onSecondaryContainer = Color(0xFF3E001D)
//
// val md_theme_light_tertiary = Color(0xFF7D4996)
// val md_theme_light_onTertiary = Color(0xFFFFFFFF)
// val md_theme_light_tertiaryContainer = Color(0xFFF6D9FF)
// val md_theme_light_onTertiaryContainer = Color(0xFF310049)
//
// val md_theme_light_error = Color(0xFFBA1A1A)
// val md_theme_light_errorContainer = Color(0xFFFFDAD6)
// val md_theme_light_onError = Color(0xFFFFFFFF)
// val md_theme_light_onErrorContainer = Color(0xFF410002)
//
// val md_theme_light_background = Color(0xFFFFFBFF)
// val md_theme_light_onBackground = Color(0xFF330045)
//
// val md_theme_light_surface = Color(0xFFFFFBFF)
// val md_theme_light_onSurface = Color(0xFF333333) // onSurface
// val md_theme_light_surfaceVariant = Color(0xFFF2DDE1)
// val md_theme_light_onSurfaceVariant = Color(0xFF514347)
// val md_theme_light_surfaceTint = Color(0xFF984061)
//
// val md_theme_light_outline = Color(0xFF837377)
// val md_theme_light_outlineVariant = Color(0xFFD5C2C6)
//
// val md_theme_light_inverseOnSurface = Color(0xFFFFEBFF)
// val md_theme_light_inverseSurface = Color(0xFF4D1661)
// val md_theme_light_inversePrimary = Color(0xFFFFB1C8)
//
// val md_theme_light_shadow = Color(0xFF000000)
// val md_theme_light_scrim = Color(0xFF000000)
//
//
// // val md_theme_dark_primary = Color(0xFFFFFFFF)
// val md_theme_dark_primary = Color(0xFF80CFFF)
// val md_theme_dark_onPrimary = Color(0xFF000000)
// val md_theme_dark_primaryContainer = Color(0xFF7B2949)
// val md_theme_dark_onPrimaryContainer = Color(0xFFFFD9E2)
//
// val md_theme_dark_secondary = Color(0xFFFFB1C8)
// val md_theme_dark_onSecondary = Color(0xFF5E1133)
// val md_theme_dark_secondaryContainer = Color(0xFF7B2949)
// val md_theme_dark_onSecondaryContainer = Color(0xFFFFD9E2)
//
// val md_theme_dark_tertiary = Color(0xFFE8B3FF)
// val md_theme_dark_onTertiary = Color(0xFF4A1764)
// val md_theme_dark_tertiaryContainer = Color(0xFF63307C)
// val md_theme_dark_onTertiaryContainer = Color(0xFFF6D9FF)
//
// val md_theme_dark_error = Color(0xFFFFB4AB)
// val md_theme_dark_errorContainer = Color(0xFF93000A)
// val md_theme_dark_onError = Color(0xFF690005)
// val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
//
// val md_theme_dark_background = Color(0xFF330045)
// val md_theme_dark_onBackground = Color(0xFFFAD7FF)
//
// // val md_theme_dark_surface = Color(0xFF000000)
// val md_theme_dark_surface = Color(0xFF1E1E1E)
// val md_theme_dark_onSurface = Color(0xFFFFFFFF)
// val md_theme_dark_surfaceVariant = Color(0xFF514347)
// val md_theme_dark_onSurfaceVariant = Color(0xFFD5C2C6)
// val md_theme_dark_surfaceTint = Color(0xFFFFB1C8)
//
// val md_theme_dark_outline = Color(0xFF9E8C90)
// val md_theme_dark_outlineVariant = Color(0xFF514347)
//
// val md_theme_dark_inverseOnSurface = Color(0xFF330045)
// val md_theme_dark_inverseSurface = Color(0xFFFAD7FF)
// val md_theme_dark_inversePrimary = Color(0xFF984061)
//
// val md_theme_dark_shadow = Color(0xFF000000)
// val md_theme_dark_scrim = Color(0xFF000000)

// Primary Light
val primaryLight = Color(0xFF0673BA) // Main brand color
val onPrimaryLight = Color(0xFFFFFFFF) // Contrast text/icon on primary
val primaryContainerLight = Color(0xFFCEEAFD) // Lighter tone for background of components
val onPrimaryContainerLight = Color(0XFF033D63) // 	Text/icon over container
val secondaryLight = Color(0xFF984061) // Main secondary tone, rich and expressive
val onSecondaryLight = Color(0xFFFFFFFF) // Best contrast for readable text/icons
val secondaryContainerLight = Color(0xFFF0DBE3) // Soft container tone
val onSecondaryContainerLight = Color(0xFF361722) // Dark text/icon for contrast on container
val tertiaryLight = Color(0xFF7D4996) // Base tertiary
val onTertiaryLight = Color(0xFFFFFFFF) // Light text/icons on tertiary
val tertiaryContainerLight = Color(0XFFE9DDEE) // soft pastel background variant
val onTertiaryContainerLight = Color(0xFF2B1934) // Deep tone for legibility over container
val errorLight = Color(0xFFBA1A1A)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFFDAD6)
val onErrorContainerLight = Color(0xFF93000A)
val backgroundLight = Color(0xFFF8F9FF)
val onBackgroundLight = Color(0xFF181C20)
val surfaceLight = Color(0xFFF8F9FF)
val onSurfaceLight = Color(0xFF181C20)
val surfaceVariantLight = Color(0xFFDCE3EF)
val onSurfaceVariantLight = Color(0xFF404751)
val outlineLight = Color(0xFF717882)
val outlineVariantLight = Color(0xFFC0C7D2)
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF2D3136)
val inverseOnSurfaceLight = Color(0xFFEEF1F7)
val inversePrimaryLight = Color(0xFF9BCAFF)
val surfaceDimLight = Color(0xFFD8DAE0)
val surfaceBrightLight = Color(0xFFF8F9FF)
val surfaceContainerLowestLight = Color(0xFFFFFFFF)
val surfaceContainerLowLight = Color(0xFFF1F3FA)
val surfaceContainerLight = Color(0xFFECEEF4)
val surfaceContainerHighLight = Color(0xFFE6E8EE)
val surfaceContainerHighestLight = Color(0xFFE0E2E9)

val primaryDark = Color(0xFF9CD6FC) // Slightly desaturated light tone
val onPrimaryDark = Color(0xFF033D63) // Dark enough to maintain contrast
val primaryContainerDark = Color(0xFF044C7C) // Deep tone for component background
val onPrimaryContainerDark = Color(0XFFE6F5FE) // Light text/icon over container
val secondaryDark = Color(0xFFD9A5B8) // Softer tone for dark surfaces
val onSecondaryDark = Color(0xFF361722) // Still provides strong contrast
val secondaryContainerDark = Color(0xFF5A2639) // Deep tone for container
val onSecondaryContainerDark = Color(0xFFF0DBE3) // 	Light foreground over container
val tertiaryDark = Color(0xFFC7A9D6) // bright pop for dark mode
val onTertiaryDark = Color(0xFF3A2145) // Deep text/icon for contrast
val tertiaryContainerDark = Color(0xFF653A78) // Solid background fill
val onTertiaryContainerDark = Color(0xFFF4EEF7) // Light text/icons over container
val errorDark = Color(0xFFFFB4AB)
val onErrorDark = Color(0xFF690005)
val errorContainerDark = Color(0xFF93000A)
val onErrorContainerDark = Color(0xFFFFDAD6)
val backgroundDark = Color(0xFF101418)
val onBackgroundDark = Color(0xFFE0E2E9)
val surfaceDark = Color(0xFF101418)
val onSurfaceDark = Color(0xFFE0E2E9)
val surfaceVariantDark = Color(0xFF404751)
val onSurfaceVariantDark = Color(0xFFC0C7D2)
val outlineDark = Color(0xFF8A919C)
val outlineVariantDark = Color(0xFF404751)
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFE0E2E9)
val inverseOnSurfaceDark = Color(0xFF2D3136)
val inversePrimaryDark = Color(0xFF0062A0)
val surfaceDimDark = Color(0xFF101418)
val surfaceBrightDark = Color(0xFF36393E)
val surfaceContainerLowestDark = Color(0xFF0B0E13)
val surfaceContainerLowDark = Color(0xFF181C20)
val surfaceContainerDark = Color(0xFF1C2025)
val surfaceContainerHighDark = Color(0xFF272A2F)
val surfaceContainerHighestDark = Color(0xFF32353A)

// New UI Colors
object NewUi {
    val walletColor1 = Color(0xFF1f7dd5)
    val walletColor2 = Color(0xFF1ec0a0)
}
