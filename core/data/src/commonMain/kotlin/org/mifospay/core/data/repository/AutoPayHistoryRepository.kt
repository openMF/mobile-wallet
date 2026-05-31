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
import org.mifospay.core.model.autopay.AutoPayHistory
import org.mifospay.core.network.model.entity.Page

// TODO: Align repository with final API response/request schema once confirmed by backend

interface AutoPayHistoryRepository {
    /**
     * Get AutoPay history for a specific AutoPay schedule
     */
    fun getAutoPayHistory(autoPayId: Long): Flow<DataState<List<AutoPayHistory>>>

    /**
     * Get AutoPay history with pagination
     */
    fun getAutoPayHistoryWithPagination(
        autoPayId: Long,
        limit: Int = 20,
        offset: Int = 0,
    ): Flow<DataState<Page<AutoPayHistory>>>

    /**
     * Get AutoPay history by ID
     */
    suspend fun getHistoryById(id: Long): DataState<AutoPayHistory>

    /**
     * Get AutoPay history by status
     */
    fun getHistoryByStatus(status: String): Flow<DataState<List<AutoPayHistory>>>

    /**
     * Get AutoPay history by date range
     */
    fun getHistoryByDateRange(
        fromDate: String,
        toDate: String,
    ): Flow<DataState<List<AutoPayHistory>>>

    /**
     * Search AutoPay history
     */
    fun searchHistory(query: String): Flow<DataState<List<AutoPayHistory>>>

    /**
     * Get history statistics
     */
    fun getHistoryStatistics(autoPayId: Long): Flow<DataState<AutoPayHistoryStatistics>>
}

data class AutoPayHistoryStatistics(
    val totalTransactions: Int = 0,
    val successfulTransactions: Int = 0,
    val failedTransactions: Int = 0,
    val pendingTransactions: Int = 0,
    val totalAmount: Double = 0.0,
    val successfulAmount: Double = 0.0,
    val failedAmount: Double = 0.0,
    val currency: String = "USD",
)
