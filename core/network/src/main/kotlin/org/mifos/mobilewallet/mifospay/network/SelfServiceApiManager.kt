package org.mifos.mobilewallet.mifospay.network

import org.mifos.mobilewallet.mifospay.network.services.AuthenticationService
import org.mifos.mobilewallet.mifospay.network.services.BeneficiaryService
import org.mifos.mobilewallet.mifospay.network.services.ClientService
import org.mifos.mobilewallet.mifospay.network.services.RegistrationService
import org.mifos.mobilewallet.mifospay.network.services.SavingsAccountsService
import org.mifos.mobilewallet.mifospay.network.services.ThirdPartyTransferService
import javax.inject.Inject

class SelfServiceApiManager @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val clientService: ClientService,
    private val savingsAccountsService: SavingsAccountsService,
    private val registrationService: RegistrationService,
    private val beneficiaryService: BeneficiaryService,
    private val thirdPartyTransferService: ThirdPartyTransferService,
) {
    val authenticationApi: AuthenticationService
        get() = authenticationService
    val clientsApi: ClientService
        get() = clientService
    val savingAccountsListApi: SavingsAccountsService
        get() = savingsAccountsService
    val registrationAPi: RegistrationService
        get() = registrationService
    val beneficiaryApi: BeneficiaryService
        get() = beneficiaryService
    val thirdPartyTransferApi: ThirdPartyTransferService
        get() = thirdPartyTransferService
}
