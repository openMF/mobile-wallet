package org.mifos.mobilewallet.core.data.fineract.api;

import android.util.Base64;

import org.mifos.mobilewallet.core.data.fineract.api.services.AccountTransfersService;
import org.mifos.mobilewallet.core.data.fineract.api.services.AuthenticationService;
import org.mifos.mobilewallet.core.data.fineract.api.services.ClientService;
import org.mifos.mobilewallet.core.data.fineract.api.services.DocumentService;
import org.mifos.mobilewallet.core.data.fineract.api.services.InvoiceService;
import org.mifos.mobilewallet.core.data.fineract.api.services.KYCLevel1Service;
import org.mifos.mobilewallet.core.data.fineract.api.services.RegistrationService;
import org.mifos.mobilewallet.core.data.fineract.api.services.RunReportService;
import org.mifos.mobilewallet.core.data.fineract.api.services.SavedCardService;
import org.mifos.mobilewallet.core.data.fineract.api.services.SavingAccountsListService;
import org.mifos.mobilewallet.core.data.fineract.api.services.SearchService;
import org.mifos.mobilewallet.core.data.fineract.api.services.TwoFactorAuthService;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by naman on 17/6/17.
 */

public class FineractApiManager {

    private static BaseURL baseUrl = new BaseURL();
    private static final String BASE_URL = baseUrl.getUrl();

    private static Retrofit retrofit;
    private static AuthenticationService authenticationApi;
    private static ClientService clientsApi;
    private static SavingAccountsListService savingAccountsListApi;
    private static RegistrationService registrationAPi;
    private static SearchService searchApi;
    private static SavedCardService savedCardApi;
    private static DocumentService documentApi;
    private static TwoFactorAuthService twoFactorAuthApi;
    private static AccountTransfersService accountTransfersApi;
    private static RunReportService runReportApi;
    private static KYCLevel1Service kycLevel1Api;
    private static InvoiceService invoiceApi;

    private static SelfServiceApiManager sSelfInstance;

    public FineractApiManager() {
        String authToken = "Basic " + Base64.encodeToString("mifos:password".getBytes(),
                Base64.NO_WRAP);
        createService(authToken);

        if (sSelfInstance == null) {
            sSelfInstance = new SelfServiceApiManager();
        }
    }

    private static void init() {
        authenticationApi = createApi(AuthenticationService.class);
        clientsApi = createApi(ClientService.class);
        savingAccountsListApi = createApi(SavingAccountsListService.class);
        registrationAPi = createApi(RegistrationService.class);
        searchApi = createApi(SearchService.class);

        savedCardApi = createApi(SavedCardService.class);
        documentApi = createApi(DocumentService.class);
        twoFactorAuthApi = createApi(TwoFactorAuthService.class);
        accountTransfersApi = createApi(AccountTransfersService.class);
        runReportApi = createApi(RunReportService.class);
        kycLevel1Api = createApi(KYCLevel1Service.class);
        invoiceApi = createApi(InvoiceService.class);
    }

    private static <T> T createApi(Class<T> clazz) {
        return retrofit.create(clazz);
    }

    public static void createService(String authToken) {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(new ApiInterceptor(authToken, "default"))
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();
        init();

    }

    public static void createSelfService(String authToken) {
        SelfServiceApiManager.createService(authToken);
    }

    public AuthenticationService getAuthenticationApi() {
        return authenticationApi;
    }

    public ClientService getClientsApi() {
        return clientsApi;
    }

    public SavingAccountsListService getSavingAccountsListApi() {
        return savingAccountsListApi;
    }

    public RegistrationService getRegistrationAPi() {
        return registrationAPi;
    }

    public SearchService getSearchApi() {
        return searchApi;
    }

    public static SelfServiceApiManager getSelfApiManager() {
        return sSelfInstance;
    }

    public DocumentService getDocumentApi() {
        return documentApi;
    }

    public RunReportService getRunReportApi() {
        return runReportApi;
    }

    public TwoFactorAuthService getTwoFactorAuthApi() {
        return twoFactorAuthApi;
    }

    public AccountTransfersService getAccountTransfersApi() {
        return accountTransfersApi;
    }

    public SavedCardService getSavedCardApi() {
        return savedCardApi;
    }

    public KYCLevel1Service getKycLevel1Api() {
        return kycLevel1Api;
    }

    public InvoiceService getInvoiceApi() {
        return invoiceApi;
    }
}
