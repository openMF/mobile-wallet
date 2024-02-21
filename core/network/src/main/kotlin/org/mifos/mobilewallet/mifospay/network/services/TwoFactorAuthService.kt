package org.mifos.mobilewallet.mifospay.network.services

import com.mifos.mobilewallet.model.domain.twofactor.AccessToken
import com.mifos.mobilewallet.model.domain.twofactor.DeliveryMethod
import org.mifos.mobilewallet.mifospay.network.ApiEndPoints
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import rx.Observable

/**
 * Created by ankur on 01/June/2018
 */
interface TwoFactorAuthService {
    @get:GET(ApiEndPoints.TWOFACTOR)
    val deliveryMethods: Observable<List<DeliveryMethod>>

    @POST(ApiEndPoints.TWOFACTOR)
    fun requestOTP(@Query("deliveryMethod") deliveryMethod: String): Observable<String>

    @POST(ApiEndPoints.TWOFACTOR + "/validate")
    fun validateToken(@Query("token") token: String): Observable<AccessToken>
}