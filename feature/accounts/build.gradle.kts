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
    alias(libs.plugins.mifospay.cmp.feature)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.dokka)
}

android {
    namespace = "org.mifospay.feature.accounts"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}
// Configure Dokka
tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
    // Output format (HTML by default)
    outputDirectory.set(layout.buildDirectory.dir("dokka"))

    // Module name in documentation
    moduleName.set("feature")

    // Documentation format
    dokkaSourceSets {
        configureEach {
            sourceLink {
                localDirectory.set(file("src"))
                remoteUrl.set(uri("https://github.com/openMF/mobile-wallet.git").toURL())
                remoteLineSuffix.set("#L")
            }
        }
    }
}