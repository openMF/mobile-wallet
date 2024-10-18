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

import kotlinx.coroutines.flow.Flow
import org.mifospay.core.common.DataState
import org.mifospay.core.network.model.twofactor.AccessToken
import org.mifospay.core.network.model.twofactor.DeliveryMethod

interface TwoFactorAuthRepository {
    suspend fun deliveryMethods(): Flow<DataState<List<DeliveryMethod>>>

    suspend fun requestOTP(deliveryMethod: String): Flow<DataState<String>>

    suspend fun validateToken(token: String): Flow<DataState<AccessToken>>
}
