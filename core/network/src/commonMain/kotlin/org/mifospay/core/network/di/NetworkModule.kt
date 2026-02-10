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
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.mifos.corebase.network.httpClient
import org.mifos.corebase.network.setupDefaultHttpClient
import org.mifospay.core.common.MifosDispatchers
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.network.FineractApiManager
import org.mifospay.core.network.InterBankApiManager
import org.mifospay.core.network.KtorfitClient
import org.mifospay.core.network.SelfServiceApiManager
import org.mifospay.core.network.config.InstanceConfigLoader
import org.mifospay.core.network.config.InstanceConfigManager
import org.mifospay.core.network.config.SupabaseInstanceConfigLoader
import org.mifospay.core.network.utils.BaseURL
import org.mifospay.core.network.utils.FlowConverterFactory
import org.mifospay.core.network.utils.KtorInterceptor
import kotlin.io.encoding.ExperimentalEncodingApi

private val ioDispatcher = named(MifosDispatchers.IO.name)

@OptIn(ExperimentalEncodingApi::class)
val NetworkModule = module {
    single<InstanceConfigLoader> {
        SupabaseInstanceConfigLoader(
            ioDispatcher = get(ioDispatcher),
            json = get(),
        )
    }

    single {
        InstanceConfigManager(userPreferencesRepository = get())
    }

    single {
        BaseURL(configManager = get())
    }

    single<KtorfitClient>(qualifier = SelfClient) {
        val preferencesRepository = get<UserPreferencesRepository>()
        val baseURL = get<BaseURL>()
        val configManager = get<InstanceConfigManager>()
        KtorfitClient(
            Ktorfit.Builder()
                .httpClient(
                    client = httpClient(
                        config = setupDefaultHttpClient(
                            baseUrl = baseURL.selfServiceUrl,
                            loggableHosts = listOf(configManager.getEndpoint()),
                        ),
                    ).config {
                        install(KtorInterceptor) {
                            getToken = { preferencesRepository.authToken }
                            this.configManager = configManager
                        }
                    },
                )
                .converterFactories(FlowConverterFactory())
                .build(),
        )
    }

    single<KtorfitClient>(qualifier = BaseClient) {
        val baseURL = get<BaseURL>()
        val configManager = get<InstanceConfigManager>()
        KtorfitClient(
            Ktorfit.Builder()
                .httpClient(
                    client = httpClient(
                        config = setupDefaultHttpClient(
                            baseUrl = baseURL.url,
                            basicCredentialsProvider = {
                                BasicAuthCredentials(
                                    username = "mifos",
                                    password = "password",
                                )
                            },
                            defaultHeaders = mapOf(
                                BaseURL.HEADER_TENANT to configManager.getPlatformTenantId(),
                                BaseURL.HEADER_CONTENT_TYPE to BaseURL.HEADER_CONTENT_TYPE_VALUE,
                                BaseURL.HEADER_ACCEPT to BaseURL.HEADER_ACCEPT_VALUE,
                            ),
                            loggableHosts = listOf(configManager.getEndpoint()),
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
        val baseURL = get<BaseURL>()
        val configManager = get<InstanceConfigManager>()
        KtorfitClient(
            Ktorfit.Builder()
                .httpClient(
                    client = httpClient(
                        config = setupDefaultHttpClient(
                            baseUrl = baseURL.interBankUrl,
                            defaultHeaders = mapOf(
                                BaseURL.HEADER_TENANT to configManager.getPlatformTenantId(),
                                BaseURL.HEADER_CONTENT_TYPE to BaseURL.HEADER_CONTENT_TYPE_VALUE,
                                BaseURL.HEADER_ACCEPT to BaseURL.HEADER_ACCEPT_VALUE,
                            ),
                            loggableHosts = listOf(configManager.getEndpoint()),
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
