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
