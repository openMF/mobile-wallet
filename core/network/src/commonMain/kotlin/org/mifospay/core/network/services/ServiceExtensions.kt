/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.network.services

import de.jensklingenberg.ktorfit.Ktorfit

fun Ktorfit.createAccountTransfersService(): AccountTransfersService =
    this.create()

fun Ktorfit.createAuthenticationService(): AuthenticationService =
    this.create()

fun Ktorfit.createBeneficiaryService(): BeneficiaryService =
    this.create()

fun Ktorfit.createClientService(): ClientService =
    this.create()

fun Ktorfit.createDocumentService(): DocumentService =
    this.create()

fun Ktorfit.createInterBankService(): InterBankService =
    this.create()

fun Ktorfit.createInvoiceService(): InvoiceService =
    this.create()

fun Ktorfit.createKYCLevel1Service(): KYCLevel1Service =
    this.create()

fun Ktorfit.createNotificationService(): NotificationService =
    this.create()

fun Ktorfit.createOfficeService(): OfficeService =
    this.create()

fun Ktorfit.createPocketService(): PocketService =
    this.create()

fun Ktorfit.createRegistrationService(): RegistrationService =
    this.create()

fun Ktorfit.createRunReportService(): RunReportService =
    this.create()

fun Ktorfit.createSavedCardService(): SavedCardService =
    this.create()

fun Ktorfit.createSavingsAccountsService(): SavingsAccountsService =
    this.create()

fun Ktorfit.createSearchService(): SearchService =
    this.create()

fun Ktorfit.createStandingInstructionService(): StandingInstructionService =
    this.create()

fun Ktorfit.createThirdPartyTransferService(): ThirdPartyTransferService =
    this.create()

fun Ktorfit.createTwoFactorAuthService(): TwoFactorAuthService =
    this.create()

fun Ktorfit.createUserService(): UserService =
    this.create()
