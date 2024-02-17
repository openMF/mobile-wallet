package org.mifos.mobilewallet.mifospay.network

import okhttp3.Interceptor
import okhttp3.Response
import org.mifos.mobilewallet.datastore.PreferencesHelper
import java.io.IOException

class ApiInterceptor(private val preferencesHelper: PreferencesHelper) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val chainRequest = chain.request()
        val builder = chainRequest.newBuilder().header(HEADER_TENANT, DEFAULT)
        val authToken = preferencesHelper.token
        if (!authToken.isNullOrEmpty()) {
            builder.header(HEADER_AUTH, authToken)
        }
        val request = builder.build()
        return chain.proceed(request)
    }

    companion object {
        const val HEADER_TENANT = "Fineract-Platform-TenantId"
        const val HEADER_AUTH = "Authorization"
        const val DEFAULT = "default"
    }
}
