package org.mifos.mobilewallet.mifospay.network.services

import com.mifos.mobilewallet.model.domain.user.User
import com.mifos.mobilewallet.model.entity.authentication.AuthenticationPayload
import org.mifos.mobilewallet.mifospay.network.ApiEndPoints
import retrofit2.http.Body
import retrofit2.http.POST
import rx.Observable

interface AuthenticationService {
    @POST(ApiEndPoints.AUTHENTICATION)
    fun authenticate(@Body authPayload: AuthenticationPayload): Observable<User>
}
