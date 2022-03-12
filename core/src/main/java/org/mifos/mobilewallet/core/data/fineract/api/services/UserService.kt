package org.mifos.mobilewallet.core.data.fineract.api.services

import org.mifos.mobilewallet.core.data.fineract.api.ApiEndPoints
import org.mifos.mobilewallet.core.data.fineract.entity.UserWithRole
import org.mifos.mobilewallet.core.domain.model.user.NewUser
import org.mifos.mobilewallet.core.domain.usecase.user.CreateUser
import org.mifos.mobilewallet.core.data.fineract.api.GenericResponse
import retrofit2.http.*
import rx.Observable

/**
 * Created by ankur on 11/June/2018
 */
interface UserService {
    @get:GET(ApiEndPoints.USER)
    val users: Observable<List<UserWithRole?>?>?

    @POST(ApiEndPoints.USER)
    fun createUser(@Body user: NewUser?): Observable<CreateUser.ResponseValue?>?

    @PUT(ApiEndPoints.USER + "/{userId}")
    fun updateUser(
        @Path("userId") userId: Int,
        @Body updateUserEntity: Any?
    ): Observable<GenericResponse?>?

    @DELETE(ApiEndPoints.USER + "/{userId}")
    fun deleteUser(
        @Path("userId") userId: Int
    ): Observable<GenericResponse?>?

    @GET(ApiEndPoints.USER + "/{userId}")
    fun getUser(
        @Path("userId") userId: Long
    ): Observable<UserWithRole?>?
}