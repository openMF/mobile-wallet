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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.mifospay.core.common.ScreenState
import org.mifospay.core.common.asScreenStateFlow
import org.mifospay.core.data.repository.BeneficiaryRepository
import org.mifospay.core.model.beneficiary.Beneficiary
import org.mifospay.core.model.beneficiary.BeneficiaryPayload
import org.mifospay.core.model.beneficiary.BeneficiaryUpdatePayload
import org.mifospay.core.model.network.entity.templates.beneficiary.BeneficiaryTemplate
import org.mifospay.core.network.SelfServiceApiManager

class BeneficiaryRepositoryImpl(
    private val apiManager: SelfServiceApiManager,
    private val ioDispatcher: CoroutineDispatcher,
) : BeneficiaryRepository {
    override suspend fun getBeneficiaryList(): Flow<ScreenState<List<Beneficiary>>> {
        return apiManager.beneficiaryApi.beneficiaryList()
            .asScreenStateFlow(isEmpty = { it.isEmpty() })
            .flowOn(ioDispatcher)
    }

    override suspend fun getBeneficiaryTemplate(): Flow<ScreenState<BeneficiaryTemplate>> {
        return apiManager.beneficiaryApi.beneficiaryTemplate()
            .asScreenStateFlow()
            .flowOn(ioDispatcher)
    }

    override suspend fun createBeneficiary(
        beneficiaryPayload: BeneficiaryPayload,
    ) {
        withContext(ioDispatcher) {
            apiManager.beneficiaryApi.createBeneficiary(beneficiaryPayload)
        }
    }

    override suspend fun updateBeneficiary(
        beneficiaryId: Long,
        payload: BeneficiaryUpdatePayload,
    ) {
        withContext(ioDispatcher) {
            apiManager.beneficiaryApi.updateBeneficiary(beneficiaryId, payload)
        }
    }

    override suspend fun deleteBeneficiary(beneficiaryId: Long) {
        withContext(ioDispatcher) {
            apiManager.beneficiaryApi.deleteBeneficiary(beneficiaryId)
        }
    }
}
