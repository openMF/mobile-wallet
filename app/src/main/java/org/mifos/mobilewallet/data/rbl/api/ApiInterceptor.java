package org.mifos.mobilewallet.data.rbl.api;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by naman on 23/6/17.
 */

public class ApiInterceptor implements Interceptor {

    public static final String HEADER_CONTENT_TYPE = "content-type";
    public static final String HEADER_ACCEPT = "accept";


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request chainRequest = chain.request();
        Request.Builder builder = chainRequest.newBuilder()
                .header(HEADER_CONTENT_TYPE, "application/json");

        builder.addHeader(HEADER_ACCEPT, "application/json");
        Request request = builder.build();
        return chain.proceed(request);
    }
}
