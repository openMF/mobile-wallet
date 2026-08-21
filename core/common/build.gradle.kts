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
    // Fork addition: StringResourceSerializer + DialogManager/DialogMessage need the `compose`
    // extension (compose.components.resources) for org.jetbrains.compose.resources.StringResource.
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            // Fork addition: applying the compose-compiler plugin (below) requires the Compose
            // Runtime on the classpath for every target it compiles, including androidMain — the
            // multiplatform `compose.runtime` accessor (not the versionless `libs.androidx.compose.
            // runtime` catalog alias, which relies on a BOM/transitive constraint this module has
            // none of) resolves a real pinned version on its own, matching core/designsystem,
            // core/firebase, core/analytics, core/store et al.
            implementation(compose.runtime)
            // Fork addition: StringResourceSerializer + DialogManager/DialogMessage (dialogManager/)
            // resolve resource-id strings via org.jetbrains.compose.resources.StringResource.
            implementation(compose.components.resources)
            // Fork addition: DateAsStringSerializer's ImmutableListSerializer serializes
            // ImmutableList<String> (toPersistentList()).
            implementation(libs.kotlinx.collections.immutable)
            // Fork addition: SavedStateHandleExtensions (getSerialized/setSerialized) needs the KMP
            // SavedStateHandle type — same alias core/ui already uses for the same purpose.
            implementation(libs.jb.lifecycleViewmodelSavedState)
            // Fork addition: AppErrorMapper maps Ktor exceptions (ClientRequestException /
            // ServerResponseException / kotlinx.io.IOException, transitively brought in by ktor-io).
            implementation(libs.ktor.client.core)
            api(libs.kermit.logging)
            api(libs.kotlinx.datetime)
            // Re-export core-base/common (CommonModule DI, base utilities) so app-shell + feature
            // modules depend on core/common, never core-base/common directly (encapsulation, Phase A).
            api(projects.coreBase.common)
        }
    }
}