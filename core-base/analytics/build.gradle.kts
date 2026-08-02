/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
plugins {
    alias(libs.plugins.kmp.core.base.library.convention)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

// Firebase BOM: top-level api scope so version constraint propagates to all consumers (KT-58759).
dependencies {
    add("androidMainApi", platform(libs.firebase.bom))
}

kotlin {
    sourceSets {
        // firebaseMain — platforms with native GitLive Firebase SDK support
        // Covers: Android, iOS (iosArm64 / iosSimulatorArm64), JS
        // Note: firebase-crashlytics has no JS target — keep this source set analytics-only.
        val firebaseMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                api(libs.gitlive.firebase.analytics)
            }
        }
        // nonFirebaseMain — platforms without native Firebase SDK (desktop JVM, wasmJS)
        val nonFirebaseMain by creating {
            dependsOn(commonMain.get())
        }

        // Wire each target to the appropriate tier
        androidMain.get().dependsOn(firebaseMain)
        nativeMain.get().dependsOn(firebaseMain)       // iOS (iosArm64, iosSimulatorArm64)
        jsMain.get().dependsOn(firebaseMain)
        getByName("desktopMain").dependsOn(nonFirebaseMain)
        wasmJsMain.get().dependsOn(nonFirebaseMain)

        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(libs.kermit.logging)
            implementation(libs.kotlinx.datetime)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}
