package org.mifos.mobilewallet.core.data.fineractcn.api.services

import org.mifos.mobilewallet.core.data.fineractcn.api.ApiEndPoints
import org.mifos.mobilewallet.core.data.fineractcn.entity.LoginResponse
import retrofit2.http.POST
import retrofit2.http.Query
import rx.Observable

/**
 * Created by Devansh 17/06/2020
 */
interface AuthenticationService {

    @POST(ApiEndPoints.IDENTITY + "/token")
    fun authenticate(@Query("grant_type") grantType: String,
                     @Query("username") userName: String,
                     @Query("password") password: String): Observable<LoginResponse>

}