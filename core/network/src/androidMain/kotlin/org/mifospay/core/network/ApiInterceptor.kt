/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
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
            HEADER_TENANT,
            DEFAULT,
        )
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
        const val DEFAULT = "venus"
    }
}
