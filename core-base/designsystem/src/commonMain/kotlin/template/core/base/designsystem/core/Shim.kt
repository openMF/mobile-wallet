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
 * Foundation-migration shim (2026-08-01):
 *
 * The fork historically imported types from `template.core.base.designsystem.core.*`,
 * but the upstream KMP template's `dev` branch has renamed the design-system package
 * root from `template.` to `kpt.`. The 156-file consumer surface (feature and cmp/core modules)
 * hasn't been rewritten yet — so this shim re-exports every consumed symbol via
 * `typealias` from `kpt.core.base.designsystem.core.*`. Deletion tracker: after every
 * fork consumer is migrated to `import kpt.core.base.designsystem.core.X`, this file
 * can be removed. Do NOT add new symbols here — add them to the kpt.* source directly
 * and have the caller import that.
 */
@file:Suppress("PackageDirectoryMismatch")

package template.core.base.designsystem.core

@Deprecated(
    message = "Use kpt.core.base.designsystem.core.ComponentDsl",
    replaceWith = ReplaceWith("kpt.core.base.designsystem.core.ComponentDsl"),
)
typealias ComponentDsl = kpt.core.base.designsystem.core.ComponentDsl

@Deprecated(
    message = "Use kpt.core.base.designsystem.core.KptColorScheme",
    replaceWith = ReplaceWith("kpt.core.base.designsystem.core.KptColorScheme"),
)
typealias KptColorScheme = kpt.core.base.designsystem.core.KptColorScheme

@Deprecated(
    message = "Use kpt.core.base.designsystem.core.KptElevation",
    replaceWith = ReplaceWith("kpt.core.base.designsystem.core.KptElevation"),
)
typealias KptElevation = kpt.core.base.designsystem.core.KptElevation

@Deprecated(
    message = "Use kpt.core.base.designsystem.core.KptShapes",
    replaceWith = ReplaceWith("kpt.core.base.designsystem.core.KptShapes"),
)
typealias KptShapes = kpt.core.base.designsystem.core.KptShapes

@Deprecated(
    message = "Use kpt.core.base.designsystem.core.KptSpacing",
    replaceWith = ReplaceWith("kpt.core.base.designsystem.core.KptSpacing"),
)
typealias KptSpacing = kpt.core.base.designsystem.core.KptSpacing

@Deprecated(
    message = "Use kpt.core.base.designsystem.core.KptThemeProvider",
    replaceWith = ReplaceWith("kpt.core.base.designsystem.core.KptThemeProvider"),
)
typealias KptThemeProvider = kpt.core.base.designsystem.core.KptThemeProvider

@Deprecated(
    message = "Use kpt.core.base.designsystem.core.KptTopAppBarConfiguration",
    replaceWith = ReplaceWith("kpt.core.base.designsystem.core.KptTopAppBarConfiguration"),
)
typealias KptTopAppBarConfiguration = kpt.core.base.designsystem.core.KptTopAppBarConfiguration

@Deprecated(
    message = "Use kpt.core.base.designsystem.core.KptTypography",
    replaceWith = ReplaceWith("kpt.core.base.designsystem.core.KptTypography"),
)
typealias KptTypography = kpt.core.base.designsystem.core.KptTypography

@Deprecated(
    message = "Use kpt.core.base.designsystem.core.TopAppBarAction",
    replaceWith = ReplaceWith("kpt.core.base.designsystem.core.TopAppBarAction"),
)
typealias TopAppBarAction = kpt.core.base.designsystem.core.TopAppBarAction

@Deprecated(
    message = "Use kpt.core.base.designsystem.core.TopAppBarVariant",
    replaceWith = ReplaceWith("kpt.core.base.designsystem.core.TopAppBarVariant"),
)
typealias TopAppBarVariant = kpt.core.base.designsystem.core.TopAppBarVariant
