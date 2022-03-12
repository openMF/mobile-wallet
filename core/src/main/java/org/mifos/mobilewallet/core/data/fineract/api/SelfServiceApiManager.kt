package org.mifos.mobilewallet.core.data.fineract.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.mifos.mobilewallet.core.data.fineract.api.services.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by naman on 20/8/17.
 */
class SelfServiceApiManager {
    val authenticationApi: AuthenticationService?
        get() = Companion.authenticationApi
    val clientsApi: ClientService?
        get() = Companion.clientsApi
    val savingAccountsListApi: SavingsAccountsService?
        get() = Companion.savingAccountsListApi
    val registrationAPi: RegistrationService?
        get() = Companion.registrationAPi
    val beneficiaryApi: BeneficiaryService?
        get() = Companion.beneficiaryApi
    val thirdPartyTransferApi: ThirdPartyTransferService?
        get() = Companion.thirdPartyTransferApi

    companion object {
        const val DEFAULT = "default"
        private val baseUrl = BaseURL()
        private val BASE_URL = baseUrl.selfServiceUrl
        private var retrofit: Retrofit? = null
        private var authenticationApi: AuthenticationService? = null
        private var clientsApi: ClientService? = null
        private var savingAccountsListApi: SavingsAccountsService? = null
        private var registrationAPi: RegistrationService? = null
        private var beneficiaryApi: BeneficiaryService? = null
        private var thirdPartyTransferApi: ThirdPartyTransferService? = null
        private fun init() {
            authenticationApi = createApi(
                AuthenticationService::class.java
            )
            clientsApi = createApi(
                ClientService::class.java
            )
            savingAccountsListApi = createApi(
                SavingsAccountsService::class.java
            )
            registrationAPi = createApi(
                RegistrationService::class.java
            )
            beneficiaryApi = createApi(
                BeneficiaryService::class.java
            )
            thirdPartyTransferApi = createApi(
                ThirdPartyTransferService::class.java
            )
        }

        private fun <T> createApi(clazz: Class<T>): T {
            return retrofit!!.create(clazz)
        }

        fun createService(authToken: String?) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val okHttpClient = OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .addInterceptor(ApiInterceptor(authToken!!, DEFAULT))
                .build()
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build()
            init()
        }
    }

    init {
        val authToken = ""
        createService(authToken)
    }
}