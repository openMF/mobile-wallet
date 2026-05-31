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
import kotlinx.coroutines.flow.map
import org.mifospay.core.common.DataState
import org.mifospay.core.common.asDataStateFlow
import org.mifospay.core.data.repository.AutoPayHistoryRepository
import org.mifospay.core.data.repository.AutoPayHistoryStatistics
import org.mifospay.core.model.autopay.AutoPayHistory
import org.mifospay.core.model.autopay.PaymentStatus
import org.mifospay.core.network.FineractApiManager
import org.mifospay.core.network.model.entity.Page

// TODO: Align repository with final API response/request schema once confirmed by backend

class AutoPayHistoryRepositoryImpl(
    private val apiManager: FineractApiManager,
    private val ioDispatcher: CoroutineDispatcher,
) : AutoPayHistoryRepository {

    override fun getAutoPayHistory(autoPayId: Long): Flow<DataState<List<AutoPayHistory>>> {
        return apiManager.autoPayApi.getAutoPayHistory(autoPayId, 100)
            .map { page -> page.pageItems }
            .catch { DataState.Error(it, null) }
            .asDataStateFlow()
    }

    override fun getAutoPayHistoryWithPagination(
        autoPayId: Long,
        limit: Int,
        offset: Int,
    ): Flow<DataState<Page<AutoPayHistory>>> {
        return apiManager.autoPayApi.getAutoPayHistory(autoPayId, limit)
            .map { page -> page }
            .catch { DataState.Error(it, null) }
            .asDataStateFlow()
    }

    override suspend fun getHistoryById(id: Long): DataState<AutoPayHistory> {
        return DataState.Error(Exception("Individual history lookup not supported in read-only mode"), null)
    }

    override fun getHistoryByStatus(status: String): Flow<DataState<List<AutoPayHistory>>> {
        return apiManager.autoPayApi.getAutoPayHistory(0, 100) // Get all and filter client-side
            .map { page ->
                val filtered = page.pageItems.filter { it.status?.name == status }
                filtered
            }
            .catch { DataState.Error(it, null) }
            .asDataStateFlow()
    }

    override fun getHistoryByDateRange(
        fromDate: String,
        toDate: String,
    ): Flow<DataState<List<AutoPayHistory>>> {
        return apiManager.autoPayApi.getAutoPayHistory(0, 100) // Get all and filter client-side
            .map { page ->
                val filtered = page.pageItems.filter { history ->
                    val transactionDate = history.transactionDate
                    transactionDate != null && transactionDate >= fromDate && transactionDate <= toDate
                }
                filtered
            }
            .catch { DataState.Error(it, null) }
            .asDataStateFlow()
    }

    override fun searchHistory(query: String): Flow<DataState<List<AutoPayHistory>>> {
        return apiManager.autoPayApi.getAutoPayHistory(0, 100) // Get all and filter client-side
            .map { page ->
                val filtered = page.pageItems.filter { history ->
                    history.recipientName?.contains(query, ignoreCase = true) == true ||
                        history.referenceNumber?.contains(query, ignoreCase = true) == true
                }
                filtered
            }
            .catch { DataState.Error(it, null) }
            .asDataStateFlow()
    }

    override fun getHistoryStatistics(autoPayId: Long): Flow<DataState<AutoPayHistoryStatistics>> {
        return apiManager.autoPayApi.getAutoPayHistory(autoPayId, 100)
            .map { page ->
                val historyList = page.pageItems

                val totalCount = historyList.size
                val successfulCount = historyList.count { it.status == PaymentStatus.COMPLETED }
                val failedCount = historyList.count { it.status == PaymentStatus.FAILED }
                val pendingCount = historyList.count { it.status == PaymentStatus.PENDING }

                val statistics = AutoPayHistoryStatistics(
                    totalTransactions = totalCount,
                    successfulTransactions = successfulCount,
                    failedTransactions = failedCount,
                    pendingTransactions = pendingCount,
                )
                statistics
            }
            .catch { DataState.Error(it, null) }
            .asDataStateFlow()
    }
}
