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
    alias(libs.plugins.kotlin.parcelize)
    id("kotlinx-serialization")
}

android {
    namespace = "org.mifospay.core.model"
}

dependencies {
    api(libs.kotlinx.datetime)

    // For Serialized name
    implementation(libs.squareup.retrofit.converter.gson)
    implementation(libs.kotlinx.serialization.json)
}
