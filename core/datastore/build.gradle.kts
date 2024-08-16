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
    alias(libs.plugins.mifospay.android.library)
    alias(libs.plugins.mifospay.android.hilt)
}

apply(from = "${project.rootDir}/config/quality/quality.gradle")

android {
    namespace = "org.mifospay.core.datastore"
    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }
    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    api(libs.kotlinx.datetime)
    api(libs.androidx.dataStore.core)
    api(projects.core.datastoreProto)
    api(projects.core.common)
    api(projects.core.model)
    implementation(libs.squareup.retrofit.converter.gson)
}