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
import kotlinx.coroutines.withContext
import org.mifospay.core.common.DataState
import org.mifospay.core.common.asDataStateFlow
import org.mifospay.core.data.repository.BeneficiaryRepository
import org.mifospay.core.model.beneficiary.Beneficiary
import org.mifospay.core.model.beneficiary.BeneficiaryPayload
import org.mifospay.core.model.beneficiary.BeneficiaryUpdatePayload
import org.mifospay.core.network.SelfServiceApiManager
import org.mifospay.core.network.model.entity.templates.beneficiary.BeneficiaryTemplate

class BeneficiaryRepositoryImpl(
    private val apiManager: SelfServiceApiManager,
    private val ioDispatcher: CoroutineDispatcher,
) : BeneficiaryRepository {
    override suspend fun getBeneficiaryList(): Flow<DataState<List<Beneficiary>>> {
        return apiManager.beneficiaryApi.beneficiaryList().asDataStateFlow().flowOn(ioDispatcher)
    }

    override suspend fun getBeneficiaryTemplate(): Flow<DataState<BeneficiaryTemplate>> {
        return apiManager.beneficiaryApi.beneficiaryTemplate().asDataStateFlow().flowOn(ioDispatcher)
    }

    override suspend fun createBeneficiary(
        beneficiaryPayload: BeneficiaryPayload,
    ): DataState<String> {
        return try {
            withContext(ioDispatcher) {
                apiManager.beneficiaryApi.createBeneficiary(beneficiaryPayload)
            }

            DataState.Success("Beneficiary created successfully")
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun updateBeneficiary(
        beneficiaryId: Long,
        payload: BeneficiaryUpdatePayload,
    ): DataState<String> {
        return try {
            withContext(ioDispatcher) {
                apiManager.beneficiaryApi.updateBeneficiary(beneficiaryId, payload)
            }

            DataState.Success("Beneficiary updated successfully")
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun deleteBeneficiary(beneficiaryId: Long): DataState<String> {
        return try {
            withContext(ioDispatcher) {
                apiManager.beneficiaryApi.deleteBeneficiary(beneficiaryId)
            }

            DataState.Success("Beneficiary deleted successfully")
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }
}
