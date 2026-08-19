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
    alias(libs.plugins.kmp.library.convention)
    alias(libs.plugins.kotlin.parcelize)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            api(libs.kermit.logging)
            api(libs.kotlinx.datetime)
            // Re-export core-base/common (CommonModule DI, base utilities) so app-shell + feature
            // modules depend on core/common, never core-base/common directly (encapsulation, Phase A).
            api(projects.coreBase.common)
        }
    }
}