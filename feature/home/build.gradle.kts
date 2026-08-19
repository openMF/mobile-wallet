/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
plugins {
    alias(libs.plugins.cmp.feature.convention)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Template SubmitHandler idiom (submitHandler / SubmitState) for the
            // MarkAsDefault one-shot write in HomeViewModel.
            implementation(projects.coreBase.store)
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            // HomeScreen uses androidx.compose.material3.windowsizeclass.* for
            // calculateWindowSizeClass()/WindowWidthSizeClass adaptive-layout branching.
            // The multi-platform port lives at dev.chrisbanes.material3:material3-window-size-class-multiplatform.
            implementation(libs.window.size)
        }
    }
}