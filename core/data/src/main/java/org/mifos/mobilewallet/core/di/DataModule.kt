package org.mifos.mobilewallet.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.base.UseCaseThreadPoolScheduler
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import org.mifos.mobilewallet.mifospay.network.FineractApiManager
import org.mifos.mobilewallet.mifospay.network.SelfServiceApiManager

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    fun provideUseCaseThreadPoolScheduler(): UseCaseThreadPoolScheduler =
        UseCaseThreadPoolScheduler()

    @Provides
    fun providesUseCaseHandler(useCaseThreadPoolScheduler: UseCaseThreadPoolScheduler): UseCaseHandler {
        return UseCaseHandler(useCaseThreadPoolScheduler)
    }

    @Provides
    fun providesFineractRepository(
        fineractApiManager: FineractApiManager,
        selfServiceApiManager: SelfServiceApiManager
    ): FineractRepository {
        return FineractRepository(fineractApiManager, selfServiceApiManager)
    }
}
