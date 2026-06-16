/*
 * Copyright 2026 Mifos Initiative
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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.mifospay.core.common.DataState
import org.mifospay.core.data.repository.AccountRepository
import org.mifospay.core.data.repository.WidgetRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.datastore.WidgetPreferenceDataSource
import org.mifospay.core.model.widget.WidgetData

/**
 * Concrete implementation of [WidgetRepository].
 *
 * Follows the same pattern as [AccountRepositoryImpl], [ClientRepositoryImpl], etc.:
 *   - Constructor-injected [ioDispatcher] for all I/O scheduling
 *   - Flow methods apply [flowOn] at the end of the chain
 *   - Suspend methods wrap the call body in [withContext]
 *
 * Thin delegation layer — all real persistence is in [WidgetPreferenceDataSource].
 */
class WidgetRepositoryImpl(
    private val widgetDataSource: WidgetPreferenceDataSource,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val accountRepository: AccountRepository,
    private val ioDispatcher: CoroutineDispatcher,
) : WidgetRepository {

    override val widgetDataStream: Flow<WidgetData>
        get() = widgetDataSource.widgetData

    override suspend fun getWidgetData(): WidgetData =
        withContext(ioDispatcher) {
            val widgetData = widgetDataSource.widgetData.value

            val defaultAccountId = userPreferencesRepository.defaultAccount.value?.accountId
                ?: return@withContext widgetData

            val clientId = userPreferencesRepository.clientId.value
                ?: return@withContext widgetData

            val accounts = accountRepository
                .getSelfAccounts(clientId)
                .firstOrNull { it is DataState.Success || it is DataState.Error }
                ?: return@withContext widgetData

            if (accounts is DataState.Error) throw accounts.exception
            if (accounts !is DataState.Success) return@withContext widgetData

            val defaultAccount = accounts.data.firstOrNull { it.id == defaultAccountId }
                ?: return@withContext widgetData

            widgetData.copy(
                currentBalance = defaultAccount.balance,
                currency = defaultAccount.currency.code,
                accountNumber = defaultAccount.number,
            )
        }

    override suspend fun saveWidgetData(data: WidgetData) = withContext(ioDispatcher) {
        widgetDataSource.updateWidgetData(data)
    }

    override suspend fun clearWidgetData() = withContext(ioDispatcher) {
        widgetDataSource.clearWidgetData()
    }
}
