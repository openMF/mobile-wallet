package org.mifos.mobilewallet.core.data.fineract.api.services;

import org.mifos.mobilewallet.core.data.fineract.api.ApiEndPoints;
import org.mifos.mobilewallet.core.data.fineract.entity.UserEntity;
import org.mifos.mobilewallet.core.data.fineract.entity.authentication.AuthenticationPayload;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by naman on 17/6/17.
 */

public interface AuthenticationService {

    @POST(ApiEndPoints.AUTHENTICATION)
    Observable<UserEntity> authenticate(@Body AuthenticationPayload authPayload);
}
