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
    alias(libs.plugins.mifospay.kmp.library)
    alias(libs.plugins.mifospay.cmp.feature)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlin.parcelize)
}

kotlin {
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            optimized = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.domain)
            api(projects.core.data)
            api(projects.core.network)
            //put your multiplatform dependencies here
            implementation(compose.material3)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.components.resources)
            implementation(libs.window.size)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(projects.feature.auth)
            implementation(projects.libs.mifosPasscode)
            implementation(projects.feature.home)
            implementation(projects.feature.settings)
            implementation(projects.feature.faq)
            implementation(projects.feature.editpassword)
            implementation(projects.feature.profile)
            implementation(projects.feature.history)
            implementation(projects.feature.payments)
            implementation(projects.feature.finance)
            implementation(projects.feature.accounts)
            implementation(projects.feature.invoices)
            implementation(projects.feature.kyc)
            implementation(projects.feature.notification)
            implementation(projects.feature.savedcards)
            implementation(projects.feature.receipt)
            implementation(projects.feature.standingInstruction)
            implementation(projects.feature.requestMoney)
            implementation(projects.feature.sendMoney)
            implementation(projects.feature.makeTransfer)
            implementation(projects.feature.qr)
            implementation(projects.feature.merchants)
            implementation(projects.feature.upiSetup)
        }

        desktopMain.dependencies {
            // Desktop specific dependencies
            implementation(compose.desktop.currentOs)
            implementation(compose.desktop.common)
        }
    }
}

android {
    namespace = "org.mifospay.shared"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

compose.resources {
    publicResClass = true
    generateResClass = always
}