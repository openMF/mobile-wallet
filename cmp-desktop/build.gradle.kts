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
import org.gradle.internal.os.OperatingSystem

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvm("desktop")

    jvmToolchain(21)

    sourceSets {
        val desktopMain by getting {
            dependencies {
                implementation(projects.core.common)
                implementation(projects.core.data)
                implementation(projects.core.model)
                implementation(projects.core.datastore)

                implementation(projects.cmpShared)

                implementation(libs.kotlinx.coroutines.swing)
                implementation(compose.desktop.currentOs)
                implementation(libs.jb.kotlin.stdlib)
                implementation(libs.kotlin.reflect)
                implementation(libs.filekit.core)
            }
        }
    }
}

val appPackageName: String = libs.versions.packageName.get()
val appPackageNameSpace: String = libs.versions.packageNamespace.get()
val appPackageVersion: String = libs.versions.packageVersion.get()

compose.desktop {
    application {
        mainClass = "MainKt"
        val buildNumber: String = (project.findProperty("buildNumber") as String?) ?: "1"
        val isAppStoreRelease: Boolean =
            (project.findProperty("macOsAppStoreRelease") as String?)?.toBoolean() ?: false

        nativeDistributions {
            targetFormats(
                TargetFormat.Pkg,
                TargetFormat.Dmg,
                TargetFormat.Msi,
                TargetFormat.Exe,
                TargetFormat.Deb
            )
            packageName = appPackageName
            packageVersion = appPackageVersion
            description = "Mifos Wallet Desktop Application"
            copyright = "© 2024 Mifos Initiative. All rights reserved."
            vendor = "Mifos Initiative"
            licenseFile.set(project.file("../LICENSE"))
            includeAllModules = true
            outputBaseDir.set(project.layout.buildDirectory.dir("release"))

            macOS {
                bundleID = appPackageNameSpace
                dockName = appPackageName
                iconFile.set(project.file("icons/ic_launcher.icns"))
                minimumSystemVersion = "12.0"
                appStore = isAppStoreRelease

                infoPlist {
                    packageBuildVersion = buildNumber
                    extraKeysRawXml = """
                        <key>ITSAppUsesNonExemptEncryption</key>
                        <false/>
                    """.trimIndent()
                }

                if (isAppStoreRelease) {
                    signing {
                        sign.set(true)
                        identity.set("The Mifos Initiative")
                    }

                    provisioningProfile.set(project.file("embedded.provisionprofile"))
                    runtimeProvisioningProfile.set(project.file("runtime.provisionprofile"))

                    entitlementsFile.set(project.file("entitlements.plist"))
                    runtimeEntitlementsFile.set(project.file("runtime-entitlements.plist"))
                } else {
                    notarization {
                        val providers = project.providers
                        appleID.set(providers.environmentVariable("NOTARIZATION_APPLE_ID"))
                        password.set(providers.environmentVariable("NOTARIZATION_PASSWORD"))
                        teamID.set(providers.environmentVariable("NOTARIZATION_TEAM_ID"))
                    }
                }
            }

            windows {
                menuGroup = appPackageName
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
            isEnabled = false
//            configurationFiles.from(file("compose-desktop.pro"))
//            obfuscate.set(true)
//            optimize.set(true)
        }
    }
}

/**
 * Removes the `com.apple.quarantine` extended attribute from the built `.app`.
 *
 * Why:
 * macOS Gatekeeper marks files that originated from the Internet with the
 * `com.apple.quarantine` xattr. During a Compose Desktop / jpackage build the
 * JBR/JRE, native libs, and other resources are copied into the `.app`. If any
 * of those files are quarantined, Appstore will not allow it..
 *
 * What this does:
 * Runs `xattr -dr com.apple.quarantine <App.app>` **recursively** on the
 * distributable after it has been assembled but before signing/packaging.
 * We depend on `createReleaseDistributable` so the `.app` exists, and we guard
 * execution to only run on macOS hosts.
 */
val unquarantineApp = tasks.register<Exec>("unquarantineMacApp") {
    group = "macOS"
    description = "Remove com.apple.quarantine from the built .app before signing"
    onlyIf { OperatingSystem.current().isMacOsX }

    // Ensure the .app bundle exists first
    dependsOn("createReleaseDistributable")

    // build/release/main-release/app/<YourApp>.app
    val appName = "$appPackageName.app"
    val appPath = layout.buildDirectory
        .dir("release/main-release/app/$appName")
        .map { it.asFile.absolutePath }

    commandLine("xattr", "-dr", "com.apple.quarantine", appPath.get())
}

/**
 * Ensure un-quarantining always happens before we create the signed PKG.
 */
tasks.matching { it.name == "packageReleasePkg" }.configureEach {
    dependsOn(unquarantineApp)
}

