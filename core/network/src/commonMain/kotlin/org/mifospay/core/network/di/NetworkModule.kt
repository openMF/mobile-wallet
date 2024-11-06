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

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import org.koin.dsl.module
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.network.FineractApiManager
import org.mifospay.core.network.KtorfitClient
import org.mifospay.core.network.SelfServiceApiManager
import org.mifospay.core.network.ktorHttpClient
import org.mifospay.core.network.utils.BaseURL
import org.mifospay.core.network.utils.KtorInterceptor

val NetworkModule = module {
    single<HttpClient>(KtorClient) {
        val preferencesRepository = get<UserPreferencesRepository>()

        ktorHttpClient.config {
            install(Auth)
            install(KtorInterceptor) {
                getToken = { preferencesRepository.authToken }
            }
        }
    }

    // TODO:: This could be removed, added for testing
    single<HttpClient>(KtorBaseClient) {
        ktorHttpClient.config {
            install(Auth) {
                basic {
                    sendWithoutRequest { true }
                    credentials {
                        BasicAuthCredentials(
                            username = "mifos",
                            password = "password",
                        )
                    }
                }
            }

            defaultRequest {
                header("Fineract-Platform-TenantId", "venus")
                header("Content-Type", "application/json")
                header("Accept", "application/json")
            }
        }
    }

    single<KtorfitClient>(BaseClient) {
        KtorfitClient.builder()
            .httpClient(get(KtorBaseClient))
            .baseURL(BaseURL.url)
            .build()
    }

    single<KtorfitClient>(SelfClient) {
        KtorfitClient.builder()
            .httpClient(get(KtorClient))
            .baseURL(BaseURL.selfServiceUrl)
            .build()
    }

    single {
        FineractApiManager(ktorfitClient = get(BaseClient))
    }

    single {
        SelfServiceApiManager(ktorfitClient = get(SelfClient))
    }
}
