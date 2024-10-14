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
    alias(libs.plugins.mifospay.kmp.library)
    alias(libs.plugins.mifospay.cmp.feature)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlin.parcelize)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.domain)
            api(projects.core.data)
            api(projects.core.network)
            //put your multiplatform dependencies here
            api(compose.material3)
            api(compose.foundation)
            api(compose.ui)
            api(compose.components.uiToolingPreview)
            api(compose.components.resources)
            api(libs.window.size)
            api(libs.koin.core)
            api(libs.koin.compose)
            api(libs.koin.compose.viewmodel)

            api(projects.feature.auth)
            api(projects.libs.mifosPasscode)
            api(projects.feature.home)
            api(projects.feature.settings)
            api(projects.feature.faq)
            api(projects.feature.editpassword)
            api(projects.feature.profile)
            api(projects.feature.history)
            api(projects.feature.payments)
        }

        desktopMain.dependencies {
            // Desktop specific dependencies
            implementation(compose.desktop.currentOs)
            implementation(compose.desktop.common)
        }
    }
}

android {
    namespace = "org.mifospay.shared"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

compose.resources {
    publicResClass = true
    generateResClass = always
}