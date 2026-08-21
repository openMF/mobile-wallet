/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.DigestAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.auth.providers.digest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import kpt.core.base.security.CertificatePinConfig
import co.touchlab.kermit.Logger.Companion as KermitLogger

expect fun httpClient(config: HttpClientConfig<*>.() -> Unit): HttpClient

/**
 * Platform hook: installs a CORS proxy plugin on web targets; no-op on native/Android/Desktop.
 *
 * Called automatically by [setupDefaultHttpClient] when [proxiedHosts] is non-empty.
 * On web, requests to any host in [proxiedHosts] are transparently rewritten through
 * [corsProxyBaseUrl] (format: "https://proxy-host/path?" — original URL is appended as-is).
 */
internal expect fun HttpClientConfig<*>.installProxyPlugin(
    proxiedHosts: List<String>,
    corsProxyBaseUrl: String,
)

/**
 * The auth-credential providers for [setupDefaultHttpClient], grouped so the client builder stays under
 * the parameter-count limit. All null (the default) installs no `Auth` plugin. When more than one is
 * supplied, Bearer takes precedence, then Basic, then Digest (mirrors the builder's `when` order).
 *
 * @param basic Provider for Basic authentication credentials.
 * @param digest Provider for Digest authentication credentials.
 * @param bearer Provider for Bearer token authentication.
 * @param bearerRefresh Optional refresh logic for Bearer tokens (only used if [bearer] is configured).
 */
data class AuthProviders(
    val basic: (() -> BasicAuthCredentials)? = null,
    val digest: (() -> DigestAuthCredentials)? = null,
    val bearer: (() -> BearerTokens)? = null,
    val bearerRefresh: (() -> BearerTokens)? = null,
)

/**
 * Provides a default [HttpClientConfig] setup for use with a Ktor-based HTTP client.
 *
 * This function simplifies client configuration by handling common concerns such as:
 * - Authentication (Bearer, Basic, Digest)
 * - Default headers
 * - Timeouts
 * - Logging
 * - JSON serialization
 *
 * It can be passed directly into a Ktor client builder via the `config` lambda:
 * ```kotlin
 * val client = httpClient(setupDefaultHttpClient(baseUrl = "https://api.example.com"))
 * ```
 *
 * @param baseUrl The base URL to be applied to all requests unless explicitly overridden.
 * @param isReleaseBuild When true, restricts logging to headers-only and disables pretty-printed JSON.
 * @param authRequiredUrl A list of hostnames that require authentication.
 * @param defaultHeaders Headers that are applied to every request.
 * @param requestTimeout Timeout in milliseconds for entire request lifecycle.
 * @param socketTimeout Timeout in milliseconds for socket-level communication.
 * @param httpLogger A logger used for HTTP logging (defaults to `Logger.DEFAULT`).
 * @param httpLogLevel Level of HTTP logging (e.g. `LogLevel.ALL`).
 * @param loggableHosts A list of hostnames for which HTTP logging is enabled.
 * @param sensitiveHeaders List of headers to be hidden in logs (defaults to Authorization).
 * @param jsonConfig Custom [Json] configuration used by `ContentNegotiation`.
 * @param authProviders The Basic/Digest/Bearer credential providers (see [AuthProviders]). Default
 *   installs no auth plugin.
 * @param certificatePinConfig TLS certificate pin configuration. Pins are applied by platform
 *   engines that support it (OkHttp on Android/Desktop). Other platforms ignore this parameter.
 * @param proxiedHosts Hostnames whose requests should be transparently routed through
 *   [corsProxyBaseUrl] on browser targets (web only). Ignored on Android/Desktop/iOS.
 *   Example: `listOf("api.stlouisfed.org")` routes FRED API calls via the CORS proxy.
 * @param corsProxyBaseUrl Base URL of the CORS proxy. Must accept the target URL appended raw
 *   after `?` — corsproxy.io format: `https://proxy/?<original-url>`. corsproxy.io is the
 *   default (free, sets CORS headers on all responses). Replace with a self-hosted proxy in
 *   production.
 * @param multiUrlProvider Optional [MultiUrlConfigProvider] for apps that expose more than one
 *   named endpoint. When supplied, [DynamicBaseUrlPlugin] resolves the base URL per [urlType] at
 *   call time. Takes precedence over [dynamicUrlProvider]. Default null preserves the plain
 *   static-URL path (no regression).
 * @param urlType The named endpoint to resolve from [multiUrlProvider]. Ignored unless
 *   [multiUrlProvider] is set. Defaults to [UrlType.MAIN].
 *
 * @return A configuration lambda to be passed into the Ktor [HttpClient].
 */
@Suppress("UnusedParameter")
fun setupDefaultHttpClient(
    baseUrl: String,
    isReleaseBuild: Boolean = false,
    authRequiredUrl: List<String> = emptyList(),
    defaultHeaders: Map<String, String> = emptyMap(),
    requestTimeout: Long = 60_000L,
    socketTimeout: Long = 60_000L,
    httpLogger: Logger = Logger.DEFAULT,
    httpLogLevel: LogLevel = if (isReleaseBuild) LogLevel.HEADERS else LogLevel.ALL,
    loggableHosts: List<String> = emptyList(),
    sensitiveHeaders: List<String> = listOf(HttpHeaders.Authorization),
    jsonConfig: Json = Json {
        prettyPrint = !isReleaseBuild
        isLenient = true
        ignoreUnknownKeys = true
        explicitNulls = false
        // Cross-version tolerance: an unrecognized enum value (e.g. a server enum the client
        // doesn't model yet) OR an explicit JSON null on a non-null field with a default coerces
        // to that default instead of throwing a SerializationException. Many DTOs already document
        // a `*.UNKNOWN`/default fallback that only works with this flag enabled.
        coerceInputValues = true
    },
    authProviders: AuthProviders = AuthProviders(),
    certificatePinConfig: CertificatePinConfig = CertificatePinConfig.default(),
    proxiedHosts: List<String> = emptyList(),
    corsProxyBaseUrl: String = "https://corsproxy.io",
    multiUrlProvider: MultiUrlConfigProvider? = null,
    urlType: UrlType = UrlType.MAIN,
    dynamicUrlProvider: DynamicUrlConfigProvider? = null,
): HttpClientConfig<*>.() -> Unit = {
    val refreshMutex = Mutex()
    val basicCredentialsProvider = authProviders.basic
    val digestCredentialsProvider = authProviders.digest
    val bearerTokensProvider = authProviders.bearer
    val bearerRefreshProvider = authProviders.bearerRefresh

    when {
        bearerTokensProvider != null -> {
            install(Auth) {
                bearer {
                    loadTokens { bearerTokensProvider() }
                    if (bearerRefreshProvider != null) {
                        refreshTokens {
                            refreshMutex.withLock {
                                val currentTokens = bearerTokensProvider()
                                if (currentTokens != oldTokens) {
                                    currentTokens
                                } else {
                                    bearerRefreshProvider()
                                }
                            }
                        }
                    }
                    sendWithoutRequest { request ->
                        request.url.host in authRequiredUrl
                    }
                }
            }
        }

        basicCredentialsProvider != null -> {
            install(Auth) {
                basic {
                    credentials {
                        basicCredentialsProvider()
                    }
                    sendWithoutRequest { request ->
                        request.url.host in authRequiredUrl
                    }
                }
            }
        }

        digestCredentialsProvider != null -> {
            install(Auth) {
                digest {
                    credentials {
                        digestCredentialsProvider()
                    }
                }
            }
        }
    }

    defaultRequest {
        url(baseUrl)
        defaultHeaders.forEach { (key, value) ->
            headers.append(key, value)
        }
    }

    install(HttpTimeout) {
        requestTimeoutMillis = requestTimeout
        socketTimeoutMillis = socketTimeout
    }

    install(Logging) {
        logger = httpLogger
        level = httpLogLevel
        filter { request ->
            loggableHosts.any { host ->
                request.url.host.contains(host)
            }
        }
        sanitizeHeader { header ->
            header in sensitiveHeaders
        }
        logger = object : Logger {
            override fun log(message: String) {
                KermitLogger.d(tag = "KtorClient", messageString = message)
            }
        }
    }

    install(ContentNegotiation) {
        json(jsonConfig)
    }

    // Runtime base-URL switching (opt-in). The [DynamicBaseUrlPlugin] rewrites each request's
    // host/scheme at call time — the [baseUrl] above becomes a static fallback. A
    // [MultiUrlConfigProvider] + [urlType] resolves the base URL per named endpoint (multi-server
    // apps); a plain [DynamicUrlConfigProvider] keeps the single-URL behaviour. Consumers wire a
    // provider in their DI (see core/network NetworkModule); both default null, so the plain
    // static-URL path is unchanged (no regression).
    when {
        multiUrlProvider != null -> install(DynamicBaseUrlPlugin) {
            multiConfigProvider = multiUrlProvider
            this.urlType = urlType
        }

        dynamicUrlProvider != null -> install(DynamicBaseUrlPlugin) {
            configProvider = dynamicUrlProvider
        }
    }

    installProxyPlugin(proxiedHosts, corsProxyBaseUrl)
}
