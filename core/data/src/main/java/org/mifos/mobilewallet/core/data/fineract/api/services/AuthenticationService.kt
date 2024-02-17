package org.mifos.mobilewallet.core.data.fineract.api.services

import org.mifos.mobilewallet.core.data.fineract.api.ApiEndPoints
import com.mifos.mobilewallet.model.entity.UserEntity
import com.mifos.mobilewallet.model.entity.authentication.AuthenticationPayload
import retrofit2.http.Body
import retrofit2.http.POST
import rx.Observable

/**
 * Created by naman on 17/6/17.
 */
interface AuthenticationService {
    @POST(ApiEndPoints.AUTHENTICATION)
    fun authenticate(@Body authPayload: AuthenticationPayload): Observable<UserEntity>
}
