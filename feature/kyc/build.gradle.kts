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
    namespace = "org.mifospay.kyc"
}

dependencies {
    implementation(projects.core.data)
    implementation(libs.compose.material)
    implementation(libs.sheets.compose.dialogs.core)
    implementation(libs.sheets.compose.dialogs.calender)
    implementation(libs.compose.country.code.picker)
    // TODO:: this should be removed
    implementation(libs.squareup.okhttp)
}