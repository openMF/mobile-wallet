package org.mifos.mobilewallet.data.rbl.api;

import org.mifos.mobilewallet.BuildConfig;
import org.mifos.mobilewallet.data.rbl.api.services.AadharService;
import org.mifos.mobilewallet.data.rbl.api.services.PanService;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by naman on 22/6/17.
 */

public class RblApiManager {

    private static final String BASE_URL = "https://api.us.apiconnect.ibmcloud.com/rbl/rblhackathon/";
    public static final String CLIENT_ID = BuildConfig.RBL_CLIENT_ID;
    public static final String CLIENT_SECRET = BuildConfig.RBL_CLIENT_SECRET;

    private static Retrofit retrofit;
    private static PanService panApi;
    private static AadharService aadharApi;

    public RblApiManager() {
        createService();
    }

    private static void init() {
        panApi = createApi(PanService.class);
        aadharApi = createApi(AadharService.class);
    }

    private static <T> T createApi(Class<T> clazz) {
        return retrofit.create(clazz);
    }

    public static void createService() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(new ApiInterceptor())
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();
        init();
    }

    public PanService getPanApi() {
        return panApi;
    }

    public AadharService getAadharApi() {
        return aadharApi;
    }

}
