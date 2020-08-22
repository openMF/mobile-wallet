package org.mifos.mobilewallet.core.data.fineract.api.services

import org.mifos.mobilewallet.core.data.fineract.api.ApiEndPoints
import org.mifos.mobilewallet.core.data.fineract.entity.UserEntity
import retrofit2.http.POST
import retrofit2.http.Query
import rx.Observable

/**
 * Created by naman on 17/6/17.
 */
interface AuthenticationService {

    @POST(ApiEndPoints.AUTHENTICATION)
    fun authenticate(@Query("username") username: String,
                     @Query("password") password: String): Observable<UserEntity>
}