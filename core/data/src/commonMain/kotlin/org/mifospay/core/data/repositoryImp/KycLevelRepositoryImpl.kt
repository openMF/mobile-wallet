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
import org.mifospay.core.data.repository.KycLevelRepository
import org.mifospay.core.network.FineractApiManager
import org.mifospay.core.network.model.GenericResponse
import org.mifospay.core.network.model.entity.kyc.KYCLevel1Details

class KycLevelRepositoryImpl(
    private val apiManager: FineractApiManager,
    private val ioDispatcher: CoroutineDispatcher,
) : KycLevelRepository {
    override suspend fun fetchKYCLevel1Details(
        clientId: Int,
    ): Flow<Result<List<KYCLevel1Details>>> {
        return apiManager.kycLevel1Api
            .fetchKYCLevel1Details(clientId)
            .asResult().flowOn(ioDispatcher)
    }

    override suspend fun addKYCLevel1Details(
        clientId: Int,
        kycLevel1Details: KYCLevel1Details,
    ): Flow<Result<GenericResponse>> {
        return apiManager.kycLevel1Api
            .addKYCLevel1Details(clientId, kycLevel1Details)
            .asResult().flowOn(ioDispatcher)
    }

    override suspend fun updateKYCLevel1Details(
        clientId: Int,
        kycLevel1Details: KYCLevel1Details,
    ): Flow<Result<GenericResponse>> {
        return apiManager.kycLevel1Api
            .updateKYCLevel1Details(clientId, kycLevel1Details)
            .asResult().flowOn(ioDispatcher)
    }
}
