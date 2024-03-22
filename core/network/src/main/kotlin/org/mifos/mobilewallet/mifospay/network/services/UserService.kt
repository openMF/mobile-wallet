package org.mifos.mobilewallet.mifospay.network.services

import com.mifos.mobilewallet.model.entity.UserWithRole
import com.mifos.mobilewallet.model.domain.user.NewUser
import org.mifos.mobilewallet.mifospay.network.ApiEndPoints
import org.mifos.mobilewallet.mifospay.network.GenericResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import rx.Observable

/**
 * Created by ankur on 11/June/2018
 */
interface UserService {
    @get:GET(ApiEndPoints.USER)
    val users: Observable<List<UserWithRole>>

    @POST(ApiEndPoints.USER)
    fun <T> createUser(@Body user: NewUser): Observable<T>

    @PUT(ApiEndPoints.USER + "/{userId}")
    fun updateUser(
        @Path("userId") userId: Int,
        @Body updateUserEntity: Any
    ): Observable<GenericResponse>

    @DELETE(ApiEndPoints.USER + "/{userId}")
    fun deleteUser(
        @Path("userId") userId: Int
    ): Observable<GenericResponse>

    @GET("self/userdetails")
    fun getUser(): Observable<UserWithRole>
}
