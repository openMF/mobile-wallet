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
        androidMain.dependencies {
            api(libs.androidx.metrics)
            implementation(libs.androidx.browser)
            implementation(libs.androidx.compose.runtime)
            implementation(compose.uiTooling)
        }

        commonMain.dependencies {
            implementation(projects.core.analytics)
            implementation(projects.core.designsystem)
            implementation(projects.core.model)
            implementation(projects.core.common)
            // For rememberKptPullToRefreshState(pagingStream) bridge — observes
            // ScreenState freshness + calls pagingStream.refresh() on pull.
            implementation(projects.coreBase.store)
            implementation(libs.jb.composeViewmodel)
            implementation(libs.jb.lifecycleViewmodel)
            implementation(libs.jb.lifecycleViewmodelSavedState)
            implementation(libs.coil.kt)
            implementation(libs.coil.kt.compose)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.jb.composeNavigation)
            implementation(libs.filekit.compose)
            implementation(libs.filekit.core)

            // Fork-specific: MifosProgressIndicator uses Compottie (Lottie for CMP)
            // for progress/success/failure animations distinct from the Store5 core-base
            // default set. PasswordStrengthIndicator uses M3 material-icons-extended
            // (Icons.filled.CheckCircle, Icons.filled.Close).
            implementation(libs.compottie)
            implementation(libs.compottie.resources)
            implementation(compose.materialIconsExtended)
        }
        androidInstrumentedTest.dependencies {
            implementation(libs.bundles.androidx.compose.ui.test)
        }
    }
}

compose.resources {
    publicResClass = true
    generateResClass = always
    packageOfResClass = "kpt.core.ui.generated.resources"
}