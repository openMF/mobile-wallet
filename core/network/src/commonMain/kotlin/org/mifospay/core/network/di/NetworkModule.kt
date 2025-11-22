/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.network.di

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import org.koin.dsl.module
import org.mifos.corebase.network.httpClient
import org.mifos.corebase.network.setupDefaultHttpClient
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.network.FineractApiManager
import org.mifospay.core.network.InterBankApiManager
import org.mifospay.core.network.KtorfitClient
import org.mifospay.core.network.SelfServiceApiManager
import org.mifospay.core.network.utils.BaseURL
import org.mifospay.core.network.utils.FlowConverterFactory
import org.mifospay.core.network.utils.KtorInterceptor
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
val NetworkModule = module {
    single<KtorfitClient>(qualifier = SelfClient) {
        val preferencesRepository = get<UserPreferencesRepository>()
        KtorfitClient(
            Ktorfit.Builder()
                .httpClient(
                    client = httpClient(
                        config = setupDefaultHttpClient(
                            baseUrl = BaseURL.selfServiceUrl,
                            loggableHosts = listOf("mifos-bank-1.mifos.community"),
                        ),
                    ).config {
                        install(KtorInterceptor) {
                            getToken = { preferencesRepository.authToken }
                        }
                    },
                )
                .converterFactories(FlowConverterFactory())
                .build(),
        )
    }

    single<KtorfitClient>(qualifier = BaseClient) {
        KtorfitClient(
            Ktorfit.Builder()
                .httpClient(
                    client = httpClient(
                        config = setupDefaultHttpClient(
                            baseUrl = BaseURL.url,
                            basicCredentialsProvider = {
                                BasicAuthCredentials(
                                    username = "mifos",
                                    password = "password",
                                )
                            },
                            defaultHeaders = mapOf(
                                "Fineract-Platform-TenantId" to "mifos-bank-1",
                                "Content-Type" to "application/json",
                                "Accept" to "application/json",
                            ),
                            loggableHosts = listOf("mifos-bank-1.mifos.community", "apis.flexcore.mx"),
                        ),
                    ),
                )
                .converterFactories(
                    FlowConverterFactory(),
                )
                .build(),
        )
    }

    single<KtorfitClient>(qualifier = InterBankClient) {
        KtorfitClient(
            Ktorfit.Builder()
                .httpClient(
                    client = httpClient(
                        config = setupDefaultHttpClient(
                            baseUrl = BaseURL.interBankUrl,
                            defaultHeaders = mapOf(
                                "Fineract-Platform-TenantId" to BaseURL.FINERACT_PLATFORM_TENANT_ID,
                                "Content-Type" to "application/json",
                                "Accept" to "application/json",
                            ),
                            loggableHosts = listOf("apis.flexcore.mx"),
                        ),
                    ),
                )
                .converterFactories(FlowConverterFactory())
                .build(),
        )
    }

    single {
        FineractApiManager(ktorfitClient = get(BaseClient))
    }

    single {
        SelfServiceApiManager(ktorfitClient = get(SelfClient))
    }

    single {
        InterBankApiManager(ktorfitClient = get(InterBankClient))
    }
}
