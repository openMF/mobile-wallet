package org.mifos.mobilewallet.datastore.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.mifos.mobilewallet.datastore.PreferencesHelper
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun prefManager(@ApplicationContext context: Context): PreferencesHelper {
        return PreferencesHelper(context)
    }
}