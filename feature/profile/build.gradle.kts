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
    alias(libs.plugins.mifospay.android.feature)
    alias(libs.plugins.mifospay.android.library.compose)
}

android {
    namespace = "org.mifospay.feature.profile"
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.libs.countryCodePicker)

    implementation(libs.qrkit)

    implementation(libs.squareup.okhttp)

    implementation(libs.coil.kt.compose)
}