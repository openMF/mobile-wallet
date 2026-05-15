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
    alias(libs.plugins.cmp.feature.convention)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlinCocoapods)
}

kotlin {
    iosArm64()
    iosSimulatorArm64()
    iosX64()

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.domain)
            implementation(projects.coreBase.ui)
            api(projects.core.data)
            api(projects.core.network)
            //put your multiplatform dependencies here
            implementation(compose.material3)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.components.resources)
            implementation(libs.window.size)

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
            implementation(projects.feature.beneficiary)
            implementation(projects.feature.invoices)
            implementation(projects.feature.kyc)
            implementation(projects.feature.notification)
            implementation(projects.feature.savedcards)
            implementation(projects.feature.receipt)
            implementation(projects.feature.standingInstruction)
            implementation(projects.feature.transferIntrabank)
            implementation(projects.feature.transferInterbank)
            implementation(projects.feature.mpayQr)
            implementation(projects.feature.mpayQrScan)
            implementation(projects.feature.fastMpay)
            implementation(projects.feature.merchants)
            implementation(projects.feature.upiSetup)
            implementation(projects.feature.onboardingLanguage)
            implementation(projects.feature.passcode)
        }

        desktopMain.dependencies {
            // Desktop specific dependencies
            implementation(compose.desktop.currentOs)
            implementation(compose.desktop.common)
        }
    }

    cocoapods {
        summary = "KMP Shared Module"
        homepage = "https://github.com/openMF/mobile-wallet"
        version = "1.0"
        ios.deploymentTarget = "16.0"
        podfile = project.file("../cmp-ios/Podfile")

        framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
}

android {
    namespace = "org.mifospay.shared"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

compose.resources {
    publicResClass = true
    generateResClass = always
}