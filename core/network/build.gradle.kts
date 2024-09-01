import com.android.build.api.dsl.Packaging

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
    fun Packaging.() {
        exclude("META-INF/kotlinx-io.kotlin_module")
        exclude ("META-INF/atomicfu.kotlin_module")
        exclude("META-INF/kotlinx-coroutines-io.kotlin_module")
        exclude ("META-INF/kotlinx-coroutines-core.kotlin_module")
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

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
    implementation("io.ktor:ktor-client-core:2.3.4")
    implementation("io.ktor:ktor-client-android:2.3.4")
    implementation("io.ktor:ktor-client-serialization:2.3.4")
    implementation("io.ktor:ktor-client-logging:2.3.4")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.4")
    implementation("io.ktor:ktor-client-json:2.3.4")
    implementation("io.ktor:ktor-client-websockets:2.3.4")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.4")

    testImplementation(libs.kotlinx.coroutines.test)
}
