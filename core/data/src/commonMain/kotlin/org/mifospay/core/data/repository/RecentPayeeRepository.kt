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
import org.mifospay.core.model.account.RecentPayee

/**
 * Repository for fetching recent paid beneficiaries.
 * Derives recent payees from transaction history by:
 * 1. Fetching DEBIT transactions from savings account
 * 2. Getting transfer details for each transaction
 * 3. Extracting unique recipients sorted by most recent
 */
interface RecentPayeeRepository {

    /**
     * Fetches recent payees for a given savings account.
     *
     * @param accountId The savings account ID to fetch transactions from
     * @param limit Maximum number of recent payees to return (default 10)
     * @return Flow of DataState containing list of recent payees sorted by most recent first
     */
    fun getRecentPayees(
        accountId: Long,
        limit: Int = 10,
    ): Flow<DataState<List<RecentPayee>>>
}
