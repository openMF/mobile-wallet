package org.mifos.mobilewallet.mifospay.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository
import org.mifos.mobilewallet.datastore.PreferencesHelper

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    fun providesLocalRepository(preferencesHelper: PreferencesHelper): LocalRepository {
        return LocalRepository(preferencesHelper)
    }
}
