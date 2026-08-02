/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.datastore.di

import com.russhwolf.settings.Settings
import kpt.core.base.common.di.CommonModule
import kpt.core.base.datastore.di.DatastoreBaseModule
import kpt.core.datastore.UserPreferencesRepository
import kpt.core.datastore.UserPreferencesRepositoryImpl
import kpt.core.datastore.infra.SettingsSyncStatePersister
import kpt.core.datastore.infra.SyncStatePersister
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val DatastoreModule = module {
    includes(CommonModule, DatastoreBaseModule)

    single {
        UserPreferencesRepositoryImpl(
            plainSettings = get<Settings>(named("plain")),
            secureSettings = get<Settings>(named("secure")),
            dispatcher = get(),
        )
    } bind UserPreferencesRepository::class

    // Sync state persister — Settings-backed (same store as user prefs).
    // Read by Synchronizer at sync start; written on snapshot/changeList completion.
    single<SyncStatePersister> {
        SettingsSyncStatePersister(plainSettings = get<Settings>(named("plain")))
    }
}
