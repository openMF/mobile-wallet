/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.data.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kpt.core.base.common.di.CommonModule
import kpt.core.base.store.infra.FetchedAtRepository
import kpt.core.base.store.submit.OfflineSubmitSyncer
import kpt.core.base.store.submit.SubmitOutbox
import kpt.core.data.infra.NetworkMonitor
import kpt.core.data.infra.impl.JordondNetworkMonitor
import kpt.core.data.infra.impl.platformConnectivity
import kpt.core.data.infra.impl.RoomBookkeeper
import kpt.core.data.infra.impl.RoomFetchedAtRepository
import kpt.core.data.infra.impl.RoomSubmitOutbox
import kpt.core.data.user.UserDataRepository
import kpt.core.data.user.UserLogoutManager
import kpt.core.data.user.impl.UserDataRepositoryImpl
import kpt.core.data.user.impl.UserLogoutManagerImpl
import kpt.core.database.AppDatabase
import kpt.core.database.di.DatabaseModule
import kpt.core.datastore.di.DatastoreModule
import kpt.core.network.di.NetworkModule
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.mobilenativefoundation.store.store5.Bookkeeper

val DataModule = module {
    includes(platformModule, CommonModule, DatabaseModule, DatastoreModule, NetworkModule)

    // Cross-platform network monitor backed by jordond/connectivity — seeds current state
    // correctly on every target (fixes the kmptoolkit cmp-network-monitor v3.5.3 seed bug).
    // Supersedes both the template `NetworkMonitorProvider.install()` and the former
    // Android-only SeededNetworkMonitor override. Root fix tracked upstream in KmpToolkit.
    single<NetworkMonitor> { JordondNetworkMonitor(platformConnectivity(get()), get()) }
    singleOf(::UserDataRepositoryImpl) bind UserDataRepository::class

    // Framework FetchedAtRepository — durable lastFetchedAt persistence backing
    // DataFreshnessIndicator timestamps. Room-only by design (no in-memory fallback).
    single<FetchedAtRepository> { RoomFetchedAtRepository(get<AppDatabase>().fetchedAtDao) }

    // Framework DraftDao — backing store for SubmitOutbox / DraftSubmitHandler
    single { get<AppDatabase>().draftDao }

    // App-scoped CoroutineScope for cross-VM long-running coroutines (framework infra).
    single<CoroutineScope> { CoroutineScope(SupervisorJob() + Dispatchers.Default) }


    single<UserLogoutManager> { UserLogoutManagerImpl(get(), get(), get()) }

}

expect val platformModule: Module

