package org.mifos.mobilewallet.mifospay.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.mifos.mobilewallet.datastore.PreferencesHelper
import org.mifos.mobilewallet.mifospay.network.BaseURL
import org.mifos.mobilewallet.mifospay.network.MifosWalletOkHttpClient
import org.mifos.mobilewallet.mifospay.network.services.AccountTransfersService
import org.mifos.mobilewallet.mifospay.network.services.AuthenticationService
import org.mifos.mobilewallet.mifospay.network.services.BeneficiaryService
import org.mifos.mobilewallet.mifospay.network.services.ClientService
import org.mifos.mobilewallet.mifospay.network.services.DocumentService
import org.mifos.mobilewallet.mifospay.network.services.InvoiceService
import org.mifos.mobilewallet.mifospay.network.services.KYCLevel1Service
import org.mifos.mobilewallet.mifospay.network.services.NotificationService
import org.mifos.mobilewallet.mifospay.network.services.RegistrationService
import org.mifos.mobilewallet.mifospay.network.services.RunReportService
import org.mifos.mobilewallet.mifospay.network.services.SavedCardService
import org.mifos.mobilewallet.mifospay.network.services.SavingsAccountsService
import org.mifos.mobilewallet.mifospay.network.services.SearchService
import org.mifos.mobilewallet.mifospay.network.services.StandingInstructionService
import org.mifos.mobilewallet.mifospay.network.services.ThirdPartyTransferService
import org.mifos.mobilewallet.mifospay.network.services.TwoFactorAuthService
import org.mifos.mobilewallet.mifospay.network.services.UserService
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    @SelfServiceApi
    fun providesRetrofitSelfService(preferencesHelper: PreferencesHelper): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BaseURL().selfServiceUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .client(MifosWalletOkHttpClient(preferencesHelper).mifosOkHttpClient)
            .build()
    }

    @Provides
    @Singleton
    @FineractApi
    fun providesRetrofitFineract(preferencesHelper: PreferencesHelper): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BaseURL().url)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .client(MifosWalletOkHttpClient(preferencesHelper).mifosOkHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun providesFineractApiManager(
        @Named("FineractAuthenticationService") authenticationService: AuthenticationService,
        @Named("FineractClientService") clientService: ClientService,
        @Named("FineractSavingsAccountsService") savingsAccountsService: SavingsAccountsService,
        @Named("FineractRegistrationService") registrationService: RegistrationService,
        searchService: SearchService,
        documentService: DocumentService,
        runReportService: RunReportService,
        twoFactorAuthService: TwoFactorAuthService,
        accountTransfersService: AccountTransfersService,
        savedCardService: SavedCardService,
        kYCLevel1Service: KYCLevel1Service,
        invoiceService: InvoiceService,
        userService: UserService,
        @Named("FineractThirdPartyTransferService") thirdPartyTransferService: ThirdPartyTransferService,
        standingInstructionService: StandingInstructionService,
        notificationService: NotificationService,
    ): org.mifos.mobilewallet.mifospay.network.FineractApiManager {
        return org.mifos.mobilewallet.mifospay.network.FineractApiManager(
            authenticationService,
            clientService,
            savingsAccountsService,
            registrationService,
            searchService,
            documentService,
            runReportService,
            twoFactorAuthService,
            accountTransfersService,
            savedCardService,
            kYCLevel1Service,
            invoiceService,
            userService,
            thirdPartyTransferService,
            standingInstructionService,
            notificationService
        )
    }

    @Provides
    @Singleton
    fun providesSelfServiceApiManager(
        @Named("SelfServiceAuthenticationService") authenticationService: AuthenticationService,
        @Named("SelfServiceClientService") clientService: ClientService,
        @Named("SelfServiceSavingsAccountsService") savingsAccountsService: SavingsAccountsService,
        @Named("SelfServiceRegistrationService") registrationService: RegistrationService,
        beneficiaryService: BeneficiaryService,
        @Named("SelfServiceThirdPartyTransferService") thirdPartyTransferService: ThirdPartyTransferService,
    ): org.mifos.mobilewallet.mifospay.network.SelfServiceApiManager {
        return org.mifos.mobilewallet.mifospay.network.SelfServiceApiManager(
            authenticationService,
            clientService,
            savingsAccountsService,
            registrationService,
            beneficiaryService,
            thirdPartyTransferService
        )
    }

    //-----Fineract API Service---------//

    @Provides
    @Singleton
    @Named("FineractAuthenticationService")
    fun providesAuthenticationService(@FineractApi retrofit: Retrofit): AuthenticationService {
        return retrofit.create(AuthenticationService::class.java)
    }

    @Provides
    @Singleton
    @Named("FineractClientService")
    fun providesClientService(@FineractApi retrofit: Retrofit): ClientService {
        return retrofit.create(ClientService::class.java)
    }

    @Provides
    @Singleton
    @Named("FineractSavingsAccountsService")
    fun providesSavingsAccountsService(@FineractApi retrofit: Retrofit): SavingsAccountsService {
        return retrofit.create(SavingsAccountsService::class.java)
    }

    @Provides
    @Singleton
    @Named("FineractRegistrationService")
    fun providesRegistrationService(@FineractApi retrofit: Retrofit): RegistrationService {
        return retrofit.create(RegistrationService::class.java)
    }

    @Provides
    @Singleton
    fun providesSearchService(@FineractApi retrofit: Retrofit): SearchService {
        return retrofit.create(SearchService::class.java)
    }

    @Provides
    @Singleton
    fun providesSavedCardService(@FineractApi retrofit: Retrofit): SavedCardService {
        return retrofit.create(SavedCardService::class.java)
    }

    @Provides
    @Singleton
    fun providesDocumentService(@FineractApi retrofit: Retrofit): DocumentService {
        return retrofit.create(DocumentService::class.java)
    }

    @Provides
    @Singleton
    fun provideTwoFactorAuthService(@FineractApi retrofit: Retrofit): TwoFactorAuthService {
        return retrofit.create(TwoFactorAuthService::class.java)
    }

    @Provides
    @Singleton
    fun providesAccountTransfersService(@FineractApi retrofit: Retrofit): AccountTransfersService {
        return retrofit.create(AccountTransfersService::class.java)
    }

    @Provides
    @Singleton
    fun providesRunReportService(@FineractApi retrofit: Retrofit): RunReportService {
        return retrofit.create(RunReportService::class.java)
    }

    @Provides
    @Singleton
    fun providesKYCLevel1Service(@FineractApi retrofit: Retrofit): KYCLevel1Service {
        return retrofit.create(KYCLevel1Service::class.java)
    }

    @Provides
    @Singleton
    fun providesInvoiceService(@FineractApi retrofit: Retrofit): InvoiceService {
        return retrofit.create(InvoiceService::class.java)
    }

    @Provides
    @Singleton
    fun providesUserService(@FineractApi retrofit: Retrofit): UserService {
        return retrofit.create(UserService::class.java)
    }

    @Provides
    @Singleton
    @Named("FineractThirdPartyTransferService")
    fun providesThirdPartyTransferService(@FineractApi retrofit: Retrofit): ThirdPartyTransferService {
        return retrofit.create(ThirdPartyTransferService::class.java)
    }

    @Provides
    @Singleton
    fun providesNotificationService(@FineractApi retrofit: Retrofit): NotificationService {
        return retrofit.create(NotificationService::class.java)
    }

    @Provides
    @Singleton
    fun providesStandingInstructionService(@FineractApi retrofit: Retrofit): StandingInstructionService {
        return retrofit.create(StandingInstructionService::class.java)
    }

    //-------SelfService API Service-------//

    @Provides
    @Singleton
    @Named("SelfServiceAuthenticationService")
    fun providesSelfServiceAuthenticationService(
        @SelfServiceApi retrofit: Retrofit
    ): AuthenticationService {
        return retrofit.create(AuthenticationService::class.java)
    }

    @Provides
    @Singleton
    @Named("SelfServiceClientService")
    fun providesSelfServiceClientService(
        @SelfServiceApi retrofit: Retrofit
    ): ClientService {
        return retrofit.create(ClientService::class.java)
    }

    @Provides
    @Singleton
    @Named("SelfServiceSavingsAccountsService")
    fun providesSelfServiceSavingsAccountsService(
        @SelfServiceApi retrofit: Retrofit
    ): SavingsAccountsService {
        return retrofit.create(SavingsAccountsService::class.java)
    }

    @Provides
    @Singleton
    @Named("SelfServiceRegistrationService")
    fun providesSelfServiceRegistrationService(
        @SelfServiceApi retrofit: Retrofit
    ): RegistrationService {
        return retrofit.create(RegistrationService::class.java)
    }

    @Provides
    @Singleton
    fun providesSelfServiceBeneficiaryService(
        @SelfServiceApi retrofit: Retrofit
    ): BeneficiaryService {
        return retrofit.create(BeneficiaryService::class.java)
    }

    @Provides
    @Singleton
    @Named("SelfServiceThirdPartyTransferService")
    fun providesSelfServiceThirdPartyTransferService(
        @SelfServiceApi retrofit: Retrofit
    ): ThirdPartyTransferService {
        return retrofit.create(ThirdPartyTransferService::class.java)
    }
}