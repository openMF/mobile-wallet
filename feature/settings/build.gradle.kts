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

            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose)

            // For BiometricErrorMessages / BiometricPromptStrings used by
            // SettingsViewModel's enable-biometrics path (v2.3.0-beta requires
            // caller-supplied prompt strings + BiometricError mapping).
            implementation(projects.feature.passcode)
            implementation(libs.mifos.authenticator.biometrics)
        }
    }
}