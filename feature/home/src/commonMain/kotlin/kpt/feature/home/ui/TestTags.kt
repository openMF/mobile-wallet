/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.home.ui

/**
 * Test-tag registry for the home / bottom-nav dashboard.
 *
 * Consumed by:
 *  - `HomeScreen` via `Modifier.testTag(...)`.
 *  - Maestro on-device flow `maestro/screen-state/tab-switch.yaml` — references
 *    the `home_dashboard_scroll` string constant directly.
 */
object TestTags {

    /** Tags for `HomeScreen` (the bottom-nav home dashboard). */
    object Home {
        /**
         * Root [Scaffold] surface of `HomeScreen` — always rendered regardless
         * of dashboard loading state. Stable target for Compose UI tests.
         */
        const val SCREEN: String = "home.screen"

        /** The vertically-scrollable dashboard `Column`. */
        const val DASHBOARD_SCROLL: String = "home_dashboard_scroll"
    }
}
