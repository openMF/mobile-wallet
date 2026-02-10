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
import org.mifos.corebase.network.DynamicBaseUrlPlugin
import org.mifos.corebase.network.DynamicLoggableHosts
import org.mifos.corebase.network.MultiUrlConfigProvider
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
        val configManager = get<InstanceConfigManager>()
        KtorfitClient(
            Ktorfit.Builder()
                .httpClient(
                    client = httpClient(
                        config = setupDefaultHttpClient(
                            // Use placeholder - actual URL is set dynamically by DynamicBaseUrlPlugin
                            baseUrl = "https://placeholder.local/",
                            // Dynamic loggable hosts - reads current endpoints from configManager
                            loggableHosts = DynamicLoggableHosts(configManager),
                        ),
                    ).config {
                        install(DynamicBaseUrlPlugin) {
                            multiConfigProvider = configManager
                            urlType = MultiUrlConfigProvider.UrlType.SELF_SERVICE
                        }
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
        val configManager = get<InstanceConfigManager>()
        KtorfitClient(
            Ktorfit.Builder()
                .httpClient(
                    client = httpClient(
                        config = setupDefaultHttpClient(
                            // Use placeholder - actual URL is set dynamically by DynamicBaseUrlPlugin
                            baseUrl = "https://placeholder.local/",
                            basicCredentialsProvider = {
                                BasicAuthCredentials(
                                    username = "mifos",
                                    password = "password",
                                )
                            },
                            // Headers are set dynamically by KtorInterceptor
                            defaultHeaders = mapOf(
                                BaseURL.HEADER_CONTENT_TYPE to BaseURL.HEADER_CONTENT_TYPE_VALUE,
                                BaseURL.HEADER_ACCEPT to BaseURL.HEADER_ACCEPT_VALUE,
                            ),
                            // Dynamic loggable hosts - reads current endpoints from configManager
                            loggableHosts = DynamicLoggableHosts(configManager),
                        ),
                    ).config {
                        install(DynamicBaseUrlPlugin) {
                            multiConfigProvider = configManager
                            urlType = MultiUrlConfigProvider.UrlType.MAIN
                        }
                        install(KtorInterceptor) {
                            getToken = { null }
                            this.configManager = configManager
                        }
                    },
                )
                .converterFactories(
                    FlowConverterFactory(),
                )
                .build(),
        )
    }

    single<KtorfitClient>(qualifier = InterBankClient) {
        val configManager = get<InstanceConfigManager>()
        KtorfitClient(
            Ktorfit.Builder()
                .httpClient(
                    client = httpClient(
                        config = setupDefaultHttpClient(
                            // Use placeholder - actual URL is set dynamically by DynamicBaseUrlPlugin
                            baseUrl = "https://placeholder.local/",
                            // Headers are set dynamically by KtorInterceptor
                            defaultHeaders = mapOf(
                                BaseURL.HEADER_CONTENT_TYPE to BaseURL.HEADER_CONTENT_TYPE_VALUE,
                                BaseURL.HEADER_ACCEPT to BaseURL.HEADER_ACCEPT_VALUE,
                            ),
                            // Dynamic loggable hosts - reads current endpoints from configManager
                            loggableHosts = DynamicLoggableHosts(configManager),
                        ),
                    ).config {
                        install(DynamicBaseUrlPlugin) {
                            multiConfigProvider = configManager
                            urlType = MultiUrlConfigProvider.UrlType.INTERBANK
                        }
                        install(KtorInterceptor) {
                            getToken = { null }
                            this.configManager = configManager
                        }
                    },
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
