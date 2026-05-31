/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.repositoryImpl

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.mifospay.core.common.DataState
import org.mifospay.core.common.asDataStateFlow
import org.mifospay.core.data.repository.AutoPayRepository
import org.mifospay.core.data.repository.AutoPayStatistics
import org.mifospay.core.data.util.AutoPayValidator
import org.mifospay.core.model.autopay.AutoPay
import org.mifospay.core.model.autopay.AutoPayHistory
import org.mifospay.core.model.autopay.AutoPayPayload
import org.mifospay.core.model.autopay.AutoPayTemplate
import org.mifospay.core.model.autopay.AutoPayUpdatePayload
import org.mifospay.core.model.autopay.UpcomingPayment
import org.mifospay.core.network.FineractApiManager

class AutoPayRepositoryImpl(
    private val apiManager: FineractApiManager,
    private val ioDispatcher: CoroutineDispatcher,
) : AutoPayRepository {

    override fun getAutoPayTemplate(
        clientId: Long,
        sourceAccountId: Long,
    ): Flow<DataState<AutoPayTemplate>> {
        return apiManager.autoPayApi
            .getAutoPayTemplate(clientId, sourceAccountId)
            .catch { DataState.Error(it, null) }
            .asDataStateFlow().flowOn(ioDispatcher)
    }

    override fun getAllAutoPaySchedules(
        clientId: Long,
    ): Flow<DataState<List<AutoPay>>> {
        return apiManager.autoPayApi
            .getAllAutoPaySchedules(clientId)
            .catch { DataState.Error(it, null) }
            .asDataStateFlow().flowOn(ioDispatcher)
    }

    override fun getAutoPaySchedule(
        autoPayId: Long,
    ): Flow<DataState<AutoPay>> {
        return apiManager.autoPayApi
            .getAutoPaySchedule(autoPayId)
            .catch { DataState.Error(it, null) }
            .asDataStateFlow().flowOn(ioDispatcher)
    }

    override suspend fun createAutoPaySchedule(
        payload: AutoPayPayload,
    ): DataState<String> {
        return try {
            withContext(ioDispatcher) {
                apiManager.autoPayApi.createAutoPaySchedule(payload)
            }
            DataState.Success("AutoPay schedule created successfully")
        } catch (e: Exception) {
            DataState.Error(e, null)
        }
    }

    override suspend fun updateAutoPaySchedule(
        autoPayId: Long,
        payload: AutoPayUpdatePayload,
    ): DataState<String> {
        return try {
            withContext(ioDispatcher) {
                apiManager.autoPayApi.updateAutoPaySchedule(
                    autoPayId = autoPayId,
                    payload = payload,
                )
            }
            DataState.Success("AutoPay schedule updated successfully")
        } catch (e: Exception) {
            DataState.Error(e, null)
        }
    }

    override suspend fun deleteAutoPaySchedule(
        autoPayId: Long,
    ): DataState<String> {
        return try {
            withContext(ioDispatcher) {
                apiManager.autoPayApi.deleteAutoPaySchedule(autoPayId)
            }
            DataState.Success("AutoPay schedule deleted successfully")
        } catch (e: Exception) {
            DataState.Error(e, null)
        }
    }

    override suspend fun pauseAutoPaySchedule(
        autoPayId: Long,
    ): DataState<String> {
        return try {
            withContext(ioDispatcher) {
                apiManager.autoPayApi.pauseAutoPaySchedule(autoPayId)
            }
            DataState.Success("AutoPay schedule paused successfully")
        } catch (e: Exception) {
            DataState.Error(e, null)
        }
    }

    override suspend fun resumeAutoPaySchedule(
        autoPayId: Long,
    ): DataState<String> {
        return try {
            withContext(ioDispatcher) {
                apiManager.autoPayApi.resumeAutoPaySchedule(autoPayId)
            }
            DataState.Success("AutoPay schedule resumed successfully")
        } catch (e: Exception) {
            DataState.Error(e, null)
        }
    }

    override fun getAutoPayHistory(
        autoPayId: Long,
        limit: Int,
    ): Flow<DataState<org.mifospay.core.network.model.entity.Page<AutoPayHistory>>> {
        return apiManager.autoPayApi
            .getAutoPayHistory(autoPayId, limit)
            .catch { DataState.Error(it, null) }
            .asDataStateFlow().flowOn(ioDispatcher)
    }

    override fun getUpcomingPayments(
        clientId: Long,
        limit: Int,
    ): Flow<DataState<List<UpcomingPayment>>> {
        return apiManager.autoPayApi
            .getUpcomingPayments(clientId, limit)
            .catch { DataState.Error(it, null) }
            .asDataStateFlow().flowOn(ioDispatcher)
    }

    override fun getAutoPayStatistics(
        clientId: Long,
    ): Flow<DataState<AutoPayStatistics>> {
        return apiManager.autoPayApi
            .getAutoPayStatistics(clientId)
            .catch { DataState.Error(it, null) }
            .map { response ->
                AutoPayStatistics(
                    totalActiveSchedules = response.totalActiveSchedules,
                    totalPausedSchedules = response.totalPausedSchedules,
                    totalCompletedSchedules = response.totalCompletedSchedules,
                    totalUpcomingPayments = response.totalUpcomingPayments,
                    totalAmountThisMonth = response.totalAmountThisMonth,
                    currency = response.currency,
                )
            }
            .asDataStateFlow().flowOn(ioDispatcher)
    }

    override suspend fun validateAutoPayPayload(
        payload: AutoPayPayload,
    ): DataState<Boolean> {
        return try {
            withContext(ioDispatcher) {
                when (val validationResult = AutoPayValidator.validateAutoPayPayload(payload)) {
                    is AutoPayValidator.ValidationResult.Valid -> DataState.Success(true)
                    is AutoPayValidator.ValidationResult.Invalid -> {
                        DataState.Error(Exception(validationResult.errorMessage), null)
                    }
                }
            }
        } catch (e: Exception) {
            DataState.Error(e, null)
        }
    }
}
