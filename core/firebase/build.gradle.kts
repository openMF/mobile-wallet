/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
plugins {
    alias(libs.plugins.kmp.library.convention)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // core/firebase = project layer over the core-base/firebase host (cmp-firebase engine).
            // Keeps the Kpt-prefixed domain event catalog (KptAnalyticsEvents / KptAnalyticsTracker /
            // KptAnalyticsExtensions), rewired onto the library's AnalyticsHelper.
            api(projects.coreBase.firebase)
            implementation(compose.runtime)
            implementation(compose.material3)
            implementation(compose.ui)
        }
    }
}
