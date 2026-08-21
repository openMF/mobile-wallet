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
    // Fork addition: AssetRepositoryImpl.kt's fallback (composeResources/files/countries.json)
    // needs the `compose` extension for org.jetbrains.compose.resources.ExperimentalResourceApi.
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

androidComponents {
    finalizeDsl { ext ->
        ext.withHostTest {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.common)
            implementation(projects.core.database)
            implementation(projects.coreBase.database)
            implementation(projects.coreBase.datastore)
            implementation(projects.coreBase.store)
            // api: re-export the relocated sync/monitor infra (NetworkMonitor, Synchronizer,
            // SyncManager, TimeZoneMonitor) so existing core/data consumers (features, sync,
            // cmp-android) keep the transitive visibility they had when it lived in core/data.
            api(projects.coreBase.data)
            // api: core/data is auto-wired into every feature module via cmp.feature.convention
            // (commonMainImplementation project(":core:data")) — re-exporting these means feature
            // modules that read domain types (org.mifospay.core.model.*), UserPreferencesRepository
            // (org.mifospay.core.datastore.*), or network DTOs (org.mifospay.core.network.model.*)
            // get them for free instead of every feature module repeating its own
            // implementation(projects.core.{model,datastore,network}) — features must never depend
            // on core/network directly; core/data is the sole consumer/re-exporter of it.
            api(projects.core.datastore)
            api(projects.core.model)
            api(projects.core.network)
            implementation(projects.core.firebase)

            implementation(projects.coreBase.common)
            implementation(projects.coreBase.network)
            api(projects.core.store)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            api(libs.cmp.network.monitor)

            // Fork addition: applying the compose-compiler plugin (above) requires the Compose
            // Runtime on the classpath — see core/common's build.gradle.kts for the same fix + rationale.
            implementation(compose.runtime)
            implementation(compose.components.resources)

            // Fork addition: BiometricsSetupAdapterImpl / MifosPasscodeAdapterImpl wrap the
            // mifos-authenticator biometrics/passcode storage adapters.
            implementation(libs.mifos.authenticator.biometrics)
            implementation(libs.mifos.authenticator.passcode)
        }

        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.tracing.ktx)
            implementation(libs.koin.android)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(libs.koin.test)
        }
    }
}
