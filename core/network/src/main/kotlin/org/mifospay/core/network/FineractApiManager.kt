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
import javax.inject.Inject

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
    private val ktorSavingsAccountService: KtorSavingsAccountService,
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

    val ktorSavingsAccountApi: KtorSavingsAccountService
        get() = ktorSavingsAccountService
}
