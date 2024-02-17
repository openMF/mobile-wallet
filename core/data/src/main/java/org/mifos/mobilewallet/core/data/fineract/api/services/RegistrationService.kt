package org.mifos.mobilewallet.core.data.fineract.api.services

import okhttp3.ResponseBody
import org.mifos.mobilewallet.core.data.fineract.api.ApiEndPoints
import com.mifos.mobilewallet.model.entity.register.RegisterPayload
import com.mifos.mobilewallet.model.entity.register.UserVerify
import retrofit2.http.Body
import retrofit2.http.POST
import rx.Observable

interface RegistrationService {
    @POST(ApiEndPoints.REGISTRATION)
    fun registerUser(@Body registerPayload: RegisterPayload): Observable<ResponseBody>

    @POST(ApiEndPoints.REGISTRATION + "/user")
    fun verifyUser(@Body userVerify: UserVerify): Observable<ResponseBody>
}