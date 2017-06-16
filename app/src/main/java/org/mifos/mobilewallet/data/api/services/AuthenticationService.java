package org.mifos.mobilewallet.data.api.services;

import org.mifos.mobilewallet.data.api.ApiEndPoints;
import org.mifos.mobilewallet.data.entity.UserEntity;

import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by naman on 17/6/17.
 */

public interface AuthenticationService {

    @POST(ApiEndPoints.AUTHENTICATION)
    Observable<UserEntity> authenticate(@Query("username") String username,
                                        @Query("password") String password);
}
