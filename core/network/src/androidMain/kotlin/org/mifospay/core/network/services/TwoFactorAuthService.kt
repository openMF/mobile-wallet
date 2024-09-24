/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.network.services

import com.mifospay.core.model.domain.twofactor.AccessToken
import com.mifospay.core.model.domain.twofactor.DeliveryMethod
import org.mifospay.core.network.ApiEndPoints
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
