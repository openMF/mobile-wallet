package org.mifos.mobilewallet.core.data.fineract.api.services;

import org.mifos.mobilewallet.core.data.fineract.api.ApiEndPoints;
import org.mifos.mobilewallet.core.data.fineract.entity.register.RegisterPayload;
import org.mifos.mobilewallet.core.data.fineract.entity.register.UserVerify;

import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface RegistrationService {

    @POST(ApiEndPoints.REGISTRATION)
    Observable<ResponseBody> registerUser(@Body RegisterPayload registerPayload);

    @POST(ApiEndPoints.REGISTRATION + "/user")
    Observable<ResponseBody> verifyUser(@Body UserVerify userVerify);
}