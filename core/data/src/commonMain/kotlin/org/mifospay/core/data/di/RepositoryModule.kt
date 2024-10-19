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

import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.mifospay.core.common.MifosDispatchers
import org.mifospay.core.data.repository.AccountRepository
import org.mifospay.core.data.repository.AuthenticationRepository
import org.mifospay.core.data.repository.BeneficiaryRepository
import org.mifospay.core.data.repository.ClientRepository
import org.mifospay.core.data.repository.DocumentRepository
import org.mifospay.core.data.repository.InvoiceRepository
import org.mifospay.core.data.repository.KycLevelRepository
import org.mifospay.core.data.repository.LocalAssetRepository
import org.mifospay.core.data.repository.NotificationRepository
import org.mifospay.core.data.repository.RegistrationRepository
import org.mifospay.core.data.repository.RunReportRepository
import org.mifospay.core.data.repository.SavedCardRepository
import org.mifospay.core.data.repository.SavingsAccountRepository
import org.mifospay.core.data.repository.SearchRepository
import org.mifospay.core.data.repository.SelfServiceRepository
import org.mifospay.core.data.repository.StandingInstructionRepository
import org.mifospay.core.data.repository.ThirdPartyTransferRepository
import org.mifospay.core.data.repository.TwoFactorAuthRepository
import org.mifospay.core.data.repository.UserRepository
import org.mifospay.core.data.repositoryImp.AccountRepositoryImpl
import org.mifospay.core.data.repositoryImp.AuthenticationRepositoryImpl
import org.mifospay.core.data.repositoryImp.BeneficiaryRepositoryImpl
import org.mifospay.core.data.repositoryImp.ClientRepositoryImpl
import org.mifospay.core.data.repositoryImp.DocumentRepositoryImpl
import org.mifospay.core.data.repositoryImp.InvoiceRepositoryImpl
import org.mifospay.core.data.repositoryImp.KycLevelRepositoryImpl
import org.mifospay.core.data.repositoryImp.LocalAssetRepositoryImpl
import org.mifospay.core.data.repositoryImp.NotificationRepositoryImpl
import org.mifospay.core.data.repositoryImp.RegistrationRepositoryImpl
import org.mifospay.core.data.repositoryImp.RunReportRepositoryImpl
import org.mifospay.core.data.repositoryImp.SavedCardRepositoryImpl
import org.mifospay.core.data.repositoryImp.SavingsAccountRepositoryImpl
import org.mifospay.core.data.repositoryImp.SearchRepositoryImpl
import org.mifospay.core.data.repositoryImp.SelfServiceRepositoryImpl
import org.mifospay.core.data.repositoryImp.StandingInstructionRepositoryImpl
import org.mifospay.core.data.repositoryImp.ThirdPartyTransferRepositoryImpl
import org.mifospay.core.data.repositoryImp.TwoFactorAuthRepositoryImpl
import org.mifospay.core.data.repositoryImp.UserRepositoryImpl
import org.mifospay.core.data.util.NetworkMonitor
import org.mifospay.core.data.util.TimeZoneMonitor

private val ioDispatcher = named(MifosDispatchers.IO.name)
private val unconfined = named(MifosDispatchers.Unconfined.name)

val RepositoryModule = module {
    single<Json> { Json { ignoreUnknownKeys = true } }

    single<AccountRepository> { AccountRepositoryImpl(get(), get(ioDispatcher)) }
    single<AuthenticationRepository> {
        AuthenticationRepositoryImpl(get(), get(ioDispatcher))
    }
    single<BeneficiaryRepository> { BeneficiaryRepositoryImpl(get(), get(ioDispatcher)) }
    single<ClientRepository> {
        ClientRepositoryImpl(
            apiManager = get(),
            fineractApiManager = get(),
            ioDispatcher = get(ioDispatcher),
        )
    }
    single<DocumentRepository> { DocumentRepositoryImpl(get(), get(ioDispatcher)) }
    single<InvoiceRepository> { InvoiceRepositoryImpl(get(), get(ioDispatcher)) }
    single<KycLevelRepository> { KycLevelRepositoryImpl(get(), get(ioDispatcher)) }
    single<NotificationRepository> { NotificationRepositoryImpl(get(), get(ioDispatcher)) }
    single<RegistrationRepository> { RegistrationRepositoryImpl(get(), get(ioDispatcher)) }
    single<RunReportRepository> { RunReportRepositoryImpl(get(), get(ioDispatcher)) }
    single<SavedCardRepository> { SavedCardRepositoryImpl(get(), get(ioDispatcher)) }
    single<SavingsAccountRepository> { SavingsAccountRepositoryImpl(get(), get(ioDispatcher)) }
    single<SearchRepository> { SearchRepositoryImpl(get(), get(ioDispatcher)) }
    single<SelfServiceRepository> { SelfServiceRepositoryImpl(get(), get(ioDispatcher)) }
    single<StandingInstructionRepository> {
        StandingInstructionRepositoryImpl(get(), get(ioDispatcher))
    }
    single<ThirdPartyTransferRepository> {
        ThirdPartyTransferRepositoryImpl(get(), get(ioDispatcher))
    }
    single<TwoFactorAuthRepository> { TwoFactorAuthRepositoryImpl(get(), get(ioDispatcher)) }
    single<UserRepository> { UserRepositoryImpl(get(), get(ioDispatcher)) }

    includes(platformModule)
    single<PlatformDependentDataModule> { getPlatformDataModule }
    single<NetworkMonitor> { getPlatformDataModule.networkMonitor }
    single<TimeZoneMonitor> { getPlatformDataModule.timeZoneMonitor }
    single<LocalAssetRepository> {
        LocalAssetRepositoryImpl(
            ioDispatcher = get(qualifier = ioDispatcher),
            unconfinedDispatcher = get(unconfined),
            networkJson = get(),
        )
    }
}
