package org.mifos.mobilewallet.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.mifos.mobilewallet.core.repository.auth.AuthenticationUserRepository
import org.mifos.mobilewallet.core.repository.auth.UserDataRepository
import org.mifos.mobilewallet.core.repository.local.LocalAssetRepository
import org.mifos.mobilewallet.core.repository.local.MifosLocalAssetRepository
import org.mifos.mobilewallet.core.util.ConnectivityManagerNetworkMonitor
import org.mifos.mobilewallet.core.util.NetworkMonitor
import org.mifos.mobilewallet.core.util.TimeZoneBroadcastMonitor
import org.mifos.mobilewallet.core.util.TimeZoneMonitor

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
