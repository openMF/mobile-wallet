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

// ── Fork DI-surface import notes (consolidated ABOVE the import list so ktlint
//    standard:import-ordering stays green — comments must not interrupt the sorted block):
//  • kpt.feature.* template-demo modules were stripped (absent from the fork classpath);
//    fork feature Koin modules are contributed via cmp-shared/.../shared/di/KoinModules.kt,
//    and Phase 2 T6/T7 folds them into `allModules` below as nav-conversion progresses.
//  • org.mifos.authenticator.passcode.PasscodeManager — required by AppViewModel +
//    PlatformAuthenticatorGate (see MifosPasscodeModule below; PasscodeStorageAdapter ctor
//    arg bound by the fork RepositoryModule).
//  • org.mifos.feature.passcode.MifosAuthenticatorModule — :feature:passcode ViewModel bindings
//    (re-auth passcode + biometric-setup destinations).
//  • ForkDispatchersModule (aliased DispatchersModule) + stringProviderModule — core:common
//    (named MifosDispatchers.* qualifiers + resource-id → string resolution).
//  • RepositoryModule — AppLockRepository / BiometricStorageAdapter / PasscodeStorageAdapter +
//    every fork repository impl. PreferencesModule — UserPreferencesRepository (coexists with
//    kpt DatastoreModule). DomainModule — fork use-cases.
//  • LocalModule / NetworkModule — Supabase InstanceConfigLoader + Ktor client + KtorInterceptor.
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
import kpt.sync.di.SyncModule
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.mifos.authenticator.passcode.PasscodeManager
import org.mifos.feature.passcode.MifosAuthenticatorModule
import org.mifospay.core.common.di.stringProviderModule
import org.mifospay.core.data.di.RepositoryModule
import org.mifospay.core.datastore.di.PreferencesModule
import org.mifospay.core.domain.di.DomainModule
import org.mifospay.core.network.di.LocalModule
import org.mifospay.core.network.di.NetworkModule
import org.mifospay.core.common.di.DispatchersModule as ForkDispatchersModule

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
