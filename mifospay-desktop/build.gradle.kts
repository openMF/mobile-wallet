/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvm("desktop") {
        withJava()
    }

    jvmToolchain(21)

    sourceSets {
        val desktopMain by getting {
            dependencies {
                implementation(projects.core.common)
                implementation(projects.core.data)
                implementation(projects.core.model)
                implementation(projects.core.datastore)

                implementation(projects.mifospayShared)

                implementation(libs.kotlinx.coroutines.swing)
                implementation(compose.desktop.currentOs)
                implementation(libs.jb.kotlin.stdlib)
                implementation(libs.kotlin.reflect)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.mifospay.desktop"
            packageVersion = "1.0.0"
            windows {
                // a version for all Windows distributables
                packageVersion = "1.0.0"
                // a version only for the msi package
                msiPackageVersion = "1.0.0"
                // a version only for the exe package
                exePackageVersion = "1.0.0"
                menu = true
            }
        }
    }
}