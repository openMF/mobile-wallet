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

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import org.mifospay.core.network.model.entity.register.RegisterPayload
import org.mifospay.core.network.model.entity.register.UserVerify
import org.mifospay.core.network.utils.ApiEndPoints

interface RegistrationService {
    @POST(ApiEndPoints.REGISTRATION)
    suspend fun registerUser(@Body registerPayload: RegisterPayload)

    @POST(ApiEndPoints.REGISTRATION + "/user")
    suspend fun verifyUser(@Body userVerify: UserVerify)
}
