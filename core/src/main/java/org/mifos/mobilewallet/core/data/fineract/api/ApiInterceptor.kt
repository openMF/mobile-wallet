package org.mifos.mobilewallet.core.data.fineract.api

import android.text.TextUtils
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Created by naman on 17/6/17.
 */
class ApiInterceptor(private val authToken: String, private val headerTenant: String) :
    Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val chainRequest = chain.request()
        val builder = chainRequest.newBuilder()
            .header(HEADER_TENANT, headerTenant)
        if (!TextUtils.isEmpty(authToken)) {
            builder.header(HEADER_AUTH, authToken)
        }
        val request = builder.build()
        return chain.proceed(request)
    }

    companion object {
        const val HEADER_TENANT = "Fineract-Platform-TenantId"
        const val HEADER_AUTH = "Authorization"
    }
}