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

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.mifospay.core.common.DataState
import org.mifospay.core.data.repository.RecentPayeeRepository
import org.mifospay.core.model.account.RecentPayee
import org.mifospay.core.model.savingsaccount.TransferDetail
import org.mifospay.core.network.SelfServiceApiManager

/**
 * Implementation of [RecentPayeeRepository] that fetches recent payees from transaction history.
 *
 * Flow:
 * 1. Fetch savings account with transactions
 * 2. Filter DEBIT (withdrawal) transactions that have a transferId
 * 3. Fetch TransferDetail for each unique transferId in parallel
 * 4. Group by recipient (toClient.id) and keep most recent
 * 5. Sort by transfer date descending
 */
class RecentPayeeRepositoryImpl(
    private val selfServiceApiManager: SelfServiceApiManager,
    private val ioDispatcher: CoroutineDispatcher,
) : RecentPayeeRepository {

    override fun getRecentPayees(
        accountId: Long,
        limit: Int,
    ): Flow<DataState<List<RecentPayee>>> = flow {
        emit(DataState.Loading)

        try {
            // Step 1: Fetch savings account with transactions
            val savingsApi = selfServiceApiManager.savingAccountsListApi
            val accountTransfersApi = selfServiceApiManager.accountTransfersApi

            Logger.d { "RecentPayee: Fetching transactions for account $accountId" }

            // Collect the flow to get the savings account data
            var savingsAccount: org.mifospay.core.model.savingsaccount.SavingsWithAssociationsEntity? = null
            savingsApi.getSavingsWithAssociations(accountId, ASSOCIATIONS_TRANSACTIONS)
                .collect { savingsAccount = it }

            if (savingsAccount == null) {
                emit(DataState.Success(emptyList()))
                return@flow
            }

            // Step 2: Filter withdrawal transactions with transferId
            val withdrawalTransactions = savingsAccount!!.transactions
                .filter { it.transactionType.withdrawal && it.transfer != null }
                .sortedByDescending { it.submittedOnDate.toDateLong() }
                .take(MAX_TRANSACTIONS_TO_PROCESS)

            Logger.d { "RecentPayee: Found ${withdrawalTransactions.size} withdrawal transactions" }

            if (withdrawalTransactions.isEmpty()) {
                emit(DataState.Success(emptyList()))
                return@flow
            }

            // Step 3: Get unique transfer IDs
            val uniqueTransferIds = withdrawalTransactions
                .mapNotNull { it.transfer?.id }
                .distinct()

            Logger.d { "RecentPayee: Fetching details for ${uniqueTransferIds.size} transfers" }

            // Step 4: Fetch transfer details in parallel
            val transferDetails = coroutineScope {
                uniqueTransferIds.map { transferId ->
                    async {
                        try {
                            var detail: TransferDetail? = null
                            accountTransfersApi.getAccountTransfer(transferId.toInt())
                                .collect { detail = it }
                            detail
                        } catch (e: Exception) {
                            Logger.w(e) { "RecentPayee: Failed to fetch transfer $transferId" }
                            null
                        }
                    }
                }.awaitAll().filterNotNull()
            }

            Logger.d { "RecentPayee: Retrieved ${transferDetails.size} transfer details" }

            // Step 5: Group by recipient and keep most recent
            val recentPayees = transferDetails
                .groupBy { it.toClient.id }
                .mapNotNull { (_, transfers) ->
                    // Get most recent transfer for this recipient
                    val mostRecent = transfers.maxByOrNull { it.transferDate }
                        ?: return@mapNotNull null

                    RecentPayee(
                        clientId = mostRecent.toClient.id,
                        clientName = mostRecent.toClient.displayName,
                        accountId = mostRecent.toAccount.id,
                        accountNo = mostRecent.toAccount.accountNo,
                        officeId = mostRecent.toClient.officeId,
                        officeName = mostRecent.toClient.officeName,
                        lastTransferDate = mostRecent.transferDate,
                        lastAmount = mostRecent.transferAmount,
                        currency = mostRecent.currency.code,
                    )
                }
                .sortedByDescending { it.lastTransferDate }
                .take(limit)

            Logger.d { "RecentPayee: Returning ${recentPayees.size} recent payees" }

            emit(DataState.Success(recentPayees))
        } catch (e: Exception) {
            Logger.e(e) { "RecentPayee: Failed to fetch recent payees" }
            emit(DataState.Error(e, null))
        }
    }.flowOn(ioDispatcher)

    /**
     * Converts date list [year, month, day] to comparable Long.
     */
    private fun List<Int>.toDateLong(): Long {
        return if (size >= 3) {
            this[0] * 10000L + this[1] * 100L + this[2]
        } else {
            0L
        }
    }

    companion object {
        private const val ASSOCIATIONS_TRANSACTIONS = "transactions"
        private const val MAX_TRANSACTIONS_TO_PROCESS = 50
    }
}
