package org.mifospay.core.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.header
import io.ktor.util.AttributeKey
import org.mifospay.core.datastore.PreferencesHelper

class KtorInterceptor(
    private val preferencesHelper: PreferencesHelper
) {
    class Config {
        lateinit var preferencesHelper: PreferencesHelper
    }

    companion object Plugin : HttpClientPlugin<Config, KtorInterceptor> {
        const val HEADER_TENANT = "Fineract-Platform-TenantId"
        const val HEADER_AUTH = "Authorization"
        const val DEFAULT = "venus"

        override val key: AttributeKey<KtorInterceptor> = AttributeKey("KtorInterceptor")

        override fun install(plugin: KtorInterceptor, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.State) {
                val authToken = plugin.preferencesHelper.token
                context.header(HEADER_TENANT, DEFAULT)
                if (!authToken.isNullOrEmpty()) {
                    context.header(HEADER_AUTH, authToken)
                }
            }
        }

        override fun prepare(block: Config.() -> Unit): KtorInterceptor {
            val config = Config().apply(block)
            return KtorInterceptor(config.preferencesHelper)
        }
    }
}
