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
import org.mifos.mobilewallet.core.data.fineract.api.SelfServiceApiManager
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
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import org.mifos.mobilewallet.mifospay.data.local.LocalRepository
import org.mifos.mobilewallet.core.data.fineract.local.PreferencesHelper
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    fun provideUseCaseThreadPoolScheduler(): UseCaseThreadPoolScheduler =
        UseCaseThreadPoolScheduler()

    @Provides
    fun providesUseCaseHandler(useCaseThreadPoolScheduler: UseCaseThreadPoolScheduler): UseCaseHandler {
        return UseCaseHandler(useCaseThreadPoolScheduler)
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
    ): FineractApiManager {
        return FineractApiManager(
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
    ): SelfServiceApiManager {
        return SelfServiceApiManager(
            authenticationService,
            clientService,
            savingsAccountsService,
            registrationService,
            beneficiaryService,
            thirdPartyTransferService
        )
    }

    @Provides
    fun providesFineractRepository(
        fineractApiManager: FineractApiManager,
        selfServiceApiManager: SelfServiceApiManager
    ): FineractRepository {
        return FineractRepository(fineractApiManager, selfServiceApiManager)
    }

    @Provides
    @Singleton
    fun prefManager(@ApplicationContext context: Context): PreferencesHelper {
        return PreferencesHelper(context)
    }

    @Provides
    fun providesLocalRepository(preferencesHelper: PreferencesHelper): LocalRepository {
        return LocalRepository(preferencesHelper)
    }
}
