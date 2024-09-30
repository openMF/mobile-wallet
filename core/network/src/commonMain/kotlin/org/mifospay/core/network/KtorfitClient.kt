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
import de.jensklingenberg.ktorfit.converter.CallConverterFactory
import de.jensklingenberg.ktorfit.converter.FlowConverterFactory
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.mifospay.core.network.services.AccountTransfersService
import org.mifospay.core.network.services.AuthenticationService
import org.mifospay.core.network.services.BeneficiaryService
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

class KtorfitClient(
    private val httpClient: HttpClient,
    private val ktorfit: Ktorfit,
) {
    internal val authenticationApi: AuthenticationService = ktorfit.createAuthenticationService()

    internal val clientsApi: ClientService = ktorfit.createClientService()

    internal val registrationAPi: RegistrationService = ktorfit.createRegistrationService()

    internal val searchApi: SearchService = ktorfit.createSearchService()

    internal val documentApi: DocumentService = ktorfit.createDocumentService()

    internal val runReportApi: RunReportService = ktorfit.createRunReportService()

    internal val twoFactorAuthApi: TwoFactorAuthService = ktorfit.createTwoFactorAuthService()

    internal val accountTransfersApi: AccountTransfersService =
        ktorfit.createAccountTransfersService()

    internal val savedCardApi: SavedCardService = ktorfit.createSavedCardService()

    internal val kycLevel1Api: KYCLevel1Service = ktorfit.createKYCLevel1Service()

    internal val invoiceApi: InvoiceService = ktorfit.createInvoiceService()

    internal val userApi: UserService = ktorfit.createUserService()

    internal val thirdPartyTransferApi: ThirdPartyTransferService =
        ktorfit.createThirdPartyTransferService()

    internal val notificationApi: NotificationService = ktorfit.createNotificationService()

    internal val savingsAccountsApi: SavingsAccountsService = ktorfit.createSavingsAccountsService()

    internal val standingInstructionApi: StandingInstructionService =
        ktorfit.createStandingInstructionService()

    internal val beneficiaryApi: BeneficiaryService = ktorfit.createBeneficiaryService()

    class Builder internal constructor() {
        private lateinit var baseURL: String
        private var tenant: String? = BaseURL.DEFAULT
        private var loginUsername: String? = null
        private var loginPassword: String? = null
        private var insecure: Boolean = false
        private var token: String? = null

        fun baseURL(baseURL: String): Builder {
            this.baseURL = baseURL
            return this
        }

        fun tenant(tenant: String?): Builder {
            this.tenant = tenant
            return this
        }

        fun basicAuth(username: String?, password: String?): Builder {
            this.loginUsername = username
            this.loginPassword = password
            return this
        }

        fun inSecure(insecure: Boolean): Builder {
            this.insecure = insecure
            return this
        }

        fun token(token: String?): Builder {
            this.token = token
            return this
        }

        fun build(): KtorfitClient {
            val ktorClient = HttpClient(CIO) {
                install(ContentNegotiation) {
                    json(
                        Json {
                            isLenient = true
                            ignoreUnknownKeys = true
                        },
                    )
                }

                install(DefaultRequest)

                install(Logging) {
                    logger = Logger.DEFAULT
                    level = LogLevel.INFO
                }

                defaultRequest {
                    contentType(ContentType.Application.Json)
                    headers {
                        append("Accept", "application/json")
                        tenant?.let {
                            append(BaseURL.HEADER_TENANT, it)
                        }
                        token?.let {
                            append(BaseURL.HEADER_AUTH, it)
                        }
                    }
                }
            }

            val ktorfitBuilder = Ktorfit.Builder()
                .httpClient(ktorClient)
                .baseUrl(baseURL)
                .converterFactories(CallConverterFactory())
                .converterFactories(FlowConverterFactory())
                .build()

            return KtorfitClient(ktorClient, ktorfitBuilder)
        }
    }

    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }
}
