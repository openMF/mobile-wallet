package org.mifospay.core.network

import okhttp3.Interceptor
import okhttp3.Response
import org.mifospay.core.datastore.PreferencesHelper
import java.io.IOException

class ApiInterceptor(private val preferencesHelper: PreferencesHelper) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val chainRequest = chain.request()
        val builder = chainRequest.newBuilder().header(
            org.mifospay.core.network.ApiInterceptor.Companion.HEADER_TENANT,
            org.mifospay.core.network.ApiInterceptor.Companion.DEFAULT
        )
        val authToken = preferencesHelper.token
        if (!authToken.isNullOrEmpty()) {
            builder.header(org.mifospay.core.network.ApiInterceptor.Companion.HEADER_AUTH, authToken)
        }
        val request = builder.build()
        return chain.proceed(request)
    }

    companion object {
        const val HEADER_TENANT = "Fineract-Platform-TenantId"
        const val HEADER_AUTH = "Authorization"
        const val DEFAULT = "venus"
    }
}
