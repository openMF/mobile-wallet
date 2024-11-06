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

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import org.mifospay.core.network.services.createAccountTransfersService
import org.mifospay.core.network.services.createAuthenticationService
import org.mifospay.core.network.services.createBeneficiaryService
import org.mifospay.core.network.services.createClientService
import org.mifospay.core.network.services.createDocumentService
import org.mifospay.core.network.services.createInvoiceService
import org.mifospay.core.network.services.createKYCLevel1Service
import org.mifospay.core.network.services.createNotificationService
import org.mifospay.core.network.services.createRegistrationService
import org.mifospay.core.network.services.createRunReportService
import org.mifospay.core.network.services.createSavedCardService
import org.mifospay.core.network.services.createSavingsAccountsService
import org.mifospay.core.network.services.createSearchService
import org.mifospay.core.network.services.createStandingInstructionService
import org.mifospay.core.network.services.createThirdPartyTransferService
import org.mifospay.core.network.services.createTwoFactorAuthService
import org.mifospay.core.network.services.createUserService
import org.mifospay.core.network.utils.FlowConverterFactory

class KtorfitClient(
    ktorfit: Ktorfit,
) {
    internal val authenticationApi by lazy { ktorfit.createAuthenticationService() }

    internal val clientsApi by lazy { ktorfit.createClientService() }

    internal val registrationAPi by lazy { ktorfit.createRegistrationService() }

    internal val searchApi by lazy { ktorfit.createSearchService() }

    internal val documentApi by lazy { ktorfit.createDocumentService() }

    internal val runReportApi by lazy { ktorfit.createRunReportService() }

    internal val twoFactorAuthApi by lazy { ktorfit.createTwoFactorAuthService() }

    internal val accountTransfersApi by lazy { ktorfit.createAccountTransfersService() }

    internal val savedCardApi by lazy { ktorfit.createSavedCardService() }

    internal val kycLevel1Api by lazy { ktorfit.createKYCLevel1Service() }

    internal val invoiceApi by lazy { ktorfit.createInvoiceService() }

    internal val userApi by lazy { ktorfit.createUserService() }

    internal val thirdPartyTransferApi by lazy { ktorfit.createThirdPartyTransferService() }

    internal val notificationApi by lazy { ktorfit.createNotificationService() }

    internal val savingsAccountsApi by lazy { ktorfit.createSavingsAccountsService() }

    internal val standingInstructionApi by lazy { ktorfit.createStandingInstructionService() }

    internal val beneficiaryApi by lazy { ktorfit.createBeneficiaryService() }

    class Builder internal constructor() {
        private lateinit var baseURL: String
        private lateinit var httpClient: HttpClient

        fun baseURL(baseURL: String): Builder {
            this.baseURL = baseURL
            return this
        }

        fun httpClient(ktorHttpClient: HttpClient): Builder {
            this.httpClient = ktorHttpClient
            return this
        }

        fun build(): KtorfitClient {
            val ktorfitBuilder = Ktorfit.Builder()
                .httpClient(httpClient)
                .baseUrl(baseURL)
                .converterFactories(FlowConverterFactory())
                .build()

            return KtorfitClient(ktorfitBuilder)
        }
    }

    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }
}
