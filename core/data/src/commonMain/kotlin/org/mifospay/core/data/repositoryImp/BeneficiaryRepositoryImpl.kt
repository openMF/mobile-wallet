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
import org.mifospay.core.data.repository.BeneficiaryRepository
import org.mifospay.core.model.entity.beneficary.Beneficiary
import org.mifospay.core.model.entity.beneficary.BeneficiaryPayload
import org.mifospay.core.model.entity.beneficary.BeneficiaryUpdatePayload
import org.mifospay.core.model.entity.templates.beneficiary.BeneficiaryTemplate
import org.mifospay.core.network.SelfServiceApiManager
import org.mifospay.core.network.model.CommonResponse

class BeneficiaryRepositoryImpl(
    private val apiManager: SelfServiceApiManager,
    private val ioDispatcher: CoroutineDispatcher,
) : BeneficiaryRepository {
    override suspend fun getBeneficiaryList(): Flow<Result<List<Beneficiary>>> {
        return apiManager.beneficiaryApi.beneficiaryList().asResult().flowOn(ioDispatcher)
    }

    override suspend fun getBeneficiaryTemplate(): Flow<Result<BeneficiaryTemplate>> {
        return apiManager.beneficiaryApi.beneficiaryTemplate().asResult().flowOn(ioDispatcher)
    }

    override suspend fun createBeneficiary(
        beneficiaryPayload: BeneficiaryPayload,
    ): Flow<Result<CommonResponse>> {
        return apiManager
            .beneficiaryApi
            .createBeneficiary(beneficiaryPayload)
            .asResult().flowOn(ioDispatcher)
    }

    override suspend fun updateBeneficiary(
        beneficiaryId: Long,
        payload: BeneficiaryUpdatePayload,
    ): Flow<Result<CommonResponse>> {
        return apiManager.beneficiaryApi
            .updateBeneficiary(beneficiaryId, payload)
            .asResult().flowOn(ioDispatcher)
    }

    override suspend fun deleteBeneficiary(beneficiaryId: Long): Flow<Result<CommonResponse>> {
        return apiManager.beneficiaryApi
            .deleteBeneficiary(beneficiaryId)
            .asResult().flowOn(ioDispatcher)
    }
}
