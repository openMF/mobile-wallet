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
    alias(libs.plugins.mifospay.android.library.compose)
}

android {
    namespace = "org.mifospay.core.ui"
    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    api(projects.core.designsystem)
    api(projects.core.model)
    api(projects.core.common)
    api(libs.androidx.metrics)
    api(projects.core.analytics)

    implementation(libs.accompanist.pager)
    implementation(libs.androidx.browser)
    implementation(libs.coil.kt)
    implementation(libs.coil.kt.compose)
}