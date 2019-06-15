package org.mifos.mobilewallet.core.data.paymenthub.api;

import org.mifos.mobilewallet.core.data.paymenthub.api.services.TransactionsService;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by naman on 6/4/19.
 */

public class PaymentHubApiManager {

    private static BaseURL baseUrl = new BaseURL();

    private static Retrofit retrofit;
    private static TransactionsService transactionsApi;

    @Inject
    public PaymentHubApiManager() {
        String baseURLUserType = "";
        String headerTenant = "";
        createService(baseURLUserType,headerTenant);
    }

    public static void createAPI() {
        transactionsApi = createApi(TransactionsService.class);
    }


    private static <T> T createApi(Class<T> clazz) {
        return retrofit.create(clazz);
    }

    public static void createService(String fspName, String headerTenant) {

        if (!fspName.equals("")) {
            fspName = fspName + ".";
        }
        final String BASE_URL = baseUrl.getUrl(fspName);
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .addInterceptor(new ApiInterceptor(headerTenant))
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();
        createAPI();

    }

    public TransactionsService getTransactionsApi() {
        return transactionsApi;
    }
}