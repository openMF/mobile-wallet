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
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import org.koin.dsl.module
import org.mifos.corebase.network.httpClient
import org.mifos.corebase.network.setupDefaultHttpClient
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.network.FineractApiManager
import org.mifospay.core.network.KtorfitClient
import org.mifospay.core.network.SelfServiceApiManager
import org.mifospay.core.network.ktorHttpClient
import org.mifospay.core.network.utils.BaseURL
import org.mifospay.core.network.utils.FlowConverterFactory
import org.mifospay.core.network.utils.KtorInterceptor
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
val NetworkModule = module {
    single<HttpClient> {
        val preferencesRepository = get<UserPreferencesRepository>()

        ktorHttpClient.config {
            install(Auth)
            install(KtorInterceptor) {
                getToken = { preferencesRepository.authToken }
            }
        }
    }

    single<KtorfitClient>(qualifier = SelfClient) {
        KtorfitClient(
            Ktorfit.Builder()
                .httpClient(client = get<HttpClient>())
                .baseUrl(BaseURL.selfServiceUrl)
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
                                "Fineract-Platform-TenantId" to "venus",
                                "Content-Type" to "application/json",
                                "Accept" to "application/json",
                            ),
                            loggableHosts = listOf("venus.mifos.community"),
                        ),
                    ),
                )
                .converterFactories(
                    FlowConverterFactory(),
                )
                .build(),
        )
    }

    single {
        FineractApiManager(ktorfitClient = get(BaseClient))
    }

    single {
        SelfServiceApiManager(ktorfitClient = get(SelfClient))
    }
}
