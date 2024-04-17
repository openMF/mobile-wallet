package org.mifospay.core.network

import org.mifospay.core.network.services.AuthenticationService
import org.mifospay.core.network.services.BeneficiaryService
import org.mifospay.core.network.services.ClientService
import org.mifospay.core.network.services.RegistrationService
import org.mifospay.core.network.services.SavingsAccountsService
import org.mifospay.core.network.services.ThirdPartyTransferService
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
