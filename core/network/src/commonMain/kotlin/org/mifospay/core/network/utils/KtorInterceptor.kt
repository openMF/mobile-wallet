/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.network.utils

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.header
import io.ktor.util.AttributeKey
import org.mifospay.core.datastore.UserPreferencesRepository

class KtorInterceptor(
    private val repository: UserPreferencesRepository,
) {
    companion object Plugin : HttpClientPlugin<Config, KtorInterceptor> {
        private const val HEADER_TENANT = "Fineract-Platform-TenantId"
        private const val HEADER_AUTH = "Authorization"
        private const val DEFAULT = "venus"

        override val key: AttributeKey<KtorInterceptor> = AttributeKey("KtorInterceptor")

        override fun install(plugin: KtorInterceptor, scope: HttpClient) {
            val authToken = plugin.repository.token.value

            scope.requestPipeline.intercept(HttpRequestPipeline.State) {
                context.header("Content-Type", "application/json")
                context.header("Accept", "application/json")
                context.header(HEADER_TENANT, DEFAULT)
                if (!authToken.isNullOrEmpty()) {
                    context.header(HEADER_AUTH, "Basic $authToken")
                }
            }
        }

        override fun prepare(block: Config.() -> Unit): KtorInterceptor {
            val config = Config().apply(block)
            return KtorInterceptor(config.preferenceRepository!!)
        }
    }
}

class Config(
    var preferenceRepository: UserPreferencesRepository? = null,
)
