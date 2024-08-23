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
    id("kotlinx-serialization")
}

android {
    namespace = "org.mifospay.core.network"
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
    api(projects.core.common)
    api(projects.core.model)
    api(projects.core.datastore)

    implementation(libs.kotlinx.serialization.json)
    
    implementation(libs.squareup.okhttp)
    implementation(libs.squareup.logging.interceptor)

    implementation(libs.squareup.retrofit2)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.squareup.retrofit.adapter.rxjava)
    implementation(libs.squareup.retrofit.converter.gson)

    implementation(libs.reactivex.rxjava.android)
    implementation(libs.reactivex.rxjava)

    testImplementation(libs.kotlinx.coroutines.test)
}
