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
import kotlinx.coroutines.flow.first
import org.mifospay.core.common.DataState
import org.mifospay.core.model.autopay.Bill

/**
 * TODO: This implementation currently uses local storage (Multiplatform Settings) for bill data.
 * When the backend APIs for bill management are clarified and implemented, this should be
 * refactored to use network-based repository pattern similar to other repositories in the codebase
 * (e.g., UserRepositoryImpl, BeneficiaryRepositoryImpl) with proper API integration.
 */
class BillRepositoryImpl(
    private val billDataSource: BillDataSource,
) : BillRepository {

    override fun getAllBills(): Flow<List<Bill>> {
        return billDataSource.bills
    }

    override suspend fun getBillById(id: String): Bill? {
        return try {
            val bills = billDataSource.bills.first()
            bills.find { it.id == id }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun saveBill(bill: Bill): DataState<Bill> {
        return try {
            val existingBills = billDataSource.bills.first()

            // Check if bill already exists
            val existingBill = existingBills.find {
                it.name == bill.name && it.billerId == bill.billerId
            }

            if (existingBill != null) {
                DataState.Error(Exception("Bill with this name and biller already exists"))
            } else {
                billDataSource.addBill(bill)
                DataState.Success(bill)
            }
        } catch (e: Exception) {
            DataState.Error(Exception("Failed to save bill: ${e.message}"))
        }
    }

    override suspend fun updateBill(bill: Bill): DataState<Bill> {
        return try {
            val existingBills = billDataSource.bills.first().toMutableList()
            val index = existingBills.indexOfFirst { it.id == bill.id }

            if (index != -1) {
                existingBills[index] = bill
                billDataSource.updateBills(existingBills)
                DataState.Success(bill)
            } else {
                DataState.Error(Exception("Bill not found"))
            }
        } catch (e: Exception) {
            DataState.Error(Exception("Failed to update bill: ${e.message}"))
        }
    }

    override suspend fun deleteBill(id: String): DataState<Unit> {
        return try {
            billDataSource.removeBill(id)
            DataState.Success(Unit)
        } catch (e: Exception) {
            DataState.Error(Exception("Failed to delete bill: ${e.message}"))
        }
    }

    override suspend fun searchBillsByName(query: String): List<Bill> {
        return try {
            val bills = billDataSource.bills.first()
            bills.filter { it.name.contains(query, ignoreCase = true) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun clearAllBills(): DataState<Unit> {
        return try {
            billDataSource.clearBills()
            DataState.Success(Unit)
        } catch (e: Exception) {
            DataState.Error(Exception("Failed to clear bills: ${e.message}"))
        }
    }
}
