/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.repositoryImp

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import org.mifospay.core.common.Result
import org.mifospay.core.common.asResult
import org.mifospay.core.data.repository.TwoFactorAuthRepository
import org.mifospay.core.network.FineractApiManager
import org.mifospay.core.network.model.twofactor.AccessToken
import org.mifospay.core.network.model.twofactor.DeliveryMethod

class TwoFactorAuthRepositoryImpl(
    private val apiManager: FineractApiManager,
    private val ioDispatcher: CoroutineDispatcher,
) : TwoFactorAuthRepository {
    override suspend fun deliveryMethods(): Flow<Result<List<DeliveryMethod>>> {
        return apiManager.twoFactorAuthApi.deliveryMethods().asResult().flowOn(ioDispatcher)
    }

    override suspend fun requestOTP(deliveryMethod: String): Flow<Result<String>> {
        return apiManager.twoFactorAuthApi
            .requestOTP(deliveryMethod)
            .asResult().flowOn(ioDispatcher)
    }

    override suspend fun validateToken(token: String): Flow<Result<AccessToken>> {
        return apiManager.twoFactorAuthApi.validateToken(token).asResult().flowOn(ioDispatcher)
    }
}
