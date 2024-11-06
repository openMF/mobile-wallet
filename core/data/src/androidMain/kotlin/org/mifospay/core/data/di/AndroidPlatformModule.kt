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
import org.mifospay.core.data.util.TimeZoneBroadcastMonitor
import org.mifospay.core.data.util.TimeZoneMonitor

val AndroidDataModule = module {
    single<NetworkMonitor> {
        ConnectivityManagerNetworkMonitor(androidContext(), get(named(MifosDispatchers.IO.name)))
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
