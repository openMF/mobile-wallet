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
    // Compose runtime + resources: core/data uses `org.jetbrains.compose.resources.Res.readBytes(...)`
    // in AssetRepositoryImpl to load bundled `files/countries.json` from
    // core/data/src/commonMain/composeResources/. Requires the compose gradle plugin +
    // compose-compiler on the classpath even though this module declares no @Composable.
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
            // NOTE (fork-local elevation, candidate upstream fix per
            // RULE-TEMPLATE-MODULE-FIX-UPSTREAM-001): every feature module that
            // depends on core.data ALSO transitively exposes types from core.common
            // (ScreenState, DataState, ErrorMapper), core.datastore
            // (UserPreferencesRepository), and core.model (Client, Account, etc.) via
            // its repository interfaces. Under `implementation` these three don't leak
            // to feature consumers → every feature has to re-declare them → 25 build.gradle
            // edits vs. one. Elevated to `api` here so feature/* build.gradle files stay
            // minimal (the CMP-feature convention plugin adds core.data, and this pulls
            // the trio along). Template equivalent should also elevate for the same reason.
            api(projects.core.common)
            implementation(projects.core.database)
            api(projects.core.datastore)
            api(projects.core.model)
            // core.network is also elevated to api: features/auth, autopay, fast-mpay,
            // transfer-{interbank,intrabank} reference org.mifospay.core.network types
            // (InstanceConfigManager, Page, CommonResponse) directly. Elevation keeps
            // per-feature build.gradle files minimal (same rationale as core.common et al).
            api(projects.core.network)
            implementation(projects.core.analytics)

            implementation(projects.coreBase.common)
            implementation(projects.coreBase.network)
            // coreBase.database: source uses kpt.core.base.database.invalidation.{daoFlow,notifyingWrite}.
            // (Also transitively via api(projects.core.database)->api(projects.coreBase.database); declared
            // explicitly for parity with the source-what-you-use pattern in core:common / core:network.)
            implementation(projects.coreBase.database)
            api(projects.core.store)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            api(libs.cmp.network.monitor)

            // Kermit: co.touchlab.kermit.Logger is used directly in
            // network monitor / store adapters.
            implementation(libs.kermit.logging)

            // multiplatform-settings: com.russhwolf.settings.Settings is referenced by the
            // fork's cache invalidation + user-preference adapters.
            implementation(libs.multiplatform.settings)

            // Ktor client: io.ktor.client.plugins.{ClientRequestException,ServerResponseException},
            // io.ktor.client.request.forms.{formData,MultiPartFormDataContent},
            // io.ktor.client.statement.bodyAsText, io.ktor.http.*, io.ktor.util.{encodeBase64,decodeBase64String}
            // — all resolve through ktor-client-core (which api-exposes ktor-http + ktor-utils).
            implementation(libs.ktor.client.core)

            // Koin core: org.koin.core.{module.Module, qualifier.*}, org.koin.dsl.{bind,module}, singleOf.
            implementation(libs.koin.core)

            // Compose runtime + resources: Res.readBytes("files/countries.json") in AssetRepositoryImpl.
            implementation(compose.runtime)
            implementation(compose.components.resources)

            // Fork-specific: authenticator SDK (passcode + biometrics adapters)
            api(libs.mifos.authenticator.passcode)
            api(libs.mifos.authenticator.biometrics)
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

// Generated compose-resources class package. AssetRepositoryImpl imports
// `mobile_wallet.core.data.generated.resources.Res` — mirror the module-scoped naming
// convention established by core/network (`mobile_wallet.core.network.generated.resources`).
compose.resources {
    publicResClass = true
    generateResClass = always
    packageOfResClass = "mobile_wallet.core.data.generated.resources"
}
