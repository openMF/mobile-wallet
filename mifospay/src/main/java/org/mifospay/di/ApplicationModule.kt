/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.mifospay.core.datastore.PreferencesHelper
import org.mifospay.data.local.LocalRepository

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    fun providesLocalRepository(preferencesHelper: PreferencesHelper): LocalRepository {
        return LocalRepository(preferencesHelper)
    }
}
