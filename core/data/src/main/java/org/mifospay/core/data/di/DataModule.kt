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

import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.mifospay.core.data.base.TaskLooper
import org.mifospay.core.data.base.UseCaseFactory
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.base.UseCaseScheduler
import org.mifospay.core.data.base.UseCaseThreadPoolScheduler
import org.mifospay.core.data.domain.usecase.account.BlockUnblockCommand
import org.mifospay.core.data.domain.usecase.account.DownloadTransactionReceipt
import org.mifospay.core.data.domain.usecase.account.FetchAccount
import org.mifospay.core.data.domain.usecase.account.FetchAccountTransaction
import org.mifospay.core.data.domain.usecase.account.FetchAccountTransactions
import org.mifospay.core.data.domain.usecase.account.FetchAccountTransfer
import org.mifospay.core.data.domain.usecase.account.FetchAccounts
import org.mifospay.core.data.domain.usecase.account.FetchMerchants
import org.mifospay.core.data.domain.usecase.account.TransferFunds
import org.mifospay.core.data.domain.usecase.client.CreateClient
import org.mifospay.core.data.domain.usecase.client.FetchClientData
import org.mifospay.core.data.domain.usecase.client.FetchClientDetails
import org.mifospay.core.data.domain.usecase.client.FetchClientImage
import org.mifospay.core.data.domain.usecase.client.SearchClient
import org.mifospay.core.data.domain.usecase.client.UpdateClient
import org.mifospay.core.data.domain.usecase.history.TransactionsHistory
import org.mifospay.core.data.domain.usecase.invoice.FetchInvoice
import org.mifospay.core.data.domain.usecase.invoice.FetchInvoices
import org.mifospay.core.data.domain.usecase.kyc.FetchKYCLevel1Details
import org.mifospay.core.data.domain.usecase.kyc.UpdateKYCLevel1Details
import org.mifospay.core.data.domain.usecase.kyc.UploadKYCDocs
import org.mifospay.core.data.domain.usecase.kyc.UploadKYCLevel1Details
import org.mifospay.core.data.domain.usecase.notification.FetchNotifications
import org.mifospay.core.data.domain.usecase.savedcards.AddCard
import org.mifospay.core.data.domain.usecase.savedcards.DeleteCard
import org.mifospay.core.data.domain.usecase.savedcards.EditCard
import org.mifospay.core.data.domain.usecase.savedcards.FetchSavedCards
import org.mifospay.core.data.domain.usecase.standinginstruction.CreateStandingTransaction
import org.mifospay.core.data.domain.usecase.standinginstruction.DeleteStandingInstruction
import org.mifospay.core.data.domain.usecase.standinginstruction.FetchStandingInstruction
import org.mifospay.core.data.domain.usecase.standinginstruction.GetAllStandingInstructions
import org.mifospay.core.data.domain.usecase.standinginstruction.UpdateStandingInstruction
import org.mifospay.core.data.domain.usecase.twofactor.FetchDeliveryMethods
import org.mifospay.core.data.domain.usecase.twofactor.RequestOTP
import org.mifospay.core.data.domain.usecase.twofactor.ValidateOTP
import org.mifospay.core.data.domain.usecase.user.AuthenticateUser
import org.mifospay.core.data.domain.usecase.user.CreateUser
import org.mifospay.core.data.domain.usecase.user.DeleteUser
import org.mifospay.core.data.domain.usecase.user.FetchUserDetails
import org.mifospay.core.data.domain.usecase.user.FetchUsers
import org.mifospay.core.data.domain.usecase.user.RegisterUser
import org.mifospay.core.data.domain.usecase.user.UpdateUser
import org.mifospay.core.data.domain.usecase.user.VerifyUser
import org.mifospay.core.data.fineract.entity.mapper.AccountMapper
import org.mifospay.core.data.fineract.entity.mapper.ClientDetailsMapper
import org.mifospay.core.data.fineract.entity.mapper.CurrencyMapper
import org.mifospay.core.data.fineract.entity.mapper.SearchedEntitiesMapper
import org.mifospay.core.data.fineract.entity.mapper.TransactionMapper
import org.mifospay.core.data.fineract.repository.FineractRepository
import org.mifospay.core.data.repository.auth.AuthenticationUserRepository
import org.mifospay.core.data.repository.auth.UserDataRepository
import org.mifospay.core.data.repository.local.LocalAssetRepository
import org.mifospay.core.data.repository.local.LocalRepository
import org.mifospay.core.data.repository.local.MifosLocalAssetRepository
import org.mifospay.core.data.util.ConnectivityManagerNetworkMonitor
import org.mifospay.core.data.util.NetworkMonitor
import org.mifospay.core.data.util.TimeZoneBroadcastMonitor
import org.mifospay.core.data.util.TimeZoneMonitor
import org.mifospay.core.datastore.PreferencesHelper
import org.mifospay.core.network.MifosDispatchers
import org.mifospay.core.network.di.LocalModule
import org.mifospay.core.network.localAssets.LocalAssetDataSource
import org.mifospay.core.network.localAssets.MifosLocalAssetDataSource

val DataModule = module {
    includes(LocalModule)

    single<UseCaseScheduler> { UseCaseThreadPoolScheduler() }
    single { UseCaseHandler(get()) }
    single { TaskLooper(get()) }
    single { UseCaseFactory(get()) }
    single { FetchClientData(get(), get()) }
    single { ClientDetailsMapper() }
    single { SearchClient(get(), get()) }
    single { UpdateClient(get()) }
    single { CreateClient(get()) }
    single { FetchClientDetails(get()) }
    single { FetchClientImage(get()) }
    single { BlockUnblockCommand(get()) }
    single { DownloadTransactionReceipt(get()) }
    single { AccountMapper(get()) }
    single { FetchAccount(get(), get()) }
    single { FetchAccounts(get(), get()) }
    single { FetchAccountTransaction(get(), get()) }
    single { FetchAccountTransactions(get(), get()) }
    single { FetchAccountTransfer(get()) }
    single { FetchMerchants(get()) }
    single { TransferFunds(get()) }
    single {
        TransactionsHistory(
            mUseCaseHandler = get(),
            fetchAccountTransactionsUseCase = get(),
        )
    }

    // Invoice UseCase
    single { FetchInvoice(get()) }
    single { FetchInvoices(get()) }

    // KYC UseCase
    single { FetchKYCLevel1Details(get()) }
    single { UpdateKYCLevel1Details(get()) }
    single { UploadKYCDocs(get()) }
    single { UploadKYCLevel1Details(get()) }

    // Notifications
    single { FetchNotifications(get()) }

    // Saved Cards
    single { AddCard(get()) }
    single { DeleteCard(get()) }
    single { EditCard(get()) }
    single { FetchSavedCards(get()) }

    // Standing Instructions
    single { CreateStandingTransaction(get()) }
    single { DeleteStandingInstruction(get()) }
    single { FetchStandingInstruction(get()) }
    single { GetAllStandingInstructions(get()) }
    single { UpdateStandingInstruction(get()) }

    // Two-Factor
    single { FetchDeliveryMethods(get()) }
    single { RequestOTP(get()) }
    single { ValidateOTP(get()) }

    // User
    single { AuthenticateUser(get()) }
    single { CreateUser(get()) }
    single { DeleteUser(get()) }
    single { FetchUserDetails(get()) }
    single { FetchUsers(get()) }
    single { RegisterUser(get()) }
    single { UpdateUser(get()) }
    single { VerifyUser(get()) }

    // Fineract Entity Mappers
    single { CurrencyMapper() }
    single { SearchedEntitiesMapper() }
    single { TransactionMapper(get()) }
    single { AccountMapper(get()) }
    single { ClientDetailsMapper() }

    // Fineract Repository

    single {
        FineractRepository(
            fineractApiManager = get(),
            selfApiManager = get(),
            ktorAuthenticationService = get(),
        )
    }

    // Fineract Repository Auth

    single<UserDataRepository> { AuthenticationUserRepository(get()) }

    // Fineract Repository Local

    single {
        val preferencesHelper: PreferencesHelper = get()
        LocalRepository(preferencesHelper)
    }

    factory<LocalAssetDataSource> {
        MifosLocalAssetDataSource(
            ioDispatcher = get(
                named(MifosDispatchers.IO.name),
            ),
            networkJson = get(),
            assets = get(),
        )
    }

    factory<LocalAssetRepository> {
        MifosLocalAssetRepository(
            ioDispatcher = get(
                named(MifosDispatchers.IO.name),
            ),
            datasource = get(),
        )
    }

    // Util

    single<NetworkMonitor> { ConnectivityManagerNetworkMonitor(context = androidContext()) }

    single<TimeZoneMonitor> {
        TimeZoneBroadcastMonitor(
            context = androidContext(),
            appScope = get(named("ApplicationScope")),
            ioDispatcher = get(named(MifosDispatchers.IO.name)),
        )
    }
}
