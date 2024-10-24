/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.network.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import org.mifospay.core.datastore.PreferencesHelper
import org.mifospay.core.network.BaseURL
import org.mifospay.core.network.FineractApiManager
import org.mifospay.core.network.KtorInterceptor
import org.mifospay.core.network.MifosWalletOkHttpClient
import org.mifospay.core.network.SelfServiceApiManager
import org.mifospay.core.network.services.AccountTransfersService
import org.mifospay.core.network.services.AuthenticationService
import org.mifospay.core.network.services.BeneficiaryService
import org.mifospay.core.network.services.ClientService
import org.mifospay.core.network.services.DocumentService
import org.mifospay.core.network.services.InvoiceService
import org.mifospay.core.network.services.KYCLevel1Service
import org.mifospay.core.network.services.KtorAuthenticationService
import org.mifospay.core.network.services.KtorSavingsAccountService
import org.mifospay.core.network.services.NotificationService
import org.mifospay.core.network.services.RegistrationService
import org.mifospay.core.network.services.RunReportService
import org.mifospay.core.network.services.SavedCardService
import org.mifospay.core.network.services.SavingsAccountsService
import org.mifospay.core.network.services.SearchService
import org.mifospay.core.network.services.StandingInstructionService
import org.mifospay.core.network.services.ThirdPartyTransferService
import org.mifospay.core.network.services.TwoFactorAuthService
import org.mifospay.core.network.services.UserService
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Suppress("TooManyFunctions")
val NetworkModule = module {

    single {
        Json {
            ignoreUnknownKeys = true
        }
    }

    single(SelfServiceApi) {
        val preferencesHelper: PreferencesHelper = get()
        Retrofit.Builder()
            .baseUrl(BaseURL().selfServiceUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .client(MifosWalletOkHttpClient(preferencesHelper).mifosOkHttpClient)
            .build()
    }

    single(FineractApi) {
        val preferencesHelper: PreferencesHelper = get()
        Retrofit.Builder()
            .baseUrl(BaseURL().url)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .client(MifosWalletOkHttpClient(preferencesHelper).mifosOkHttpClient)
            .build()
    }

    // This can be removed as it for testing purpose
    single(Testing) {
        val preferencesHelper: PreferencesHelper = get()
        Retrofit.Builder()
            .baseUrl(BaseURL().url)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .client(
                MifosWalletOkHttpClient(preferencesHelper, "mifos", "password", true)
                    .mifosOkHttpClient,
            )
            .build()
    }

    single {
        FineractApiManager(
            authenticationService = get(FineractAuthenticationService),
            clientService = get(FineractClientService),
            savingsAccountsService = get(FineractSavingsAccountsService),
            registrationService = get(FineractRegistrationService),
            searchService = get(),
            documentService = get(),
            runReportService = get(),
            twoFactorAuthService = get(),
            accountTransfersService = get(),
            savedCardService = get(),
            kYCLevel1Service = get(),
            invoiceService = get(),
            userService = get(),
            thirdPartyTransferService = get(FineractThirdPartyTransferService),
            standingInstructionService = get(),
            notificationService = get(),
            ktorSavingsAccountService = get(),
        )
    }

    single {
        SelfServiceApiManager(
            authenticationService = get(SelfServiceAuthenticationService),
            clientService = get(SelfServiceClientService),
            savingsAccountsService = get(SelfServiceSavingsAccountsService),
            registrationService = get(SelfServiceRegistrationService),
            beneficiaryService = get(),
            thirdPartyTransferService = get(SelfServiceThirdPartyTransferService),
            ktorSavingsAccountService = get(),
        )
    }

//    Http client for Ktor

    single {
        HttpClient(Android) {
            install(WebSockets)
            install(KtorInterceptor) {
                this.preferencesHelper = get()
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    },
                )
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 15000
            }
            install(Logging) {
                level = LogLevel.ALL
            }
        }
    }

    single { KtorAuthenticationService(client = get()) }
    single { KtorSavingsAccountService(client = get()) }

    // -----Fineract API Service---------//

    single<AuthenticationService>(FineractAuthenticationService) {
        get<Retrofit>(FineractApi).create(AuthenticationService::class.java)
    }

    single<ClientService>(FineractClientService) {
        get<Retrofit>(FineractApi).create(ClientService::class.java)
    }

    single<SavingsAccountsService>(FineractSavingsAccountsService) {
        get<Retrofit>(FineractApi).create(SavingsAccountsService::class.java)
    }

    single<RegistrationService>(FineractRegistrationService) {
        get<Retrofit>(FineractApi).create(RegistrationService::class.java)
    }

    single<SearchService> {
        get<Retrofit>(FineractApi).create(SearchService::class.java)
    }

    single<SavedCardService> {
        get<Retrofit>(FineractApi).create(SavedCardService::class.java)
    }

    single<DocumentService> {
        get<Retrofit>(FineractApi).create(DocumentService::class.java)
    }

    single<TwoFactorAuthService> {
        get<Retrofit>(FineractApi).create(TwoFactorAuthService::class.java)
    }

    single<AccountTransfersService> {
        get<Retrofit>(FineractApi).create(AccountTransfersService::class.java)
    }

    single<RunReportService> {
        get<Retrofit>(FineractApi).create(RunReportService::class.java)
    }

    single<KYCLevel1Service> {
        get<Retrofit>(FineractApi).create(KYCLevel1Service::class.java)
    }

    single<InvoiceService> {
        get<Retrofit>(FineractApi).create(InvoiceService::class.java)
    }

    single<UserService> {
        get<Retrofit>(FineractApi).create(UserService::class.java)
    }

    single<ThirdPartyTransferService>(FineractThirdPartyTransferService) {
        get<Retrofit>(FineractApi).create(ThirdPartyTransferService::class.java)
    }

    single<NotificationService> {
        get<Retrofit>(FineractApi).create(NotificationService::class.java)
    }

    single<StandingInstructionService> {
        get<Retrofit>(FineractApi).create(StandingInstructionService::class.java)
    }

    // -------SelfService API Service-------//

    single<AuthenticationService>(SelfServiceAuthenticationService) {
        get<Retrofit>(SelfServiceApi).create(AuthenticationService::class.java)
    }

    single<ClientService>(SelfServiceClientService) {
        get<Retrofit>(SelfServiceApi).create(ClientService::class.java)
    }

    single<SavingsAccountsService>(SelfServiceSavingsAccountsService) {
        get<Retrofit>(SelfServiceApi).create(SavingsAccountsService::class.java)
    }

    single<RegistrationService>(SelfServiceRegistrationService) {
        get<Retrofit>(SelfServiceApi).create(RegistrationService::class.java)
    }

    single<BeneficiaryService> {
        get<Retrofit>(SelfServiceApi).create(BeneficiaryService::class.java)
    }

    single<ThirdPartyTransferService>(SelfServiceThirdPartyTransferService) {
        get<Retrofit>(SelfServiceApi).create(ThirdPartyTransferService::class.java)
    }
}
