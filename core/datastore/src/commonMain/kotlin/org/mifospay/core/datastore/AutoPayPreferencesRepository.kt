/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.datastore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.mifospay.core.common.DataState
import org.mifospay.core.model.autopay.AutoPay
import org.mifospay.core.model.autopay.AutoPayHistory
import org.mifospay.core.model.autopay.UpcomingPayment

interface AutoPayPreferencesRepository {
    /**
     * AutoPay enabled state
     */
    val isAutoPayEnabled: StateFlow<Boolean>

    /**
     * Cached AutoPay schedules
     */
    val cachedAutoPaySchedules: Flow<List<AutoPay>>

    /**
     * Cached upcoming payments
     */
    val cachedUpcomingPayments: Flow<List<UpcomingPayment>>

    /**
     * Cached AutoPay history
     */
    val cachedAutoPayHistory: Flow<List<AutoPayHistory>>

    /**
     * Last sync timestamp
     */
    val lastSyncTimestamp: StateFlow<Long>

    /**
     * Update AutoPay enabled state
     */
    suspend fun updateAutoPayEnabled(enabled: Boolean): DataState<Unit>

    /**
     * Cache AutoPay schedules
     */
    suspend fun cacheAutoPaySchedules(schedules: List<AutoPay>): DataState<Unit>

    /**
     * Cache upcoming payments
     */
    suspend fun cacheUpcomingPayments(payments: List<UpcomingPayment>): DataState<Unit>

    /**
     * Cache AutoPay history
     */
    suspend fun cacheAutoPayHistory(history: List<AutoPayHistory>): DataState<Unit>

    /**
     * Update last sync timestamp
     */
    suspend fun updateLastSyncTimestamp(timestamp: Long): DataState<Unit>

    /**
     * Clear all cached data
     */
    suspend fun clearCache(): DataState<Unit>

    /**
     * Get cached AutoPay schedule by ID
     */
    suspend fun getCachedAutoPaySchedule(autoPayId: Long): AutoPay?

    /**
     * Check if cache is stale (older than specified time)
     */
    suspend fun isCacheStale(maxAgeMinutes: Long = 30): Boolean
}
