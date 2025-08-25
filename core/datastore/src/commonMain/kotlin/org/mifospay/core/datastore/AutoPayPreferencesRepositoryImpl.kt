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

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import org.mifospay.core.common.DataState
import org.mifospay.core.model.autopay.AutoPay
import org.mifospay.core.model.autopay.AutoPayHistory
import org.mifospay.core.model.autopay.UpcomingPayment

class AutoPayPreferencesRepositoryImpl(
    private val autoPayPreferencesDataSource: AutoPayPreferencesDataSource,
    private val ioDispatcher: CoroutineDispatcher,
    unconfinedDispatcher: CoroutineDispatcher,
) : AutoPayPreferencesRepository {
    private val unconfinedScope = CoroutineScope(unconfinedDispatcher)

    override val isAutoPayEnabled: StateFlow<Boolean> = autoPayPreferencesDataSource.isAutoPayEnabled

    override val cachedAutoPaySchedules: Flow<List<AutoPay>> = autoPayPreferencesDataSource.cachedAutoPaySchedules.flowOn(ioDispatcher)

    override val cachedUpcomingPayments: Flow<List<UpcomingPayment>> = autoPayPreferencesDataSource.cachedUpcomingPayments.flowOn(ioDispatcher)

    override val cachedAutoPayHistory: Flow<List<AutoPayHistory>> = autoPayPreferencesDataSource.cachedAutoPayHistory.flowOn(ioDispatcher)

    override val lastSyncTimestamp: StateFlow<Long> = autoPayPreferencesDataSource.lastSyncTimestamp

    override suspend fun updateAutoPayEnabled(enabled: Boolean): DataState<Unit> {
        return try {
            autoPayPreferencesDataSource.updateAutoPayEnabled(enabled)
            DataState.Success(Unit)
        } catch (e: Exception) {
            DataState.Error(e, null)
        }
    }

    override suspend fun cacheAutoPaySchedules(schedules: List<AutoPay>): DataState<Unit> {
        return try {
            autoPayPreferencesDataSource.cacheAutoPaySchedules(schedules)
            DataState.Success(Unit)
        } catch (e: Exception) {
            DataState.Error(e, null)
        }
    }

    override suspend fun cacheUpcomingPayments(payments: List<UpcomingPayment>): DataState<Unit> {
        return try {
            autoPayPreferencesDataSource.cacheUpcomingPayments(payments)
            DataState.Success(Unit)
        } catch (e: Exception) {
            DataState.Error(e, null)
        }
    }

    override suspend fun cacheAutoPayHistory(history: List<AutoPayHistory>): DataState<Unit> {
        return try {
            autoPayPreferencesDataSource.cacheAutoPayHistory(history)
            DataState.Success(Unit)
        } catch (e: Exception) {
            DataState.Error(e, null)
        }
    }

    override suspend fun updateLastSyncTimestamp(timestamp: Long): DataState<Unit> {
        return try {
            autoPayPreferencesDataSource.updateLastSyncTimestamp(timestamp)
            DataState.Success(Unit)
        } catch (e: Exception) {
            DataState.Error(e, null)
        }
    }

    suspend fun updateLastSyncTimestamp(): DataState<Unit> {
        return try {
            autoPayPreferencesDataSource.updateLastSyncTimestamp()
            DataState.Success(Unit)
        } catch (e: Exception) {
            DataState.Error(e, null)
        }
    }

    override suspend fun clearCache(): DataState<Unit> {
        return try {
            autoPayPreferencesDataSource.clearCache()
            DataState.Success(Unit)
        } catch (e: Exception) {
            DataState.Error(e, null)
        }
    }

    override suspend fun getCachedAutoPaySchedule(autoPayId: Long): AutoPay? {
        return autoPayPreferencesDataSource.getCachedAutoPaySchedule(autoPayId)
    }

    override suspend fun isCacheStale(maxAgeMinutes: Long): Boolean {
        return autoPayPreferencesDataSource.isCacheStale(maxAgeMinutes)
    }
}
