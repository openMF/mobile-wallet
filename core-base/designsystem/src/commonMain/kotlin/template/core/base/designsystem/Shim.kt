/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */

/**
 * Foundation-migration shim (2026-08-01) — see sibling
 * `template.core.base.designsystem.core.Shim.kt` for rationale.
 *
 * Re-exports the root-package `template.core.base.designsystem.*` symbols consumers
 * import (composable themes + Material3 conversion extensions) from the corresponding
 * `kpt.core.base.designsystem.*` originals. Composable functions and extension
 * functions can't be typealiased in Kotlin, so these are declared as thin forwarders.
 */
@file:Suppress("PackageDirectoryMismatch", "ktlint:standard:filename")

package template.core.base.designsystem

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import kpt.core.base.designsystem.core.KptColorScheme
import kpt.core.base.designsystem.core.KptThemeProvider
import kpt.core.base.designsystem.core.KptTypography
import kpt.core.base.designsystem.theme.KptThemeProviderImpl
import kpt.core.base.designsystem.toKptColorScheme as kptToKptColorScheme
import kpt.core.base.designsystem.toKptTypography as kptToKptTypography

/**
 * Root-package `KptTheme` composable — provides the KPT design tokens via composition
 * locals only (no Material3 wiring). Distinct from the `theme.KptTheme` object (which
 * exposes `KptTheme.colorScheme`/`.typography` etc.) — see the theme-package Shim for that.
 *
 * Forwarder for callers importing `template.core.base.designsystem.KptTheme` as a
 * composable — resolves via the composable overload distinction (object vs. function).
 */
@Composable
@Deprecated(
    message = "Use kpt.core.base.designsystem.KptTheme",
    replaceWith = ReplaceWith("kpt.core.base.designsystem.KptTheme(theme, content)"),
)
fun KptTheme(
    theme: KptThemeProvider = KptThemeProviderImpl(),
    content: @Composable () -> Unit,
) = kpt.core.base.designsystem.KptTheme(theme = theme, content = content)

/**
 * Material3-integrated theme composable. Forwarder for the `theme`-only overload.
 */
@Composable
@Deprecated(
    message = "Use kpt.core.base.designsystem.KptMaterialTheme",
    replaceWith = ReplaceWith("kpt.core.base.designsystem.KptMaterialTheme(theme, content)"),
)
fun KptMaterialTheme(
    theme: KptThemeProvider = KptThemeProviderImpl(),
    content: @Composable () -> Unit,
) = kpt.core.base.designsystem.KptMaterialTheme(theme = theme, content = content)

/**
 * Extension conversion `ColorScheme → KptColorScheme`. Uses no @Composable receiver — safe
 * to call from ordinary code.
 */
@Deprecated(
    message = "Use kpt.core.base.designsystem.toKptColorScheme",
    replaceWith = ReplaceWith("kpt.core.base.designsystem.toKptColorScheme()"),
)
fun ColorScheme.toKptColorScheme(): KptColorScheme =
    this.kptToKptColorScheme()

/**
 * Extension conversion `Typography → KptTypography`.
 */
@Deprecated(
    message = "Use kpt.core.base.designsystem.toKptTypography",
    replaceWith = ReplaceWith("kpt.core.base.designsystem.toKptTypography()"),
)
fun Typography.toKptTypography(): KptTypography =
    this.kptToKptTypography()
