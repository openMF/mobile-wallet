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
    alias(libs.plugins.kotlin.allopen)
}

android {
    namespace = "org.mifospay.core.domain"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.common)
            implementation(projects.core.data)
            implementation(projects.core.model)
        }
    }
}

// Only open classes annotated with @OpenForMokkery during test tasks
fun isTestingTask(name: String) = name.contains("test", ignoreCase = true)
val isTesting = gradle.startParameter.taskNames.any(::isTestingTask)

if (isTesting) {
    allOpen {
        annotation("org.mifospay.core.common.utils.OpenForMokkery")
    }
}