package org.mifos.mobilewallet.data.fineract.api.services;

import org.mifos.mobilewallet.data.fineract.api.ApiEndPoints;
import org.mifos.mobilewallet.data.fineract.entity.UserDetailsEntity;
import org.mifos.mobilewallet.data.fineract.entity.UserEntity;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by naman on 17/6/17.
 */

public interface AuthenticationService {

    @POST(ApiEndPoints.AUTHENTICATION)
    Observable<UserEntity> authenticate(@Query("username") String username,
                                        @Query("password") String password);

    @GET(ApiEndPoints.USERS + "/{userid}")
    Observable<UserDetailsEntity> getUserDetails(@Path("userid") long userid);
}
