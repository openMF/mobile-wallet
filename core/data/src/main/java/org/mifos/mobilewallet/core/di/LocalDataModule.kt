package org.mifos.mobilewallet.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.mifos.mobilewallet.core.repository.local.LocalAssetRepository
import org.mifos.mobilewallet.core.repository.local.MifosLocalAssetRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalDataModule {

    @Binds
    internal abstract fun bindsLocalAssetRepository(
        repository: MifosLocalAssetRepository,
    ): LocalAssetRepository
}
