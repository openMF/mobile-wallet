package org.mifos.mobilewallet.core.data.fineract.api.services

import org.mifos.mobilewallet.core.data.fineract.api.ApiEndPoints
import org.mifos.mobilewallet.core.domain.model.twofactor.AccessToken
import org.mifos.mobilewallet.core.domain.model.twofactor.DeliveryMethod
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import rx.Observable

/**
 * Created by ankur on 01/June/2018
 */
interface TwoFactorAuthService {

    @GET(ApiEndPoints.TWOFACTOR)
    fun getDeliveryMethods(): Observable<List<DeliveryMethod>>

    @POST(ApiEndPoints.TWOFACTOR)
    fun requestOTP(@Query("deliveryMethod") deliveryMethod: String): Observable<String>

    @POST("${ApiEndPoints.TWOFACTOR}/validate")
    fun validateToken(@Query("token") token: String): Observable<AccessToken>
}