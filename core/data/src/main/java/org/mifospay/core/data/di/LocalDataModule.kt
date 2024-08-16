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

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.mifospay.core.data.repository.auth.AuthenticationUserRepository
import org.mifospay.core.data.repository.auth.UserDataRepository
import org.mifospay.core.data.repository.local.LocalAssetRepository
import org.mifospay.core.data.repository.local.MifosLocalAssetRepository
import org.mifospay.core.data.util.ConnectivityManagerNetworkMonitor
import org.mifospay.core.data.util.NetworkMonitor
import org.mifospay.core.data.util.TimeZoneBroadcastMonitor
import org.mifospay.core.data.util.TimeZoneMonitor

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalDataModule {

    @Binds
    internal abstract fun bindsUserDataRepository(
        authenticationUserRepository: AuthenticationUserRepository,
    ): UserDataRepository

    @Binds
    internal abstract fun bindsLocalAssetRepository(
        repository: MifosLocalAssetRepository,
    ): LocalAssetRepository

    @Binds
    internal abstract fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor,
    ): NetworkMonitor

    @Binds
    internal abstract fun binds(impl: TimeZoneBroadcastMonitor): TimeZoneMonitor
}
