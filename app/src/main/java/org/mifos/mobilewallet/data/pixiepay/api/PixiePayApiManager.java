package org.mifos.mobilewallet.data.pixiepay.api;

import org.mifos.mobilewallet.data.pixiepay.api.services.PayInvoiceService;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by naman on 10/7/17.
 */

public class PixiePayApiManager {

    private static final String BASE_URL = "https://api.us.apiconnect.ibmcloud.com/rbl/rblhackathon/";

    private static Retrofit retrofit;
    private static PayInvoiceService payInvoiceApi;

    public PixiePayApiManager() {
        createService();
    }

    private static void init() {
        payInvoiceApi = createApi(PayInvoiceService.class);
    }

    private static <T> T createApi(Class<T> clazz) {
        return retrofit.create(clazz);
    }

    public static void createService() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();
        init();
    }

    public PayInvoiceService getPayInvoiceApi() {
        return payInvoiceApi;
    }
}
