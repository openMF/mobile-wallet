package org.mifospay.di

import android.content.Context
import com.mifos.mobile.passcode.utils.PasscodePreferencesHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.mifospay.data.local.LocalRepository
import org.mifospay.core.datastore.PreferencesHelper
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    fun providesLocalRepository(preferencesHelper: PreferencesHelper): LocalRepository {
        return LocalRepository(preferencesHelper)
    }

    @Provides
    @Singleton
    fun providesPasscodePreferencesHelper(
        @ApplicationContext context: Context
    ): PasscodePreferencesHelper {
        return PasscodePreferencesHelper(context)
    }
}
