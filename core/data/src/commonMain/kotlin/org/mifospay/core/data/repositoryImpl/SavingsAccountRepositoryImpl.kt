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

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kpt.core.base.store.infra.FetchedAtRepository
import kpt.core.base.store.screen.FetchPolicy
import kpt.core.base.store.screen.ScreenDataStream
import kpt.core.base.store.screen.asScreenStream
import kpt.core.data.infra.NetworkMonitor as StoreNetworkMonitor
import kpt.core.store.AppStoreRegistry
import kpt.core.store.wallet.account.AccountDetailKey
import org.mifospay.core.common.Constants
import org.mifospay.core.common.DataState
import org.mifospay.core.common.ScreenStateStream
import org.mifospay.core.common.asScreenStateFlow
import org.mifospay.core.data.mapper.toModel
import org.mifospay.core.data.mapper.toSavingDetail
import org.mifospay.core.data.repository.SavingsAccountRepository
import org.mifospay.core.model.savingsaccount.CreateNewSavingEntity
import org.mifospay.core.model.savingsaccount.SavingAccountDetail
import org.mifospay.core.model.savingsaccount.SavingAccountTemplate
import org.mifospay.core.model.savingsaccount.SavingsWithAssociationsEntity
import org.mifospay.core.model.savingsaccount.Transaction
import org.mifospay.core.model.savingsaccount.TransactionsEntity
import org.mifospay.core.model.savingsaccount.UpdateSavingAccountEntity
import org.mifospay.core.network.SelfServiceApiManager
import org.mifospay.core.network.model.entity.Page
import org.mobilenativefoundation.store.store5.Store

class SavingsAccountRepositoryImpl(
    private val apiManager: SelfServiceApiManager,
    private val ioDispatcher: CoroutineDispatcher,
    // Phase-5 Batch-1 SINGLE-ROW-PER-KEY wiring — injected by RepositoryModule so
    // the store-backed getAccountDetailScreen(...) can consume Store5 via
    // asScreenStream(...). Nullable-default so existing unit tests without the
    // store harness continue to compile; getAccountDetail(...) — the legacy
    // asScreenStateFlow path — is unaffected.
    private val accountDetailStore: Store<AccountDetailKey, SavingAccountDetail>? = null,
    private val storeNetworkMonitor: StoreNetworkMonitor? = null,
    private val fetchedAtRepository: FetchedAtRepository? = null,
) : SavingsAccountRepository {
    override suspend fun getSavingsAccounts(
        limit: Int,
    ): ScreenStateStream<Page<SavingsWithAssociationsEntity>> {
        return apiManager.savingAccountsListApi
            .getSavingsAccounts(limit)
            .asScreenStateFlow(isEmpty = { it.pageItems.isEmpty() })
            .flowOn(ioDispatcher)
    }

    override suspend fun getSavingsWithAssociations(
        accountId: Long,
        associationType: String,
    ): ScreenStateStream<SavingsWithAssociationsEntity> {
        return apiManager.savingAccountsListApi
            .getSavingsWithAssociations(accountId, associationType)
            .asScreenStateFlow()
            .flowOn(ioDispatcher)
    }

    override fun getAccountDetail(accountId: Long): ScreenStateStream<SavingAccountDetail> {
        return apiManager.savingAccountsListApi
            .getSavingsWithAssociations(accountId, Constants.TRANSACTIONS)
            .map(SavingsWithAssociationsEntity::toSavingDetail)
            .asScreenStateFlow()
            .flowOn(ioDispatcher)
    }

    // Phase-5 Batch-1 SINGLE-ROW-PER-KEY read — GOAL D13 (`createStore` + CACHE_FIRST_SWR).
    //
    // See the interface KDoc for the shape contract. Requires the three store-adapter
    // dependencies (accountDetailStore + NetworkMonitor + FetchedAtRepository).
    // If any is null (test wiring), we IllegalState — production DI in
    // RepositoryModule wires all three unconditionally.
    override fun getAccountDetailStream(
        accountId: Long,
        scope: CoroutineScope,
    ): ScreenDataStream<SavingAccountDetail> {
        val store = checkNotNull(accountDetailStore) {
            "getAccountDetailStream requires the `accountDetail` Store5 wiring. Verify " +
                "RepositoryModule bound AppStoreRegistry.AccountDetail and injected it here."
        }
        val netMon = checkNotNull(storeNetworkMonitor) {
            "getAccountDetailStream requires kmptoolkit NetworkMonitor. Verify DataModule bound it."
        }
        val fetchedAtRepo = checkNotNull(fetchedAtRepository) {
            "getAccountDetailStream requires FetchedAtRepository. Verify DataModule bound it."
        }
        return store.asScreenStream(
            key = AccountDetailKey(accountId),
            networkMonitor = netMon,
            fetchedAtRepository = fetchedAtRepo,
            cacheKey = "wallet_saving_account_details-$accountId",
            scope = scope,
            fetchPolicy = FetchPolicy.CACHE_FIRST_SWR,
            ttl = AppStoreRegistry.Ttl.ACCOUNT_DETAIL,
        )
    }

    override suspend fun createSavingsAccount(
        savingAccount: CreateNewSavingEntity,
    ): DataState<String> {
        return try {
            withContext(ioDispatcher) {
                apiManager.savingAccountsListApi.createSavingsAccount(savingAccount)
            }

            DataState.Success("Savings Account Created Successfully")
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun updateSavingsAccount(
        accountId: Long,
        savingAccount: UpdateSavingAccountEntity,
    ): DataState<String> {
        return try {
            withContext(ioDispatcher) {
                apiManager.savingAccountsListApi.updateSavingsAccount(accountId, savingAccount)
            }

            DataState.Success("Savings Account Updated Successfully")
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun unblockAccount(
        accountId: Long,
    ): DataState<String> {
        return try {
            withContext(ioDispatcher) {
                apiManager.savingAccountsListApi.blockUnblockAccount(accountId, "unblock")
            }

            DataState.Success("Account unblocked successfully")
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun blockAccount(accountId: Long): DataState<String> {
        return try {
            withContext(ioDispatcher) {
                apiManager.savingAccountsListApi.blockUnblockAccount(accountId, "block")
            }

            DataState.Success("Account blocked successfully")
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun getSavingAccountTransaction(
        accountId: Long,
        transactionId: Long,
    ): ScreenStateStream<Transaction> {
        return apiManager.savingAccountsListApi
            .getSavingAccountTransaction(accountId, transactionId)
            .map(TransactionsEntity::toModel)
            .asScreenStateFlow()
            .flowOn(ioDispatcher)
    }

    override suspend fun payViaMobile(accountId: Long): ScreenStateStream<Transaction> {
        return apiManager.savingAccountsListApi
            .payViaMobile(accountId)
            .map(TransactionsEntity::toModel)
            .asScreenStateFlow()
            .flowOn(ioDispatcher)
    }

    override fun getSavingAccountTemplate(clientId: Long): ScreenStateStream<SavingAccountTemplate> {
        return apiManager.savingAccountsListApi
            .getSavingAccountTemplate(clientId)
            .asScreenStateFlow()
            .flowOn(ioDispatcher)
    }
}
