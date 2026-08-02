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
 * Re-exports every consumed `template.core.base.designsystem.theme.*` symbol from
 * `kpt.core.base.designsystem.theme.*`. Classes/objects/builders use `typealias`;
 * top-level `staticCompositionLocalOf` vals need `val ... get() = ...` accessors
 * (Kotlin has no top-level-val typealias). The `kptTheme { ... }` builder function
 * needs a runtime forwarder — the lambda receiver type `KptThemeBuilder` is aliased
 * across, so the DSL still resolves for callers.
 */
@file:Suppress("PackageDirectoryMismatch")

package template.core.base.designsystem.theme

import androidx.compose.runtime.ProvidableCompositionLocal
import kpt.core.base.designsystem.core.KptColorScheme
import kpt.core.base.designsystem.core.KptElevation
import kpt.core.base.designsystem.core.KptShapes
import kpt.core.base.designsystem.core.KptSpacing
import kpt.core.base.designsystem.core.KptThemeProvider
import kpt.core.base.designsystem.core.KptTypography

@Deprecated(
    message = "Use kpt.core.base.designsystem.theme.KptColorSchemeImpl",
    replaceWith = ReplaceWith("kpt.core.base.designsystem.theme.KptColorSchemeImpl"),
)
typealias KptColorSchemeImpl = kpt.core.base.designsystem.theme.KptColorSchemeImpl

@Deprecated(
    message = "Use kpt.core.base.designsystem.theme.KptShapesImpl",
    replaceWith = ReplaceWith("kpt.core.base.designsystem.theme.KptShapesImpl"),
)
typealias KptShapesImpl = kpt.core.base.designsystem.theme.KptShapesImpl

@Deprecated(
    message = "Use kpt.core.base.designsystem.theme.KptTypographyImpl",
    replaceWith = ReplaceWith("kpt.core.base.designsystem.theme.KptTypographyImpl"),
)
typealias KptTypographyImpl = kpt.core.base.designsystem.theme.KptTypographyImpl

@Deprecated(
    message = "Use kpt.core.base.designsystem.theme.KptThemeProviderImpl",
    replaceWith = ReplaceWith("kpt.core.base.designsystem.theme.KptThemeProviderImpl"),
)
typealias KptThemeProviderImpl = kpt.core.base.designsystem.theme.KptThemeProviderImpl

/**
 * The `theme.KptTheme` singleton (design-token access — `KptTheme.colorScheme`, `KptTheme.spacing`,
 * etc.). Distinct from the root-package `KptTheme` composable — that one is in
 * `template.core.base.designsystem.Shim.kt` as an @Composable forwarder.
 */
@Deprecated(
    message = "Use kpt.core.base.designsystem.theme.KptTheme",
    replaceWith = ReplaceWith("kpt.core.base.designsystem.theme.KptTheme"),
)
typealias KptTheme = kpt.core.base.designsystem.theme.KptTheme

/**
 * `KptThemeBuilder` typealias — needed so the `kptTheme { colors { ... } }` DSL receiver
 * resolves correctly for callers that write the lambda against the template.* namespace.
 */
@Deprecated(
    message = "Use kpt.core.base.designsystem.theme.KptThemeBuilder",
    replaceWith = ReplaceWith("kpt.core.base.designsystem.theme.KptThemeBuilder"),
)
typealias KptThemeBuilder = kpt.core.base.designsystem.theme.KptThemeBuilder

// Top-level composition-local vals — Kotlin has no top-level-val typealias, so forward via property accessor.
// Getter is idempotent and cheap — each read returns the same underlying static CompositionLocal.

@Deprecated(
    message = "Use kpt.core.base.designsystem.theme.LocalKptColors",
    replaceWith = ReplaceWith("kpt.core.base.designsystem.theme.LocalKptColors"),
)
val LocalKptColors: ProvidableCompositionLocal<KptColorScheme>
    get() = kpt.core.base.designsystem.theme.LocalKptColors

@Deprecated(
    message = "Use kpt.core.base.designsystem.theme.LocalKptTypography",
    replaceWith = ReplaceWith("kpt.core.base.designsystem.theme.LocalKptTypography"),
)
val LocalKptTypography: ProvidableCompositionLocal<KptTypography>
    get() = kpt.core.base.designsystem.theme.LocalKptTypography

@Deprecated(
    message = "Use kpt.core.base.designsystem.theme.LocalKptShapes",
    replaceWith = ReplaceWith("kpt.core.base.designsystem.theme.LocalKptShapes"),
)
val LocalKptShapes: ProvidableCompositionLocal<KptShapes>
    get() = kpt.core.base.designsystem.theme.LocalKptShapes

@Deprecated(
    message = "Use kpt.core.base.designsystem.theme.LocalKptSpacing",
    replaceWith = ReplaceWith("kpt.core.base.designsystem.theme.LocalKptSpacing"),
)
val LocalKptSpacing: ProvidableCompositionLocal<KptSpacing>
    get() = kpt.core.base.designsystem.theme.LocalKptSpacing

@Deprecated(
    message = "Use kpt.core.base.designsystem.theme.LocalKptElevation",
    replaceWith = ReplaceWith("kpt.core.base.designsystem.theme.LocalKptElevation"),
)
val LocalKptElevation: ProvidableCompositionLocal<KptElevation>
    get() = kpt.core.base.designsystem.theme.LocalKptElevation

/**
 * DSL builder forwarder. `KptThemeBuilder` typealias above makes the lambda receiver
 * work seamlessly for callers writing `kptTheme { colors { primary = ... } }`.
 */
@Deprecated(
    message = "Use kpt.core.base.designsystem.theme.kptTheme",
    replaceWith = ReplaceWith("kpt.core.base.designsystem.theme.kptTheme(block)"),
)
fun kptTheme(block: KptThemeBuilder.() -> Unit): KptThemeProvider =
    kpt.core.base.designsystem.theme.kptTheme(block)
