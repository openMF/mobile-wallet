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
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.http.encodedPath
import io.ktor.http.takeFrom
import io.ktor.util.AttributeKey
import org.mifospay.core.network.config.InstanceConfigManager

/**
 * Ktor plugin that dynamically sets the base URL for each request based on
 * the currently selected instance from [InstanceConfigManager].
 *
 * This allows the app to switch between different server instances at runtime
 * without recreating the HTTP client.
 */
class DynamicBaseUrlPlugin private constructor(
    private val configManager: InstanceConfigManager,
    private val urlType: UrlType,
) {
    enum class UrlType {
        MAIN,
        SELF_SERVICE,
        INTERBANK,
    }

    companion object Plugin : HttpClientPlugin<DynamicBaseUrlConfig, DynamicBaseUrlPlugin> {
        override val key: AttributeKey<DynamicBaseUrlPlugin> =
            AttributeKey("DynamicBaseUrlPlugin")

        override fun prepare(block: DynamicBaseUrlConfig.() -> Unit): DynamicBaseUrlPlugin {
            val config = DynamicBaseUrlConfig().apply(block)
            return DynamicBaseUrlPlugin(config.configManager, config.urlType)
        }

        override fun install(plugin: DynamicBaseUrlPlugin, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.Before) {
                val currentBaseUrl = when (plugin.urlType) {
                    UrlType.MAIN -> plugin.configManager.getUrl()
                    UrlType.SELF_SERVICE -> plugin.configManager.getSelfServiceUrl()
                    UrlType.INTERBANK -> plugin.configManager.getInterbankUrl()
                }

                val originalUrl = context.url.build()
                val newUrl = rebuildUrl(originalUrl, currentBaseUrl)

                context.url.takeFrom(newUrl)
            }
        }

        /**
         * Rebuilds the request URL using the dynamic base URL while preserving
         * the original path and query parameters.
         */
        private fun rebuildUrl(originalUrl: Url, baseUrl: String): Url {
            val baseUrlParsed = Url(baseUrl)

            return URLBuilder().apply {
                protocol = baseUrlParsed.protocol
                host = baseUrlParsed.host
                port = baseUrlParsed.port

                // Combine base path with original path
                val basePath = baseUrlParsed.encodedPath.trimEnd('/')
                val originalPath = originalUrl.encodedPath.trimStart('/')

                encodedPath = if (originalPath.isNotEmpty()) {
                    "$basePath/$originalPath"
                } else {
                    basePath
                }

                // Preserve query parameters
                originalUrl.parameters.forEach { name, values ->
                    parameters.appendAll(name, values)
                }

                // Preserve fragment
                fragment = originalUrl.fragment
            }.build()
        }
    }

    /**
     * Returns the current loggable hosts based on the URL type.
     * This can be used to configure Ktor's Logging plugin dynamically.
     */
    fun getLoggableHosts(): List<String> {
        return when (urlType) {
            UrlType.MAIN -> listOf(configManager.getEndpoint())
            UrlType.SELF_SERVICE -> listOf(configManager.getEndpoint())
            UrlType.INTERBANK -> listOf(configManager.getCurrentInterbankInstance().endpoint)
        }
    }
}

class DynamicBaseUrlConfig {
    lateinit var configManager: InstanceConfigManager
    var urlType: DynamicBaseUrlPlugin.UrlType = DynamicBaseUrlPlugin.UrlType.MAIN
}
