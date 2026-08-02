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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kpt.core.base.store.infra.FetchedAtRepository
import kpt.core.base.store.screen.FetchPolicy
import kpt.core.base.store.screen.asScreenStream
import kpt.core.data.infra.NetworkMonitor as StoreNetworkMonitor
import kpt.core.store.AppStoreRegistry
import kpt.core.store.wallet.selfaccounts.SelfAccountsKey
import org.mifospay.core.common.DataState
import org.mifospay.core.common.ScreenState
import org.mifospay.core.common.asScreenStateFlow
import org.mifospay.core.data.util.toForkScreenStateFlow
import org.mifospay.core.data.mapper.toAccount
import org.mifospay.core.data.mapper.toModel
import org.mifospay.core.data.repository.AccountRepository
import org.mifospay.core.data.util.Constants
import org.mifospay.core.model.account.Account
import org.mifospay.core.model.account.AccountTransferPayload
import org.mifospay.core.model.savingsaccount.Transaction
import org.mifospay.core.model.savingsaccount.TransferDetail
import org.mifospay.core.model.search.AccountResult
import org.mifospay.core.network.FineractApiManager
import org.mifospay.core.network.SelfServiceApiManager
import org.mobilenativefoundation.store.store5.Store

// TODO use self api for account operations later
class AccountRepositoryImpl(
    private val apiManager: FineractApiManager,
    private val selfManager: SelfServiceApiManager,
    private val ioDispatcher: CoroutineDispatcher,
    // Phase-5 Batch-3 LEDGER wiring — injected by RepositoryModule so the
    // store-backed getSelfAccountsScreen(...) can consume Store5 via
    // asScreenStream(...). Nullable-default so existing unit tests without the
    // store harness continue to compile; getSelfAccounts(...) — the legacy
    // asScreenStateFlow path — is unaffected.
    private val selfAccountsStore: Store<SelfAccountsKey, List<Account>>? = null,
    private val storeNetworkMonitor: StoreNetworkMonitor? = null,
    private val fetchedAtRepository: FetchedAtRepository? = null,
) : AccountRepository {

    override fun getTransaction(
        accountId: Long,
        transactionId: Long,
    ): Flow<ScreenState<Transaction>> {
        return selfManager.accountTransfersApi
            .getTransaction(accountId, transactionId)
            .map { it.toModel() }
            .asScreenStateFlow()
            .flowOn(ioDispatcher)
    }

    override fun getAccountTransfer(transferId: Long): Flow<ScreenState<TransferDetail>> {
        return selfManager.accountTransfersApi
            .getAccountTransfer(transferId.toInt())
            .asScreenStateFlow()
            .flowOn(ioDispatcher)
    }

    override fun searchAccounts(query: String): Flow<ScreenState<List<AccountResult>>> {
        return selfManager.accountTransfersApi
            .searchAccounts(query, "savings")
            .asScreenStateFlow(isEmpty = { it.isEmpty() })
            .flowOn(ioDispatcher)
    }

    override fun getSelfAccounts(clientId: Long): Flow<ScreenState<List<Account>>> {
        return selfManager.clientsApi
            .getAccounts(clientId, Constants.SAVINGS)
            .map { it.toAccount() }
            .asScreenStateFlow(isEmpty = { it.isEmpty() })
            .flowOn(ioDispatcher)
    }

    // Phase-5 Batch-3 LEDGER read — GOAL D13 (`createStore` + CACHE_FIRST_SWR).
    //
    // See the interface KDoc for the shape contract. Requires the three
    // store-adapter dependencies (selfAccountsStore + NetworkMonitor +
    // FetchedAtRepository). If any is null (test wiring), we IllegalState —
    // production DI in RepositoryModule wires all three unconditionally.
    override fun getSelfAccountsScreen(
        clientId: Long,
        scope: CoroutineScope,
    ): Flow<ScreenState<List<Account>>> {
        val store = checkNotNull(selfAccountsStore) {
            "getSelfAccountsScreen requires the `selfAccounts` Store5 wiring. Verify " +
                "RepositoryModule bound AppStoreRegistry.SelfAccounts and injected it here."
        }
        val netMon = checkNotNull(storeNetworkMonitor) {
            "getSelfAccountsScreen requires kmptoolkit NetworkMonitor. Verify DataModule bound it."
        }
        val fetchedAtRepo = checkNotNull(fetchedAtRepository) {
            "getSelfAccountsScreen requires FetchedAtRepository. Verify DataModule bound it."
        }
        return store.asScreenStream(
            key = SelfAccountsKey(clientId),
            networkMonitor = netMon,
            fetchedAtRepository = fetchedAtRepo,
            cacheKey = "wallet_self_accounts-$clientId",
            scope = scope,
            isEmpty = { it.isEmpty() },
            fetchPolicy = FetchPolicy.CACHE_FIRST_SWR,
            ttl = AppStoreRegistry.Ttl.SELF_ACCOUNTS,
        ).state.toForkScreenStateFlow()
    }

    override suspend fun makeTransfer(payload: AccountTransferPayload): DataState<String> {
        return try {
            withContext(ioDispatcher) {
                selfManager.accountTransfersApi.makeTransfer(payload)
            }

            DataState.Success("Transaction Successful")
        } catch (e: Exception) {
            DataState.Error(e, null)
        }
    }
}
