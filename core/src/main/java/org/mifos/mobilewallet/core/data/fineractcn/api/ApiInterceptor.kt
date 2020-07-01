package org.mifos.mobilewallet.core.data.fineractcn.api

import android.support.annotation.NonNull
import android.text.TextUtils
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Created by Devansh on 17/06/2020
 */
class ApiInterceptor(
        private val accessToken: String,
        private val tenant: String,
        private val user: String) : Interceptor {

    companion object {
        const val AUTHORIZATION = "Authorization"
        const val HEADER_X_TENANT_IDENTIFIER = "X-Tenant-Identifier"
        const val USER = "User"
    }

    @Throws(IOException::class)
    override fun intercept(@NonNull chain: Interceptor.Chain): Response {
        val chainRequest = chain.request()
        val builder = chainRequest.newBuilder()

        if (!TextUtils.isEmpty(accessToken)) {
            builder.header(AUTHORIZATION, accessToken)
            builder.header(USER, user)
        }

        builder.header(HEADER_X_TENANT_IDENTIFIER, tenant)

        val request = builder.build()
        return chain.proceed(request)
    }
}