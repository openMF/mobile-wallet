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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.mifospay.core.common.DataState
import org.mifospay.core.common.asDataStateFlow
import org.mifospay.core.data.repository.KycLevelRepository
import org.mifospay.core.model.kyc.KYCLevel1Details
import org.mifospay.core.network.FineractApiManager

class KycLevelRepositoryImpl(
    private val apiManager: FineractApiManager,
    private val ioDispatcher: CoroutineDispatcher,
) : KycLevelRepository {
    override fun fetchKYCLevel1Details(
        clientId: Long,
    ): Flow<DataState<KYCLevel1Details?>> {
        return apiManager.kycLevel1Api
            .fetchKYCLevel1Details(clientId)
            .catch { DataState.Error(it, null) }
            .map { it.firstOrNull() }
            .asDataStateFlow().flowOn(ioDispatcher)
    }

    override suspend fun addKYCLevel1Details(
        clientId: Long,
        kycLevel1Details: KYCLevel1Details,
    ): DataState<String> {
        return try {
            withContext(ioDispatcher) {
                apiManager.kycLevel1Api.addKYCLevel1Details(clientId, kycLevel1Details)
            }

            DataState.Success("KYC Level One details added successfully")
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun updateKYCLevel1Details(
        clientId: Long,
        kycLevel1Details: KYCLevel1Details,
    ): DataState<String> {
        return try {
            withContext(ioDispatcher) {
                apiManager.kycLevel1Api.updateKYCLevel1Details(clientId, kycLevel1Details)
            }

            DataState.Success("KYC Level One details added successfully")
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }
}
