package org.mifos.mobilewallet.core.data.fineract.api

import android.util.Base64
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.mifos.mobilewallet.core.data.fineract.api.services.*
import org.mifos.mobilewallet.core.utils.Constants
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

/**
 * Created by naman on 17/6/17.
 */
class FineractApiManager {
    val authenticationApi: AuthenticationService?
        get() = Companion.authenticationApi
    val clientsApi: ClientService?
        get() = Companion.clientsApi
    val registrationAPi: RegistrationService?
        get() = Companion.registrationAPi
    val searchApi: SearchService?
        get() = Companion.searchApi
    val documentApi: DocumentService?
        get() = Companion.documentApi
    val runReportApi: RunReportService?
        get() = Companion.runReportApi
    val twoFactorAuthApi: TwoFactorAuthService?
        get() = Companion.twoFactorAuthApi
    val accountTransfersApi: AccountTransfersService?
        get() = Companion.accountTransfersApi
    val savedCardApi: SavedCardService?
        get() = Companion.savedCardApi
    val kycLevel1Api: KYCLevel1Service?
        get() = Companion.kycLevel1Api
    val invoiceApi: InvoiceService?
        get() = Companion.invoiceApi
    val userApi: UserService?
        get() = Companion.userApi
    val thirdPartyTransferApi: ThirdPartyTransferService?
        get() = Companion.thirdPartyTransferApi
    val notificationApi: NotificationService?
        get() = Companion.notificationApi

    companion object {
        const val DEFAULT = "default"
        const val BASIC = "Basic "
        private val baseUrl = BaseURL()
        private val BASE_URL = baseUrl.url
        private var retrofit: Retrofit? = null
        private var authenticationApi: AuthenticationService? = null
        private var clientsApi: ClientService? = null
        var savingAccountsListApi: SavingsAccountsService? = null
            get() = field
            private set
        private var registrationAPi: RegistrationService? = null
        private var searchApi: SearchService? = null
        private var savedCardApi: SavedCardService? = null
        private var documentApi: DocumentService? = null
        private var twoFactorAuthApi: TwoFactorAuthService? = null
        private var accountTransfersApi: AccountTransfersService? = null
        private var runReportApi: RunReportService? = null
        private var kycLevel1Api: KYCLevel1Service? = null
        private var invoiceApi: InvoiceService? = null
        private var userApi: UserService? = null
        private var thirdPartyTransferApi: ThirdPartyTransferService? = null
        private var notificationApi: NotificationService? = null
        var standingInstructionApi: StandingInstructionService? = null
            get() = field
            private set
        var selfApiManager: SelfServiceApiManager? = null
            private set

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
            searchApi = createApi(
                SearchService::class.java
            )
            savedCardApi = createApi(
                SavedCardService::class.java
            )
            documentApi = createApi(
                DocumentService::class.java
            )
            twoFactorAuthApi = createApi(
                TwoFactorAuthService::class.java
            )
            accountTransfersApi = createApi(
                AccountTransfersService::class.java
            )
            runReportApi = createApi(
                RunReportService::class.java
            )
            kycLevel1Api = createApi(
                KYCLevel1Service::class.java
            )
            invoiceApi = createApi(
                InvoiceService::class.java
            )
            userApi = createApi(
                UserService::class.java
            )
            thirdPartyTransferApi = createApi(
                ThirdPartyTransferService::class.java
            )
            notificationApi = createApi(
                NotificationService::class.java
            )
            standingInstructionApi = createApi(
                StandingInstructionService::class.java
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

        @JvmStatic
        fun createSelfService(authToken: String?) {
            SelfServiceApiManager.createService(authToken)
        }
    }

    init {
        val authToken = BASIC + Base64.encodeToString(
            Constants.MIFOS_PASSWORD
                .toByteArray(Charset.forName("UTF-8")), Base64.NO_WRAP
        )
        createService(authToken)
        if (selfApiManager == null) {
            selfApiManager = SelfServiceApiManager()
        }
    }
}