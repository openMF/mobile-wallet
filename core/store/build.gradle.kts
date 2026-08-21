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
    alias(libs.plugins.kmp.koin.convention)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Foundation: state model + UI defaults the seam customizes.
            // `api` so consumers get LocalScreenStateDefaults, ScreenState*, etc.
            // by depending on `core/store` alone.
            api(projects.coreBase.store)
            api(projects.coreBase.ui)

            // Compose runtime — needed for the @Composable appScreenStateDefaults() factory.
            implementation(compose.runtime)
            // compose-resources — for stringResource()-based ScreenState copy (i18n).
            implementation(compose.components.resources)

            implementation(projects.core.database)
            implementation(projects.coreBase.database)
            implementation(projects.core.model)
            implementation(projects.core.network)
            implementation(libs.cmp.network.monitor)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kermit.logging)
        }
    }
}

compose.resources {
    publicResClass = true
    generateResClass = always
    packageOfResClass = "kpt.core.store.generated.resources"
}
