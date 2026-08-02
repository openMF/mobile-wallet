/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
plugins {
    alias(libs.plugins.cmp.feature.convention)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            // Template idiom: ScreenDataStream + ScreenState (core-base/store) and
            // ScreenContent (core-base/ui) so the pocket dashboard consumes the
            // store's pre-decided ScreenState directly instead of a fork fold.
            implementation(projects.coreBase.store)
            implementation(projects.coreBase.ui)
        }
    }
}
