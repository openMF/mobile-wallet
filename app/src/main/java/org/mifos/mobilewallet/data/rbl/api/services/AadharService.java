package org.mifos.mobilewallet.data.rbl.api.services;

import org.mifos.mobilewallet.data.rbl.api.ApiEndPoints;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by naman on 16/7/17.
 */

public interface AadharService {

    @POST(ApiEndPoints.AADHAR_VERIFY)
    Observable<Response<ResponseBody>> verifyAadhar(@Query("client_id") String clientId,
            @Query("client_secret") String clientSecret,
            @Body RequestBody body);

    @POST(ApiEndPoints.AADHAR_GENERATE_OTP)
    Observable<Response<ResponseBody>> generateAadharOtp(@Query("client_id") String clientId,
            @Query("client_secret") String clientSecret,
            @Body RequestBody body);

    @POST(ApiEndPoints.AADHAR_VERIFY_OTP)
    Observable<Response<ResponseBody>> verifyAadharOtp(@Query("client_id") String clientId,
            @Query("client_secret") String clientSecret,
            @Body RequestBody body);


}

