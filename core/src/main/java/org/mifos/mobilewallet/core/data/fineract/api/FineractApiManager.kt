package org.mifos.mobilewallet.core.data.fineract.api

import org.mifos.mobilewallet.core.data.fineract.api.services.AccountTransfersService
import org.mifos.mobilewallet.core.data.fineract.api.services.AuthenticationService
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
import javax.inject.Inject

/**
 * Created by naman on 17/6/17.
 */
class FineractApiManager @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val clientService: ClientService,
    private val savingsAccountsService: SavingsAccountsService,
    private val registrationService: RegistrationService,
    private val searchService: SearchService,
    private val documentService: DocumentService,
    private val runReportService: RunReportService,
    private val twoFactorAuthService: TwoFactorAuthService,
    private val accountTransfersService: AccountTransfersService,
    private val savedCardService: SavedCardService,
    private val kYCLevel1Service: KYCLevel1Service,
    private val invoiceService: InvoiceService,
    private val userService: UserService,
    private val thirdPartyTransferService: ThirdPartyTransferService,
    private val standingInstructionService: StandingInstructionService,
    private val notificationService: NotificationService,
) {

    val authenticationApi: AuthenticationService
        get() = authenticationService

    val clientsApi: ClientService
        get() = clientService

    val registrationAPi: RegistrationService
        get() = registrationService

    val searchApi: SearchService
        get() = searchService

    val documentApi: DocumentService
        get() = documentService

    val runReportApi: RunReportService
        get() = runReportService

    val twoFactorAuthApi: TwoFactorAuthService
        get() = twoFactorAuthService

    val accountTransfersApi: AccountTransfersService
        get() = accountTransfersService

    val savedCardApi: SavedCardService
        get() = savedCardService

    val kycLevel1Api: KYCLevel1Service
        get() = kYCLevel1Service

    val invoiceApi: InvoiceService
        get() = invoiceService

    val userApi: UserService
        get() = userService

    val thirdPartyTransferApi: ThirdPartyTransferService
        get() = thirdPartyTransferService

    val notificationApi: NotificationService
        get() = notificationService

    val savingsAccountsApi: SavingsAccountsService
        get() = savingsAccountsService

    val standingInstructionApi: StandingInstructionService
        get() = standingInstructionService
}
