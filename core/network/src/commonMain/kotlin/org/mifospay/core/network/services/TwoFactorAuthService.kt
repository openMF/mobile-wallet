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

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query
import kotlinx.coroutines.flow.Flow
import org.mifospay.core.network.model.twofactor.AccessToken
import org.mifospay.core.network.model.twofactor.DeliveryMethod
import org.mifospay.core.network.utils.ApiEndPoints

interface TwoFactorAuthService {
    @GET(ApiEndPoints.TWOFACTOR)
    suspend fun deliveryMethods(): Flow<List<DeliveryMethod>>

    @POST(ApiEndPoints.TWOFACTOR)
    suspend fun requestOTP(@Query("deliveryMethod") deliveryMethod: String): Flow<String>

    @POST(ApiEndPoints.TWOFACTOR + "/validate")
    suspend fun validateToken(@Query("token") token: String): Flow<AccessToken>
}
