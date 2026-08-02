/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.mifospay.core.common.MifosDispatchers
import org.mifospay.core.data.util.ConnectivityManagerNetworkMonitor
import org.mifospay.core.data.util.NetworkMonitor
import org.mifospay.core.data.util.SeededNetworkMonitor
import org.mifospay.core.data.util.TimeZoneBroadcastMonitor
import org.mifospay.core.data.util.TimeZoneMonitor
import kpt.core.data.infra.NetworkMonitor as StoreNetworkMonitor

val AndroidDataModule = module {
    single<NetworkMonitor> {
        ConnectivityManagerNetworkMonitor(androidContext(), get(named(MifosDispatchers.IO.name)))
    }

    // Fix kmptoolkit cmp-network-monitor v3.5.3 initial-seed bug: the store + shell
    // resolve the kmptoolkit `NetworkMonitor` (StoreNetworkMonitor), whose
    // `NetworkMonitorProvider.install()` reports offline-forever when already connected
    // at launch. Bind it to a fork-backed monitor that seeds current connectivity. This
    // module is includes()d after the template's install() binding, so it wins on Android.
    single<StoreNetworkMonitor> {
        SeededNetworkMonitor(
            context = androidContext(),
            ioDispatcher = get(named(MifosDispatchers.IO.name)),
            scope = get(named("ApplicationScope")),
        )
    }

    single<TimeZoneMonitor> {
        TimeZoneBroadcastMonitor(
            context = androidContext(),
            appScope = get(named("ApplicationScope")),
            ioDispatcher = get(named(MifosDispatchers.IO.name)),
        )
    }

    single {
        AndroidPlatformDependentDataModule(
            context = androidContext(),
            dispatcher = get(named(MifosDispatchers.IO.name)),
            scope = get(named("ApplicationScope")),
        )
    }
}

actual val platformModule: Module = AndroidDataModule

actual val getPlatformDataModule: PlatformDependentDataModule
    get() = org.koin.core.context.GlobalContext.get().get()
