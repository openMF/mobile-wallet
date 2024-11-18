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

    jvmToolchain(17)

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

fun String.formatToValidVersion(): String {
    // Remove any text after '-' or '+'
    val cleanVersion = this.split(Regex("[-+]")).first()

    // Split version numbers
    val parts = cleanVersion.split(".")

    return when {
        // If starts with 0, change to 1
        parts[0] == "0" -> {
            val newParts = parts.toMutableList()
            newParts[0] = "1"
            // Take only up to 3 parts (MAJOR.MINOR.PATCH)
            newParts.take(3).joinToString(".")
        }
        // If valid, take only up to 3 parts
        else -> parts.take(3).joinToString(".")
    }
}

val Project.dynamicVersion
    get() = project.version.toString().formatToValidVersion()

val productName = "MifosWallet"
val productNameSpace = "org.mifos.pay"

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Exe, TargetFormat.Deb)
            packageName = productName
            packageVersion = project.dynamicVersion
            description = "Mifos Wallet Desktop Application"
            copyright = "© 2024 Mifos Initiative. All rights reserved."
            vendor = "Mifos Initiative"
            licenseFile.set(project.file("../LICENSE"))
            includeAllModules = true

            macOS {
                bundleID = productNameSpace
                dockName = productName
                iconFile.set(project.file("icons/ic_launcher.icns"))
                notarization {
                    val providers = project.providers
                    appleID.set(providers.environmentVariable("NOTARIZATION_APPLE_ID"))
                    password.set(providers.environmentVariable("NOTARIZATION_PASSWORD"))
                    teamID.set(providers.environmentVariable("NOTARIZATION_TEAM_ID"))
                }
            }

            windows {
                menuGroup = productName
                shortcut = true
                dirChooser = true
                perUserInstall = true
                iconFile.set(project.file("icons/ic_launcher.ico"))
            }

            linux {
                modules("jdk.security.auth")
                iconFile.set(project.file("icons/ic_launcher.png"))
            }
        }
        buildTypes.release.proguard {
            configurationFiles.from(file("compose-desktop.pro"))
            obfuscate.set(true)
            optimize.set(true)
        }
    }
}
