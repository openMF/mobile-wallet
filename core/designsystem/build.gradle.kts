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
        androidInstrumentedTest.dependencies {
            implementation(libs.androidx.compose.ui.test)
        }
        androidUnitTest.dependencies {
            implementation(libs.androidx.compose.ui.test)
        }
        androidMain.dependencies {
            // Fork addition: PermissionBox.kt needs the Activity permission-request APIs
            // (ContextCompat/ActivityCompat/rememberLauncherForActivityResult).
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            api(projects.coreBase.designsystem)
            // Theme wires LocalScreenStateDefaults from core/store so every screen
            // wrapped by KptTheme picks up the app's branded ScreenState defaults.
            implementation(projects.core.store)

            implementation(compose.ui)
            implementation(compose.uiUtil)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.coil.kt.compose)

            // Fork addition: BottomSheet.kt's predictive-back handling needs arkivanov Essenty's
            // BackCallback.
            implementation(libs.back.handler)
            // Fork addition: MifosIcons.kt draws from the FluentUI icon set.
            implementation(libs.fluentui.system.icons)
        }
    }
}

compose.resources {
    publicResClass = true
    generateResClass = always
    packageOfResClass = "kpt.core.designsystem.generated.resources"
}