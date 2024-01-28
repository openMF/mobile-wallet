package org.mifos.mobilewallet.mifospay.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.mifos.mobilewallet.core.data.fineract.api.BaseURL
import org.mifos.mobilewallet.core.data.fineract.api.MifosWalletOkHttpClient
import org.mifos.mobilewallet.core.data.fineract.api.services.AccountTransfersService
import org.mifos.mobilewallet.core.data.fineract.api.services.AuthenticationService
import org.mifos.mobilewallet.core.data.fineract.api.services.BeneficiaryService
import org.mifos.mobilewallet.core.data.fineract.api.services.ClientService
import org.mifos.mobilewallet.core.data.fineract.api.services.DocumentService
import org.mifos.mobilewallet.core.data.fineract.api.services.InvoiceService
import org.mifos.mobilewallet.core.data.fineract.api.services.KYCLevel1Service
import org.mifos.mobilewallet.core.data.fineract.api.services.NotificationService
import org.mifos.mobilewallet.core.data.fineract.api.services.RegistrationService
import org.mifos.mobilewallet.core.data.fineract.api.services.RunReportService
import org.mifos.mobilewallet.core.data.fineract.api.services.SavedCardService
import org.mifos.mobilewallet.core.data.fineract.api.services.SavingsAccountsService
import org.mifos.mobilewallet.core.data.fineract.api.services.SearchService
import org.mifos.mobilewallet.core.data.fineract.api.services.StandingInstructionService
import org.mifos.mobilewallet.core.data.fineract.api.services.ThirdPartyTransferService
import org.mifos.mobilewallet.core.data.fineract.api.services.TwoFactorAuthService
import org.mifos.mobilewallet.core.data.fineract.api.services.UserService
import org.mifos.mobilewallet.core.data.fineract.local.PreferencesHelper
import org.mifos.mobilewallet.core.di.FineractApi
import org.mifos.mobilewallet.core.di.SelfServiceApi
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