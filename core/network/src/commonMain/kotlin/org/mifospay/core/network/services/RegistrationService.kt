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
import org.mifospay.core.model.entity.register.RegisterPayload
import org.mifospay.core.model.entity.register.UserVerify
import org.mifospay.core.network.ApiEndPoints

interface RegistrationService {
    @POST(ApiEndPoints.REGISTRATION)
    fun registerUser(@Body registerPayload: RegisterPayload)

    @POST(ApiEndPoints.REGISTRATION + "/user")
    fun verifyUser(@Body userVerify: UserVerify)
}
