/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifospay.core.data.repositoryImpl

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn
import org.mifospay.core.common.ScreenStateStream
import org.mifospay.core.common.asScreenStateFlow
import org.mifospay.core.data.repository.TwoFactorAuthRepository
import org.mifospay.core.network.FineractApiManager
import org.mifospay.core.network.model.twofactor.AccessToken
import org.mifospay.core.network.model.twofactor.DeliveryMethod

class TwoFactorAuthRepositoryImpl(
    private val apiManager: FineractApiManager,
    private val ioDispatcher: CoroutineDispatcher,
) : TwoFactorAuthRepository {
    override suspend fun deliveryMethods(): ScreenStateStream<List<DeliveryMethod>> {
        return apiManager.twoFactorAuthApi.deliveryMethods()
            .asScreenStateFlow(isEmpty = { it.isEmpty() })
            .flowOn(ioDispatcher)
    }

    override suspend fun requestOTP(deliveryMethod: String): ScreenStateStream<String> {
        return apiManager.twoFactorAuthApi
            .requestOTP(deliveryMethod)
            .asScreenStateFlow()
            .flowOn(ioDispatcher)
    }

    override suspend fun validateToken(token: String): ScreenStateStream<AccessToken> {
        return apiManager.twoFactorAuthApi.validateToken(token)
            .asScreenStateFlow()
            .flowOn(ioDispatcher)
    }
}
