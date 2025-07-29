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
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "org.mifospay.common"
}

kotlin {

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            isStatic = false
            export(libs.kermit.simple)
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            api(libs.coil.kt)
            api(libs.coil.core)
            api(libs.coil.svg)
            api(libs.coil.network.ktor)
            api(libs.kermit.logging)
            api(libs.squareup.okio)
            api(libs.jb.kotlin.stdlib)
            api(libs.kotlinx.datetime)
            implementation(compose.components.resources)
            implementation(libs.jb.composeRuntime)
            implementation(libs.jb.lifecycleViewmodelSavedState)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.collections.immutable)
        }

        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.android)
        }
        commonTest.dependencies {
            implementation(libs.kotlinx.coroutines.test)
        }
        iosMain.dependencies {
            api(libs.kermit.simple)
        }
        desktopMain.dependencies {
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.kotlin.reflect)
        }
        jsMain.dependencies {
            api(libs.jb.kotlin.stdlib.js)
            api(libs.jb.kotlin.dom)
        }
    }
}