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
    alias(libs.plugins.kotlin.serialization)
}

// namespace auto-derives from baseNamespace + module path via kmp.library.convention;
// consumer-rules.pro is auto-registered by the convention plugin when the file exists.
kotlin {
    sourceSets {
        commonMain.dependencies {
            // NOTE: core.common / core.datastore / core.model are transitively provided
            // via CMP-feature convention → core.data → api() elevation (see core/data/
            // build.gradle.kts fork-local elevation comment). Explicit lines dropped.
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            // Template idiom: ScreenDataStream (core-base/store) + ScreenContent
            // (core-base/ui) so the screen consumes the store's pre-decided
            // ScreenState directly instead of a fork-ScreenState fold.
            implementation(projects.coreBase.store)
            implementation(projects.coreBase.ui)

            implementation(libs.coil.kt.compose)
            implementation(libs.filekit.core)
            implementation(libs.filekit.dialogs)
            implementation(libs.filekit.dialogs.compose)
        }
    }
}