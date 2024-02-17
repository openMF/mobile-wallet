package org.mifos.mobilewallet.mifospay.network.services

import okhttp3.ResponseBody
import com.mifos.mobilewallet.model.entity.register.RegisterPayload
import com.mifos.mobilewallet.model.entity.register.UserVerify
import org.mifos.mobilewallet.mifospay.network.ApiEndPoints
import retrofit2.http.Body
import retrofit2.http.POST
import rx.Observable

interface RegistrationService {
    @POST(ApiEndPoints.REGISTRATION)
    fun registerUser(@Body registerPayload: RegisterPayload): Observable<ResponseBody>

    @POST(ApiEndPoints.REGISTRATION + "/user")
    fun verifyUser(@Body userVerify: UserVerify): Observable<ResponseBody>
}