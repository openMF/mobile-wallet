package org.mifos.mobilewallet.core.data.fineractcn.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.mifos.mobilewallet.core.data.fineractcn.api.BaseURL.baseURL
import org.mifos.mobilewallet.core.data.fineractcn.api.services.AccountingService
import org.mifos.mobilewallet.core.data.fineractcn.api.services.AuthenticationService
import org.mifos.mobilewallet.core.data.fineractcn.api.services.CustomerService
import org.mifos.mobilewallet.core.data.fineractcn.api.services.DepositService
import org.mifos.mobilewallet.core.utils.Constants
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by Devansh on 17/06/2020
 */
class FineractCNApiManager {

    init {
        val accessToken = ""
        createService(accessToken)
    }

    companion object {

        private lateinit var retrofit: Retrofit
        private lateinit var customerApi: CustomerService
        private lateinit var authenticationApi: AuthenticationService
        private lateinit var depositApi: DepositService
        private lateinit var accountingApi: AccountingService

        fun createAuthenticatedService(accessToken: String) {
            createService(accessToken)
        }

        private fun createService(accessToken: String) {

            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            val okHttpClient = OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .addInterceptor(interceptor)
                    /**
                     * Using hardcoded values for now
                     */
                    .addInterceptor(ApiInterceptor(accessToken, Constants.TENANT, Constants.USER))
                    .build()

            retrofit = Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(okHttpClient)
                    .build()
            initialise()
        }

        private fun initialise() {
            authenticationApi = createApi(AuthenticationService::class.java)
            customerApi = createApi(CustomerService::class.java)
            depositApi = createApi(DepositService::class.java)
            accountingApi = createApi(AccountingService::class.java)
        }


        private fun <T> createApi(clazz: Class<T>): T {
            return retrofit.create(clazz)
        }
    }

    fun getAuthenticationAPI(): AuthenticationService {
        return authenticationApi
    }

    fun getCustomerApi(): CustomerService {
        return customerApi
    }

    fun getDepositApi(): DepositService {
        return depositApi
    }

    fun getAccountingApi(): AccountingService {
        return accountingApi
    }
}