package org.mifospay.core.datastore.di

import com.russhwolf.settings.Settings
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.mifospay.core.datastore.UserPreferencesDataSource

val PreferencesModule = module {
    factory<Settings> { Settings() }
    // Use the IO dispatcher name - MifosDispatchers.IO.name
    factory { UserPreferencesDataSource(get(), get(named("IO"))) }
}