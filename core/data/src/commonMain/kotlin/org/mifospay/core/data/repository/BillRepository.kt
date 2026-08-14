/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifospay.core.data.repository

import kotlinx.coroutines.flow.Flow
import org.mifospay.core.common.DataState
import org.mifospay.core.model.autopay.Bill
import org.mifospay.core.model.autopay.BillStatus
import org.mifospay.core.model.autopay.RecurrencePattern

interface BillRepository {
    /**
     * Get all bills for a client
     */
    fun getAllBills(clientId: Long): Flow<DataState<List<Bill>>>

    /**
     * Get bill by ID
     */
    suspend fun getBillById(id: String): DataState<Bill>

    /**
     * Create a new bill
     */
    suspend fun createBill(bill: Bill): DataState<Bill>

    /**
     * Update an existing bill
     */
    suspend fun updateBill(bill: Bill): DataState<Bill>

    /**
     * Delete a bill
     */
    suspend fun deleteBill(id: String): DataState<Unit>

    /**
     * Get bills by status
     */
    suspend fun getBillsByStatus(status: BillStatus): List<Bill>

    /**
     * Get bills by biller ID
     */
    suspend fun getBillsByBillerId(billerId: String): List<Bill>

    /**
     * Get bills by recurrence pattern
     */
    suspend fun getBillsByRecurrencePattern(pattern: RecurrencePattern): List<Bill>

    /**
     * Search bills by name
     */
    suspend fun searchBillsByName(query: String): List<Bill>

    /**
     * Get overdue bills
     */
    suspend fun getOverdueBills(): List<Bill>

    /**
     * Get upcoming bills within a date range
     */
    suspend fun getUpcomingBills(fromDate: Long, toDate: Long): List<Bill>

    /**
     * Update bill status
     */
    suspend fun updateBillStatus(billId: String, status: BillStatus): DataState<Bill>

    /**
     * Validate bill data before submission
     */
    suspend fun validateBill(bill: Bill): DataState<Boolean>

    /**
     * Get bill statistics for dashboard
     */
    fun getBillStatistics(clientId: Long): Flow<DataState<BillStatistics>>

    /**
     * Clear all bills
     */
    suspend fun clearAllBills(): DataState<Unit>
}

data class BillStatistics(
    val totalBills: Int = 0,
    val activeBills: Int = 0,
    val overdueBills: Int = 0,
    val totalAmount: Double = 0.0,
    val currency: String = "USD",
    val upcomingPayments: Int = 0,
    val totalAmountThisMonth: Double = 0.0,
)
