/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.network

import org.mifospay.core.network.services.AuthenticationService
import org.mifospay.core.network.services.BeneficiaryService
import org.mifospay.core.network.services.ClientService
import org.mifospay.core.network.services.RegistrationService
import org.mifospay.core.network.services.SavingsAccountsService
import org.mifospay.core.network.services.ThirdPartyTransferService

class SelfServiceApiManager(
    private val ktorfitClient: KtorfitClient,
) {
    val authenticationApi: AuthenticationService
        get() = ktorfitClient.authenticationApi
    val clientsApi: ClientService
        get() = ktorfitClient.clientsApi
    val savingAccountsListApi: SavingsAccountsService
        get() = ktorfitClient.savingsAccountsApi
    val registrationAPi: RegistrationService
        get() = ktorfitClient.registrationAPi
    val beneficiaryApi: BeneficiaryService
        get() = ktorfitClient.beneficiaryApi
    val thirdPartyTransferApi: ThirdPartyTransferService
        get() = ktorfitClient.thirdPartyTransferApi
}
