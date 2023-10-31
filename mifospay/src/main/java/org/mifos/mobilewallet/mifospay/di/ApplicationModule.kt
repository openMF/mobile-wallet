package org.mifos.mobilewallet.mifospay.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.base.UseCaseThreadPoolScheduler
import org.mifos.mobilewallet.core.data.fineract.api.FineractApiManager
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository
import org.mifos.mobilewallet.mifospay.data.local.PreferencesHelper

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    fun provideUseCaseThreadPoolScheduler() : UseCaseThreadPoolScheduler = UseCaseThreadPoolScheduler()

    @Provides
    fun providesUseCaseHandler(useCaseThreadPoolScheduler: UseCaseThreadPoolScheduler): UseCaseHandler {
        return UseCaseHandler(useCaseThreadPoolScheduler)
    }

    @Provides
    fun providesFineractApiManager(): FineractApiManager {
        return FineractApiManager()
    }

    @Provides
    fun providesFineractRepository(fineractApiManager: FineractApiManager): FineractRepository {
        return FineractRepository(fineractApiManager)
    }

    @Provides
    fun prefManager(@ApplicationContext context: Context): PreferencesHelper {
        return PreferencesHelper(context)
    }

    @Provides
    fun providesLocalRepository(preferencesHelper: PreferencesHelper): LocalRepository {
        return LocalRepository(preferencesHelper)
    }
}