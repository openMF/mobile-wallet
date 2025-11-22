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
import org.mifospay.core.data.repository.AssetRepository
import org.mifospay.core.data.repository.AuthenticationRepository
import org.mifospay.core.data.repository.BeneficiaryRepository
import org.mifospay.core.data.repository.ClientRepository
import org.mifospay.core.data.repository.DocumentRepository
import org.mifospay.core.data.repository.InterBankRepository
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
import org.mifospay.core.data.repositoryImpl.AccountRepositoryImpl
import org.mifospay.core.data.repositoryImpl.AssetRepositoryImpl
import org.mifospay.core.data.repositoryImpl.AuthenticationRepositoryImpl
import org.mifospay.core.data.repositoryImpl.BeneficiaryRepositoryImpl
import org.mifospay.core.data.repositoryImpl.ClientRepositoryImpl
import org.mifospay.core.data.repositoryImpl.DocumentRepositoryImpl
import org.mifospay.core.data.repositoryImpl.InterBankRepositoryImpl
import org.mifospay.core.data.repositoryImpl.InvoiceRepositoryImpl
import org.mifospay.core.data.repositoryImpl.KycLevelRepositoryImpl
import org.mifospay.core.data.repositoryImpl.LocalAssetRepositoryImpl
import org.mifospay.core.data.repositoryImpl.NotificationRepositoryImpl
import org.mifospay.core.data.repositoryImpl.RegistrationRepositoryImpl
import org.mifospay.core.data.repositoryImpl.RunReportRepositoryImpl
import org.mifospay.core.data.repositoryImpl.SavedCardRepositoryImpl
import org.mifospay.core.data.repositoryImpl.SavingsAccountRepositoryImpl
import org.mifospay.core.data.repositoryImpl.SearchRepositoryImpl
import org.mifospay.core.data.repositoryImpl.SelfServiceRepositoryImpl
import org.mifospay.core.data.repositoryImpl.StandingInstructionRepositoryImpl
import org.mifospay.core.data.repositoryImpl.ThirdPartyTransferRepositoryImpl
import org.mifospay.core.data.repositoryImpl.TwoFactorAuthRepositoryImpl
import org.mifospay.core.data.repositoryImpl.UserRepositoryImpl
import org.mifospay.core.data.util.NetworkMonitor
import org.mifospay.core.data.util.TimeZoneMonitor

private val ioDispatcher = named(MifosDispatchers.IO.name)
private val unconfined = named(MifosDispatchers.Unconfined.name)

val RepositoryModule = module {
    single<Json> { Json { ignoreUnknownKeys = true } }

    single<AssetRepository> { AssetRepositoryImpl() }
    single<AccountRepository> { AccountRepositoryImpl(get(), get(), get(ioDispatcher)) }
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
    single<InterBankRepository> { InterBankRepositoryImpl(get(), get(ioDispatcher)) }
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
