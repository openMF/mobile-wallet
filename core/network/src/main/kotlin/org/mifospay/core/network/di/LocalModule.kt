/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.network.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.mifospay.core.network.localAssets.LocalAssetDataSource
import org.mifospay.core.network.localAssets.MifosLocalAssetDataSource

@Module
@InstallIn(SingletonComponent::class)
internal interface LocalModule {

    @Binds
    fun binds(impl: MifosLocalAssetDataSource): LocalAssetDataSource
}
