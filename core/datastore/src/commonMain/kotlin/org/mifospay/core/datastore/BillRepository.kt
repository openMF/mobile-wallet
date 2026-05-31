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
import org.mifospay.core.common.DataState
import org.mifospay.core.model.autopay.Bill

interface BillRepository {
    /**
     * Get all saved bills
     */
    fun getAllBills(): Flow<List<Bill>>

    /**
     * Get bill by ID
     */
    suspend fun getBillById(id: String): Bill?

    /**
     * Save a new bill
     */
    suspend fun saveBill(bill: Bill): DataState<Bill>

    /**
     * Update an existing bill
     */
    suspend fun updateBill(bill: Bill): DataState<Bill>

    /**
     * Delete a bill
     */
    suspend fun deleteBill(id: String): DataState<Unit>

    /**
     * Search bills by name
     */
    suspend fun searchBillsByName(query: String): List<Bill>

    /**
     * Clear all bills
     */
    suspend fun clearAllBills(): DataState<Unit>
}
