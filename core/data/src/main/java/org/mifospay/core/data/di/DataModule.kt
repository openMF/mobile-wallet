package org.mifospay.core.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.base.UseCaseThreadPoolScheduler
import org.mifospay.core.data.fineract.repository.FineractRepository
import org.mifospay.core.network.FineractApiManager
import org.mifospay.core.network.SelfServiceApiManager

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
