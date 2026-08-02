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
    alias(libs.plugins.kmp.library.convention)
    alias(libs.plugins.cmp.feature.convention)
    alias(libs.plugins.kmp.koin.convention)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // Core Modules
            implementation(projects.core.data)
            implementation(projects.core.database)
            implementation(projects.core.model)
            implementation(projects.core.common)
            implementation(projects.core.datastore)

            implementation(projects.core.datastore)
            implementation(projects.coreBase.common)
            implementation(projects.coreBase.platform)
            implementation(projects.coreBase.security)

            // Phase 2 T4 (02-topology-reconciliation) — shell-security re-home:
            //   * :core:designsystem  → MifosDialogBox for UnauthorizedDialogGate (b)
            //   * mifos-authenticator-biometrics → PlatformAuthenticatorCompositionProvider (d)
            //
            // NB: no `projects.cmpShared` here — `cmp-shared` already depends on
            // `cmp-navigation` (SharedApp → ComposeApp), so the shell-VM /
            // instance-selector wiring goes the OTHER direction: `SharedApp`
            // (in cmp-shared) composes `MifosPayViewModel` + `InstanceSelectorScreen`
            // and passes them into `ComposeApp` via callbacks (see
            // `cmp/navigation/ComposeApp.kt` param docs).
            // `mifos-authenticator-passcode` (the library, not the feature module)
            // is pulled `api` by `:core:data`, so `PasscodeManager` /
            // `PasscodeStep` resolve transitively.
            implementation(projects.core.designsystem)
            implementation(libs.mifos.authenticator.biometrics)

            // Phase 2 T6/T7 — fork Koin surface required by the re-homed shell
            // (see `cmp.navigation.di.KoinModules`):
            //   * :core:network — org.mifospay.core.network.di.{NetworkModule,LocalModule}
            //     (Ktor client + InstanceConfigLoader used by LoginScreen's Supabase
            //     multi-instance path).
            //   * :core:domain — org.mifospay.core.domain.di.DomainModule
            //     (use-cases the fork's RepositoryModule depends on downstream).
            //   * :feature:passcode — org.mifos.feature.passcode.MifosAuthenticatorModule
            //     (MifosPasscodeViewModel + BiometricSetupScreenViewmodel). The
            //     PasscodeManager singleton itself is registered locally by
            //     `KoinModules.MifosPasscodeModule` (this cmp-navigation module)
            //     to avoid re-creating the cmp-shared → cmp-navigation cycle.
            implementation(projects.core.network)
            implementation(projects.core.domain)
            implementation(projects.feature.passcode)

            // shell (framework) — kept
            implementation(projects.feature.home)
            implementation(projects.feature.profile)
            implementation(projects.feature.settings)
            // demo feature deps removed — fork has no template demo modules (customizer --clean equivalent, deferred to CI)
            implementation(projects.sync)

            // put your multiplatform dependencies here
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.components.resources)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            // Phase 3 (store5-screen-state-persistence 03-vm-scoping) — enables
            // koinNavViewModel() so nav destinations acquire ViewModels scoped to
            // NavBackStackEntry (cleared on pop) instead of Activity (cleared on
            // Activity death). Resolves io.insert-koin:koin-compose-viewmodel-navigation
            // via gradle/libs.versions.toml:259; version is the shared Koin ref.
            implementation(libs.koin.compose.navigation)
            // Provides `com.russhwolf.settings.Settings` referenced by
            // `saveable/PersistentSaveableStateRegistry.kt` at the app root.
            // The `named("plain")` binding itself is contributed by
            // `core-base/datastore/DatastoreBaseModule` (transitively wired in
            // via `core/datastore/DatastoreModule` in `KoinModules.allModules`).
            implementation(libs.multiplatform.settings)
        }

        commonTest.dependencies {
            implementation(libs.kotlinx.serialization.core)
        }
    }
}

compose.resources {
    publicResClass = true
    generateResClass = always
    packageOfResClass = "cmp.navigation.generated.resources"
}
