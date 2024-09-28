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

import org.mifospay.core.network.services.AccountTransfersService
import org.mifospay.core.network.services.AuthenticationService
import org.mifospay.core.network.services.ClientService
import org.mifospay.core.network.services.DocumentService
import org.mifospay.core.network.services.InvoiceService
import org.mifospay.core.network.services.KYCLevel1Service
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

class FineractApiManager(
    private val ktorfitClient: KtorfitClient,
) {

    val authenticationApi: AuthenticationService
        get() = ktorfitClient.authenticationApi

    val clientsApi: ClientService
        get() = ktorfitClient.clientsApi

    val registrationAPi: RegistrationService
        get() = ktorfitClient.registrationAPi

    val searchApi: SearchService
        get() = ktorfitClient.searchApi

    val documentApi: DocumentService
        get() = ktorfitClient.documentApi

    val runReportApi: RunReportService
        get() = ktorfitClient.runReportApi

    val twoFactorAuthApi: TwoFactorAuthService
        get() = ktorfitClient.twoFactorAuthApi

    val accountTransfersApi: AccountTransfersService
        get() = ktorfitClient.accountTransfersApi

    val savedCardApi: SavedCardService
        get() = ktorfitClient.savedCardApi

    val kycLevel1Api: KYCLevel1Service
        get() = ktorfitClient.kycLevel1Api

    val invoiceApi: InvoiceService
        get() = ktorfitClient.invoiceApi

    val userApi: UserService
        get() = ktorfitClient.userApi

    val thirdPartyTransferApi: ThirdPartyTransferService
        get() = ktorfitClient.thirdPartyTransferApi

    val notificationApi: NotificationService
        get() = ktorfitClient.notificationApi

    val savingsAccountsApi: SavingsAccountsService
        get() = ktorfitClient.savingsAccountsApi

    val standingInstructionApi: StandingInstructionService
        get() = ktorfitClient.standingInstructionApi
}
