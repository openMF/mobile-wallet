/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    alias(libs.plugins.cmp.feature.convention)
    alias(libs.plugins.mokkery)
}

android {
    namespace = "org.mifospay.feature.auth"
    buildFeatures {
        buildConfig = true
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.domain)
            implementation(projects.coreBase.ui)
            implementation(projects.coreBase.platform)
            implementation(compose.material3)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.jb.kotlin.stdlib)
            implementation(libs.kotlin.reflect)
            implementation(libs.mifos.authenticator.biometrics)
            implementation(libs.mifos.authenticator.passcode)
        }

        androidMain.dependencies {
            // Credentials Manager
            implementation(libs.androidx.credentials)
            // optional - needed for credentials support from play services, for devices running
            // Android 13 and below.
            implementation(libs.androidx.credentials.play.services.auth)
            implementation(libs.googleid)

            implementation(libs.play.services.auth)
        }

        commonTest.dependencies {
            implementation(libs.turbine)
        }
    }
}