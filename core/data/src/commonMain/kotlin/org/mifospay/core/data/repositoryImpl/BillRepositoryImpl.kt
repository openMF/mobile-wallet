/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifospay.core.data.repositoryImpl

// TODO(phase-4): Migrate to ScreenState via `createOfflineStore` (Bill
// autopay-bills tier). Deferred from Phase-3 Batch B cutover because bills
// need a real Store5 offline store (SourceOfTruth = Room, fetcher = Fineract
// bills endpoint, bookkeeper for scheduled payment reconciliation) before
// their reads can adopt the fork-wide `ScreenStateStream<T>` envelope.

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.mifospay.core.common.DataState
import org.mifospay.core.common.asDataStateFlow
import org.mifospay.core.data.repository.BillRepository
import org.mifospay.core.data.repository.BillStatistics
import org.mifospay.core.data.util.BillErrorHandler
import org.mifospay.core.data.util.BillValidator
import org.mifospay.core.model.autopay.Bill
import org.mifospay.core.model.autopay.BillStatus
import org.mifospay.core.model.autopay.RecurrencePattern
import org.mifospay.core.network.FineractApiManager
import org.mifospay.core.network.services.BillStatisticsResponse
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Network-based implementation of BillRepository.
 *
 * TODO: This implementation uses placeholder API endpoints. When the backend APIs
 * for bill management are finalized, update the endpoints and request/response
 * models according to the actual API contract.
 */
class BillRepositoryImpl(
    private val apiManager: FineractApiManager,
    private val ioDispatcher: CoroutineDispatcher,
) : BillRepository {

    override fun getAllBills(clientId: Long): Flow<DataState<List<Bill>>> {
        return apiManager.billApi
            .getAllBills(clientId)
            .asDataStateFlow()
            .flowOn(ioDispatcher)
    }

    override suspend fun getBillById(id: String): DataState<Bill> {
        return try {
            val result = withContext(ioDispatcher) {
                apiManager.billApi.getBillById(id).first()
            }
            DataState.Success(result)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun createBill(bill: Bill): DataState<Bill> {
        return try {
            val billWithId = bill.copy(
                id = bill.id ?: generateBillId(),
                createdAt = Clock.System.now().toEpochMilliseconds(),
                updatedAt = Clock.System.now().toEpochMilliseconds(),
            )
            val result = withContext(ioDispatcher) {
                apiManager.billApi.createBill(billWithId).first()
            }
            DataState.Success(result)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun updateBill(bill: Bill): DataState<Bill> {
        return try {
            val billId =
                bill.id ?: return DataState.Error(Exception("Bill ID is required for update"))
            val updatedBill = bill.copy(
                updatedAt = Clock.System.now().toEpochMilliseconds(),
            )
            val result = withContext(ioDispatcher) {
                apiManager.billApi.updateBill(billId, updatedBill).first()
            }
            DataState.Success(result)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun deleteBill(id: String): DataState<Unit> {
        return try {
            withContext(ioDispatcher) {
                apiManager.billApi.deleteBill(id).first()
            }
            DataState.Success(Unit)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun getBillsByStatus(status: BillStatus): List<Bill> {
        return try {
            withContext(ioDispatcher) {
                apiManager.billApi.getBillsByStatus(status).first()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getBillsByBillerId(billerId: String): List<Bill> {
        return try {
            withContext(ioDispatcher) {
                apiManager.billApi.getBillsByBillerId(billerId).first()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getBillsByRecurrencePattern(pattern: RecurrencePattern): List<Bill> {
        return try {
            withContext(ioDispatcher) {
                apiManager.billApi.getBillsByRecurrencePattern(pattern).first()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun searchBillsByName(query: String): List<Bill> {
        return try {
            withContext(ioDispatcher) {
                apiManager.billApi.searchBillsByName(query).first()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getOverdueBills(): List<Bill> {
        return try {
            withContext(ioDispatcher) {
                apiManager.billApi.getOverdueBills().first()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getUpcomingBills(fromDate: Long, toDate: Long): List<Bill> {
        return try {
            withContext(ioDispatcher) {
                apiManager.billApi.getUpcomingBills(fromDate, toDate).first()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun updateBillStatus(billId: String, status: BillStatus): DataState<Bill> {
        return try {
            val result = withContext(ioDispatcher) {
                apiManager.billApi.updateBillStatus(billId, status).first()
            }
            DataState.Success(result)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun validateBill(bill: Bill): DataState<Boolean> {
        return try {
            val validationResult = BillValidator.validateBill(bill)
            if (validationResult.isValid) {
                DataState.Success(true)
            } else {
                val errorMessage = BillErrorHandler.handleValidationError(validationResult)
                DataState.Error(Exception(errorMessage))
            }
        } catch (e: Exception) {
            val errorMessage = BillErrorHandler.handleNetworkError(e)
            DataState.Error(Exception(errorMessage))
        }
    }

    // TODO catch and emit DataState error

    override fun getBillStatistics(clientId: Long): Flow<DataState<BillStatistics>> {
        return apiManager.billApi
            .getBillStatistics(clientId)
            .map { response: BillStatisticsResponse ->
                DataState.Success(
                    BillStatistics(
                        totalBills = response.totalBills,
                        activeBills = response.activeBills,
                        overdueBills = response.overdueBills,
                        totalAmount = response.totalAmount,
                        currency = response.currency,
                        upcomingPayments = response.upcomingPayments,
                        totalAmountThisMonth = response.totalAmountThisMonth,
                    ),
                )
            }
            .flowOn(ioDispatcher)
    }

    override suspend fun clearAllBills(): DataState<Unit> {
        return try {
            withContext(ioDispatcher) {
                apiManager.billApi.clearAllBills().first()
            }
            DataState.Success(Unit)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun generateBillId(): String {
        return "bill_${Clock.System.now().toEpochMilliseconds()}_${(0..999).random()}"
    }
}
