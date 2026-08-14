/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
plugins {
    alias(libs.plugins.cmp.feature.convention)
    alias(libs.plugins.kotlin.serialization)
}

// namespace auto-derives from baseNamespace + module path via kmp.library.convention.
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.coreBase.ui)
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(projects.core.designsystem)
            implementation(libs.ui.backhandler)
        }

        androidMain.dependencies {
            implementation(libs.google.play.services.code.scanner)
            implementation(libs.accompanist.permissions)
        }
    }
}

// Top-level `dependencies { debugImplementation(compose.uiTooling) }` is not valid on
// AGP-9 KMP-library modules — dropped. UI tooling is already available in preview via
// compose.components.uiToolingPreview in commonMain.