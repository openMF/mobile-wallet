/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.datastore.di

import com.russhwolf.settings.Settings
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.mifospay.core.common.MifosDispatchers
import org.mifospay.core.datastore.UserPreferencesDataSource
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.datastore.UserPreferencesRepositoryImpl

val PreferencesModule = module {
    factory<Settings> { Settings() }
    // Use the IO dispatcher name - MifosDispatchers.IO.name
    factory { UserPreferencesDataSource(get(), get(named(MifosDispatchers.IO.name))) }

    single<UserPreferencesRepository> {
        UserPreferencesRepositoryImpl(
            preferenceManager = get(),
            ioDispatcher = get(named(MifosDispatchers.IO.name)),
            unconfinedDispatcher = get(named(MifosDispatchers.Unconfined.name)),
        )
    }
}
