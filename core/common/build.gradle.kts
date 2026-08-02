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
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

// core/common has no @Composable definitions, only references StringResource + suspend
// getString(...) from compose-resources. Ensure the compose runtime is on the classpath
// so the compose compiler plugin's classpath check passes.

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            api(libs.kermit.logging)
            api(libs.kotlinx.datetime)

            // ktor http types used by DataState / SafeApiCall (ClientRequestException,
            // ServerResponseException, bodyAsText); also brings kotlinx-io transitively
            // for kotlinx.io.IOException.
            implementation(libs.ktor.client.core)

            // ImmutableList / persistent collections used by DateAsStringSerializer.
            implementation(libs.kotlinx.collections.immutable)

            // SavedStateHandle in commonMain (androidx.lifecycle package, KMP artifact).
            implementation(libs.jb.lifecycleViewmodelSavedState)

            // Compose Multiplatform resources: StringResource + suspend getString(...)
            // used by StringProvider / StringResourceSerializer / DialogManager.
            implementation(compose.runtime)
            implementation(compose.components.resources)
        }
    }
}