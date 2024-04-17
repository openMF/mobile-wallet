package org.mifospay.core.network.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.mifospay.core.network.local_assets.LocalAssetDataSource
import org.mifospay.core.network.local_assets.MifosLocalAssetDataSource

@Module
@InstallIn(SingletonComponent::class)
internal interface LocalModule {

    @Binds
    fun binds(impl: MifosLocalAssetDataSource): LocalAssetDataSource
}
