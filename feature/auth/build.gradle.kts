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
    alias(libs.plugins.mifospay.cmp.feature)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
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
            implementation(compose.material3)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose)
            implementation(libs.jb.kotlin.stdlib)
            implementation(libs.kotlin.reflect)
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
    }
}