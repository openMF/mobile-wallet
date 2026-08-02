/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package cmp.navigation.di

import cmp.navigation.AppViewModel
import cmp.navigation.rootnav.RootNavViewModel
import kpt.core.base.analytics.di.analyticsModule
import kpt.core.base.common.di.CommonModule
import kpt.core.base.platform.di.platformModule
import kpt.core.base.security.di.SecurityModule
import kpt.core.data.di.DataModule
import kpt.core.database.di.DatabaseModule
import kpt.core.datastore.di.DatastoreModule
import kpt.core.store.di.appStoreModule
// Template-demo feature modules stripped — Phase 2 nav-chunk (skeleton).
// The `kpt.feature.{amortization,bills,calculators,crypto,currencyrates,
// emicalculator,home,loans,macro,rates,settings}.di.*` modules do not exist in
// the fork's classpath; their imports would prevent compilation. Fork feature
// Koin modules (`org.mifospay.feature.*.di.*Module`) are still contributed via
// `cmp-shared/.../org/mifospay/shared/di/KoinModules.kt` for legacy entry
// points; Phase 2 T6/T7 will fold each fork feature module into `allModules`
// below as the per-feature nav-conversion progresses.
import kpt.sync.di.SyncModule
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
// Fork's PasscodeManager library binding — required by cmp-navigation's
// AppViewModel + PlatformAuthenticatorGate. See MifosPasscodeModule below for
// the singleton declaration; the constructor arg (PasscodeStorageAdapter) is
// bound by the fork's RepositoryModule (org.mifospay.core.data.di).
import org.mifos.authenticator.passcode.PasscodeManager
// Fork's :feature:passcode ViewModel bindings — MifosPasscodeViewModel and
// BiometricSetupScreenViewmodel used by the re-auth passcode + biometric-setup
// destinations. Registered in feature:passcode/.../PasscodeModule.kt.
import org.mifos.feature.passcode.MifosAuthenticatorModule
// Fork's core:common bindings — DispatchersModule provides the named
// MifosDispatchers.{IO, Main, Unconfined} qualifiers that fork RepositoryModule
// resolves via `named(MifosDispatchers.IO.name)` etc. stringProviderModule
// binds resource-id → string resolution used by fork error mappers.
import org.mifospay.core.common.di.DispatchersModule as ForkDispatchersModule
import org.mifospay.core.common.di.stringProviderModule
// Fork's RepositoryModule — binds AppLockRepository (AppViewModel dep),
// BiometricStorageAdapter (PlatformAuthenticatorGate dep), PasscodeStorageAdapter
// (constructor arg for MifosPasscodeModule singleton below), plus every fork
// repository impl (Account, Auth, Beneficiary, Client, ...).
import org.mifospay.core.data.di.RepositoryModule
// Fork's PreferencesModule — binds UserPreferencesRepository consumed by
// RootNavViewModel. Kept alongside kpt DatastoreModule (they bind different
// interfaces in different packages, so both coexist safely).
import org.mifospay.core.datastore.di.PreferencesModule
// Fork's DomainModule — use-cases wired above RepositoryModule (some fork
// ViewModels resolve these transitively; kept for completeness of the fork
// data/domain surface per Phase 2 T6/T7 wiring contract).
import org.mifospay.core.domain.di.DomainModule
// Fork's core:network bindings — LocalModule binds Supabase InstanceConfigLoader
// consumed by InstanceSelectorViewModel; NetworkModule binds the Ktor HTTP
// client + KtorInterceptor (source of GlobalAuthManager 401 flips).
import org.mifospay.core.network.di.LocalModule
import org.mifospay.core.network.di.NetworkModule

object KoinModules {
    private val dataModule = module {
        includes(DataModule, appStoreModule)
    }

    private val dispatcherModule = module {
        includes(CommonModule)
    }

    private val AppModule = module {
        includes(platformModule)

        viewModelOf(::AppViewModel)
        viewModelOf(::RootNavViewModel)
        // AuthenticatedNavbarNavigationViewModel registration removed — the VM
        // was deleted in this chunk (bare-bridge rewrite; fork's `MifosApp` owns
        // its own VM state via `rememberMifosAppState`).
    }

    private val featureModule = module {
        // Template `HomeModule`/`SettingsModule` (`kpt.feature.*.di.*`) removed
        // — the modules do not exist in the fork's classpath. Fork counterparts
        // (`org.mifospay.feature.home.di.HomeModule`,
        // `org.mifospay.feature.settings.di.SettingsModule`) are contributed via
        // `cmp-shared/.../org/mifospay/shared/di/KoinModules.kt` for legacy
        // entry points; Phase 2 T6/T7 folds them in here for the new shell.
    }

    // ── Fork Koin surface (Phase 2 T6/T7 — see `cmp-navigation/build.gradle.kts`
    // comments + `AppViewModel` KDoc). Grouped by layer so this reads like a
    // mirror of `cmp-shared/.../org/mifospay/shared/di/KoinModules.kt`, which
    // is the AUTHORITATIVE fork-side module list. The subset here is what the
    // template shell (`cmp.navigation.ComposeApp` + `AppViewModel` +
    // `RootNavViewModel` + `PlatformAuthenticatorGate`) needs at composition
    // time; the full fork feature-VM surface (auth VMs, home VMs, payments,
    // etc.) still lands via `cmp-shared/.../org/mifospay/shared/di/KoinModules`
    // for legacy entry points, and will fold in here as the per-feature
    // nav-conversion progresses.
    // ──────────────────────────────────────────────────────────────────────
    private val forkCommonModule = module {
        includes(stringProviderModule, ForkDispatchersModule)
    }

    private val forkNetworkModule = module {
        includes(LocalModule, NetworkModule)
    }

    private val forkDataModule = module {
        includes(RepositoryModule)
    }

    private val forkDomainModule = module {
        includes(DomainModule)
    }

    private val forkFeatureModule = module {
        includes(MifosAuthenticatorModule)
    }

    /**
     * Provides the fork's passcode-library [PasscodeManager] as a process-wide
     * singleton — required by [AppViewModel] (background-lock re-auth gate
     * reads `passcodeManager.state.value.passcodeStep`) and every passcode
     * screen composed downstream.
     *
     * Declared HERE (cmp-navigation) rather than in the fork's own
     * `cmp-shared/.../org/mifospay/shared/di/KoinModules.MifosPasscodeModule`
     * to keep the singleton reachable from the template shell's Koin graph
     * without pulling `:cmp-shared` back as a dependency of `:cmp-navigation`
     * (which would re-create the SharedApp → ComposeApp cycle; see
     * `cmp-navigation/build.gradle.kts`). Both copies of the module bind the
     * SAME class with a `single { ... }`, so Koin's module-override semantics
     * mean the last-included one wins — since [allModules] loads THIS module
     * and the cmp-shared aggregator loads THAT one, whichever `initKoin()` is
     * called (`cmp.shared.utils.initKoin` for the new shell,
     * `org.mifospay.shared.di.initKoin` for the legacy iOS/desktop paths) gets
     * one PasscodeManager singleton wired against the same
     * PasscodeStorageAdapter binding from `RepositoryModule`.
     */
    private val MifosPasscodeModule = module {
        single { PasscodeManager(get()) }
    }

    val allModules = listOf(
        SecurityModule,
        dataModule,
        DatabaseModule,
        dispatcherModule,
        analyticsModule,
        DatastoreModule,
        // Fork's UserPreferencesRepository / AutoPay / Bill / Biller bindings.
        // Kept alongside kpt DatastoreModule while other consumers still resolve
        // template's kpt.core.datastore.UserPreferencesRepository; the two bind
        // different interfaces (different packages), so both can coexist.
        PreferencesModule,
        featureModule,
        AppModule,
        SyncModule,
        // ── Fork Koin surface (Phase 2 T6/T7) ───────────────────────────────
        forkCommonModule,
        forkNetworkModule,
        forkDataModule,
        forkDomainModule,
        forkFeatureModule,
        MifosPasscodeModule,
    )
}
