/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
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
import org.mifospay.core.network.services.KtorAuthenticationService

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
        selfServiceApiManager: SelfServiceApiManager,
        ktorAuthenticationService: KtorAuthenticationService,
    ): FineractRepository {
        return FineractRepository(
            fineractApiManager,
            selfServiceApiManager,
            ktorAuthenticationService,
        )
    }
}
