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

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.mifospay.core.datastore.PreferencesHelper
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class MifosWalletOkHttpClient(
    private val preferences: PreferencesHelper,
    private val username: String? = null,
    private val password: String? = null,
    private val isTesting: Boolean = false,
) {
    // Create a trust manager that does not validate certificate chains
    val mifosOkHttpClient: OkHttpClient
        // Interceptor :> Full Body Logger and ApiRequest Header
        get() {
            val builder = OkHttpClient.Builder()
            try {
                // Create a trust manager that does not validate certificate chains
                val trustAllCerts = arrayOf<TrustManager>(
                    object : X509TrustManager {
                        @Throws(CertificateException::class)
                        override fun checkClientTrusted(
                            chain: Array<X509Certificate>,
                            authType: String,
                        ) {
                        }

                        @Throws(CertificateException::class)
                        override fun checkServerTrusted(
                            chain: Array<X509Certificate>,
                            authType: String,
                        ) {
                        }

                        override fun getAcceptedIssuers(): Array<X509Certificate> {
                            return emptyArray()
                        }
                    },
                )

                // Install the all-trusting trust manager
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())
                // Create an ssl socket factory with our all-trusting manager
                val sslSocketFactory = sslContext.socketFactory

                // Enable Full Body Logging
                val logger = HttpLoggingInterceptor()
                logger.level = HttpLoggingInterceptor.Level.BODY

                // Set SSL certificate to OkHttpClient Builder
//                builder.sslSocketFactory(sslSocketFactory)
                builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                builder.hostnameVerifier { _, _ -> true }
            } catch (e: Exception) {
                throw RuntimeException(e)
            }

            // Enable Full Body Logging
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BODY

            // Setting Timeout 30 Seconds
            builder.connectTimeout(60, TimeUnit.SECONDS)
            builder.readTimeout(60, TimeUnit.SECONDS)

            // Interceptor :> Full Body Logger and ApiRequest Header
            builder.addInterceptor(logger)
            if (isTesting && username != null && password != null) {
                builder.addInterceptor(TestingApiInterceptor(username, password))
            } else {
                builder.addInterceptor(ApiInterceptor(preferences))
            }
            return builder.build()
        }
}
