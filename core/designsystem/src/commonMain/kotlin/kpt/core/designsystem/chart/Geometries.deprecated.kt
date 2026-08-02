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

package kpt.core.designsystem.chart

/**
 * Back-compat shims — pure-math geometry helpers were promoted to
 * `core-base/designsystem/chart` (brand-neutral names already).
 *
 * `ChartTokens` was NOT promoted in this task because it depends on
 * `MaterialTheme.finance` (declared in `core/designsystem/theme/FinanceColors.kt`)
 * which would create a backward dependency from `core-base` to `core`. Promoting
 * `ChartTokens` requires either (a) promoting `MaterialTheme.finance` to
 * `core-base/designsystem` first or (b) splitting `ChartTokens` into a pure-token
 * layer (core-base) plus a finance-aware composition layer (core). Either path is
 * its own task.
 *
 * These typealiases keep `import kpt.core.designsystem.chart.{SparklineGeometry,
 * DonutGeometry, BarGeometry}` compiling for one release. New code should import
 * from `kpt.core.base.designsystem.chart`.
 *
 * The promoted versions are `public` (was `internal` in the legacy package) so they
 * are accessible across modules via the typealias. The typealiases themselves are
 * `internal` to preserve the original visibility within `core/designsystem`.
 */

@Deprecated(
    "Promoted to core-base/designsystem/chart; import kpt.core.base.designsystem.chart.SparklineGeometry",
    ReplaceWith(
        "SparklineGeometry",
        "kpt.core.base.designsystem.chart.SparklineGeometry",
    ),
    level = DeprecationLevel.WARNING,
)
internal typealias SparklineGeometry = kpt.core.base.designsystem.chart.SparklineGeometry

@Deprecated(
    "Promoted to core-base/designsystem/chart; import kpt.core.base.designsystem.chart.DonutGeometry",
    ReplaceWith(
        "DonutGeometry",
        "kpt.core.base.designsystem.chart.DonutGeometry",
    ),
    level = DeprecationLevel.WARNING,
)
internal typealias DonutGeometry = kpt.core.base.designsystem.chart.DonutGeometry

@Deprecated(
    "Promoted to core-base/designsystem/chart; import kpt.core.base.designsystem.chart.BarGeometry",
    ReplaceWith(
        "BarGeometry",
        "kpt.core.base.designsystem.chart.BarGeometry",
    ),
    level = DeprecationLevel.WARNING,
)
internal typealias BarGeometry = kpt.core.base.designsystem.chart.BarGeometry
