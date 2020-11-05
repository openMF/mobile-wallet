package org.mifos.mobilewallet.core.data.paymenthub.api;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;

/**
 * Created by naman on 6/4/19.
 */

public class ApiInterceptor implements Interceptor {

    public static final String HEADER_TENANT = "Platform-TenantId";
    private String headerTenant;

    public ApiInterceptor(String headerTenant) {
        this.headerTenant = headerTenant;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request chainRequest = chain.request();
        Builder builder = chainRequest.newBuilder()
                .header(HEADER_TENANT, headerTenant);

        Request request = builder.build();
        return chain.proceed(request);
    }
}
