package org.mifos.mobilewallet.core.data.fineract.api;

import android.util.Base64;

import org.mifos.mobilewallet.core.data.fineract.api.services.AuthenticationService;
import org.mifos.mobilewallet.core.data.fineract.api.services.ClientService;
import org.mifos.mobilewallet.core.data.fineract.api.services.DataTablesService;
import org.mifos.mobilewallet.core.data.fineract.api.services.DocumentService;
import org.mifos.mobilewallet.core.data.fineract.api.services.RegistrationService;
import org.mifos.mobilewallet.core.data.fineract.api.services.SavingAccountsListService;
import org.mifos.mobilewallet.core.data.fineract.api.services.SearchService;

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
    private static DataTablesService dataTablesService;
    private static DocumentService documentApi;

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

        dataTablesService = createApi(DataTablesService.class);
        documentApi = createApi(DocumentService.class);
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

    public DataTablesService getDatatablesApi() {
        return dataTablesService;
    }
}
