/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.repository

import org.mifospay.core.common.DataState
import org.mifospay.core.network.model.entity.register.RegisterPayload
import org.mifospay.core.network.model.entity.register.UserVerify

interface RegistrationRepository {
    suspend fun registerUser(registerPayload: RegisterPayload): DataState<Unit>

    suspend fun verifyUser(userVerify: UserVerify): DataState<Unit>
}
