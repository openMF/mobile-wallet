package org.mifos.mobilewallet.core.data.fineract.api.services;

import org.mifos.mobilewallet.core.data.fineract.api.ApiEndPoints;
import org.mifos.mobilewallet.core.data.fineract.api.GenericResponse;
import org.mifos.mobilewallet.core.data.fineract.entity.UserWithRole;
import org.mifos.mobilewallet.core.domain.model.user.NewUser;
import org.mifos.mobilewallet.core.domain.usecase.user.CreateUser;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by ankur on 11/June/2018
 */

public interface UserService {

    @GET(ApiEndPoints.USER)
    Observable<List<UserWithRole>> getUsers();

    @POST(ApiEndPoints.USER)
    Observable<CreateUser.ResponseValue> createUser(@Body NewUser user);

    @PUT(ApiEndPoints.USER + "/{userId}")
    Observable<GenericResponse> updateUser(
            @Path("userId") int userId,
            @Body Object updateUserEntity);

    @DELETE(ApiEndPoints.USER + "/{userId}")
    Observable<GenericResponse> deleteUser(
            @Path("userId") int userId);

    @GET(ApiEndPoints.USER + "/{userId}")
    Observable<UserWithRole> getUser(
            @Path("userId") long userId);
}

