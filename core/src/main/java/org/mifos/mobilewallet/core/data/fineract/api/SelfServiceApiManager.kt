package org.mifos.mobilewallet.core.data.fineract.api

import org.mifos.mobilewallet.core.data.fineract.api.services.AuthenticationService
import org.mifos.mobilewallet.core.data.fineract.api.services.BeneficiaryService
import org.mifos.mobilewallet.core.data.fineract.api.services.ClientService
import org.mifos.mobilewallet.core.data.fineract.api.services.RegistrationService
import org.mifos.mobilewallet.core.data.fineract.api.services.SavingsAccountsService
import org.mifos.mobilewallet.core.data.fineract.api.services.ThirdPartyTransferService
import javax.inject.Inject

/**
 * Created by naman on 20/8/17.
 */
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
