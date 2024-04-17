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
