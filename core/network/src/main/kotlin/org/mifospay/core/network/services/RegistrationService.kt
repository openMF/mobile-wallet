package org.mifospay.core.network.services

import com.mifospay.core.model.entity.register.RegisterPayload
import com.mifospay.core.model.entity.register.UserVerify
import okhttp3.ResponseBody
import org.mifospay.core.network.ApiEndPoints
import retrofit2.http.Body
import retrofit2.http.POST
import rx.Observable

interface RegistrationService {
    @POST(ApiEndPoints.REGISTRATION)
    fun registerUser(@Body registerPayload: RegisterPayload): Observable<ResponseBody>

    @POST(ApiEndPoints.REGISTRATION + "/user")
    fun verifyUser(@Body userVerify: UserVerify): Observable<ResponseBody>
}