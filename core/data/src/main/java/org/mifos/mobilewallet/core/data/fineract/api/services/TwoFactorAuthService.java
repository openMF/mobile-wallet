package org.mifos.mobilewallet.core.data.fineract.api.services;

import org.mifos.mobilewallet.core.data.fineract.api.ApiEndPoints;
import org.mifos.mobilewallet.core.domain.model.twofactor.AccessToken;
import org.mifos.mobilewallet.core.domain.model.twofactor.DeliveryMethod;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by ankur on 01/June/2018
 */

public interface TwoFactorAuthService {

    @GET(ApiEndPoints.TWOFACTOR)
    Observable<List<DeliveryMethod>> getDeliveryMethods();

    @POST(ApiEndPoints.TWOFACTOR)
    Observable<String> requestOTP(@Query("deliveryMethod") String deliveryMethod);

    @POST(ApiEndPoints.TWOFACTOR + "/validate")
    Observable<AccessToken> validateToken(@Query("token") String token);

}