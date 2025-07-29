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
    alias(libs.plugins.kmp.library.convention)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
}

android {
    namespace = "org.mifospay.core.data"
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.common)
            api(projects.core.datastore)
            api(projects.core.model)
            api(projects.core.network)
            implementation(projects.core.analytics)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.jb.composeRuntime)
            implementation(compose.components.resources)
        }

        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.tracing.ktx)
            implementation(libs.koin.android)
        }
    }
}