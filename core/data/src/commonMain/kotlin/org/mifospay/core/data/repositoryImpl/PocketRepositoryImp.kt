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

// Phase-5 Batch-2: this repository was Phase-4-deferred out of the ScreenState +
// Store5 migration because it owned an in-memory MutableStateFlow<AccountStatus>
// cache (`detailedPocketCache`) that made a naive `.asScreenStateFlow()` unsafe.
// The Batch-2 rollout moves that cache behind the `pocket` Store5 store (Room
// SoT) — see `kpt.core.store.wallet.pocket.PocketStore.kt`. This impl drops the
// in-memory cache in favor of the store-backed
// `getDetailedPocketAccountsScreen(...)` method + a Store5-native compound
// fetcher; `linkAccounts` / `delinkAccounts` write online and rely on the VM's
// refresh trigger (or SWR) to re-fetch (parity with Beneficiary writes).

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kpt.core.base.store.infra.FetchedAtRepository
import kpt.core.base.store.screen.FetchPolicy
import kpt.core.base.store.screen.asScreenStream
import kpt.core.data.infra.NetworkMonitor as StoreNetworkMonitor
import kpt.core.database.wallet.pocket.PocketDao
import kpt.core.database.wallet.pocket.toDomain
import kpt.core.store.AppStoreRegistry
import kpt.core.store.wallet.linkableaccount.LinkableAccountKey
import kpt.core.store.wallet.pocket.PocketKey
import org.mifospay.core.common.DataState
import org.mifospay.core.common.ScreenState
import org.mifospay.core.common.asDataStateFlow
import org.mifospay.core.data.mapper.pocket.toAccountStatus
import org.mifospay.core.data.mapper.pocket.toDomainList
import org.mifospay.core.data.repository.PocketRepository
import org.mifospay.core.data.util.NetworkMonitor
import org.mifospay.core.data.util.runAsDataState
import org.mifospay.core.data.util.toForkScreenStateFlow
import org.mifospay.core.data.util.withNetworkCheck
import org.mifospay.core.model.enums.AccountType
import org.mifospay.core.model.payload.PocketLinkPayload
import org.mifospay.core.model.pocket.DetailedPocketAccount
import org.mifospay.core.model.pocket.LinkableAccount
import org.mifospay.core.model.pocket.PocketAccount
import org.mifospay.core.network.SelfServiceApiManager
import org.mifospay.core.network.model.entity.pocket.PocketDelinkRequest
import org.mifospay.core.network.model.entity.pocket.PocketLinkRequest
import org.mobilenativefoundation.store.store5.Store

class PocketRepositoryImp(
    private val dataManager: SelfServiceApiManager,
    private val networkMonitor: NetworkMonitor,
    private val ioDispatcher: CoroutineDispatcher,
    // Phase-5 Batch-2 LEDGER wiring — the `pocket` Store5 store + its three
    // adapter dependencies. Nullable-default so existing unit tests without
    // the store harness continue to compile; the new `getDetailedPocketAccountsScreen`
    // path IllegalStates when unwired, and the getAvailableAccountsToLink
    // fallback (which used to read the in-memory cache) checks the store's
    // DAO snapshot directly (see [getAvailableAccountsToLink]).
    private val pocketStore: Store<PocketKey, List<DetailedPocketAccount>>? = null,
    private val storeNetworkMonitor: StoreNetworkMonitor? = null,
    private val fetchedAtRepository: FetchedAtRepository? = null,
    private val pocketDao: PocketDao? = null,
    // manage-pocket linkable-accounts Store5 wiring (replaces upstream PR
    // #2057's multiplatform-settings `linkable_accounts` cache in
    // `PocketPreferencesDataSource`). Nullable-default so existing unit tests
    // without the store harness continue to compile; the new
    // `getAvailableAccountsToLinkScreen` path IllegalStates when unwired
    // (production DI wires it unconditionally in `RepositoryModule`).
    private val linkableAccountsStore: Store<LinkableAccountKey, List<LinkableAccount>>? = null,
) : PocketRepository {

    private suspend fun fetchBasicPocketsFromNetwork(): List<PocketAccount> {
        return dataManager.pocketApi.getPocketAccounts().toDomainList()
    }

    override suspend fun getPocketAccounts(): DataState<List<PocketAccount>> {
        return runAsDataState(networkMonitor, ioDispatcher) {
            fetchBasicPocketsFromNetwork()
        }
    }

    // Phase-5 Batch-2 LEDGER read — GOAL D13 (`createStore` + CACHE_FIRST_SWR).
    //
    // Requires the store-adapter dependencies (pocketStore + NetworkMonitor +
    // FetchedAtRepository). If any is null (test wiring), we IllegalState —
    // production DI in RepositoryModule wires all three unconditionally.
    //
    // Note: the compound fetch (basic pockets + client accounts + N share
    // market-price round-trips) lives inside the store's fetcher lambda
    // (`providePocketStore`). This repository method is a pure passthrough
    // to `asScreenStream` — the pre-store `syncPockets` + `addAccountDetails`
    // helpers moved to `kpt.core.store.wallet.pocket.PocketStore.kt`.
    override fun getDetailedPocketAccountsScreen(
        clientId: Long,
        scope: CoroutineScope,
    ): Flow<ScreenState<List<DetailedPocketAccount>>> {
        val store = checkNotNull(pocketStore) {
            "getDetailedPocketAccountsScreen requires the `pocket` Store5 wiring. Verify " +
                "RepositoryModule bound AppStoreRegistry.Pocket and injected it here."
        }
        val netMon = checkNotNull(storeNetworkMonitor) {
            "getDetailedPocketAccountsScreen requires kmptoolkit NetworkMonitor. Verify DataModule bound it."
        }
        val fetchedAtRepo = checkNotNull(fetchedAtRepository) {
            "getDetailedPocketAccountsScreen requires FetchedAtRepository. Verify DataModule bound it."
        }
        return store.asScreenStream(
            key = PocketKey(clientId),
            networkMonitor = netMon,
            fetchedAtRepository = fetchedAtRepo,
            cacheKey = "wallet_pockets-$clientId",
            scope = scope,
            isEmpty = { it.isEmpty() },
            fetchPolicy = FetchPolicy.CACHE_FIRST_SWR,
            ttl = AppStoreRegistry.Ttl.POCKET,
        ).state.toForkScreenStateFlow()
    }

    override suspend fun linkAccounts(
        payload: PocketLinkPayload,
        explicitlyAddedAccounts: List<DetailedPocketAccount>,
        clientId: Long,
    ): DataState<Unit> {
        // Pure online write — no local cache mutation. The pre-store impl
        // eagerly mutated `detailedPocketCache` here to give the VM a
        // momentary "already-updated" impression; the store5 rollout replaces
        // that with an SWR / manual refresh through the read store. The
        // `explicitlyAddedAccounts` parameter is preserved on the signature
        // for source-level compatibility with any calling code that populated
        // it, but is intentionally UNUSED — the next re-subscribe pulls fresh
        // data from the server.
        //
        // Suppress: retained for API compat; see rationale above.
        @Suppress("UnusedParameter")
        val ignoredExplicitAdds = explicitlyAddedAccounts

        return runAsDataState(networkMonitor, ioDispatcher) {
            val request = PocketLinkRequest(
                accountsDetail = payload.accountsDetail.map {
                    PocketLinkRequest.AccountDetail(
                        accountId = it.accountId,
                        accountType = it.accountType.name,
                    )
                },
            )
            dataManager.pocketApi.linkAccounts(request = request)
        }
    }

    override suspend fun delinkAccounts(
        pocketAccountMappingIds: List<Long>,
        clientId: Long,
    ): DataState<Unit> {
        // Same pure-online-write rationale as [linkAccounts] — no cache
        // mutation, no forceRefresh side-effect. The store's next SWR /
        // re-subscribe cycle pulls fresh data.
        return runAsDataState(networkMonitor, ioDispatcher) {
            val serverIds = pocketAccountMappingIds.filter { it > 0 }
            if (serverIds.isNotEmpty()) {
                val request = PocketDelinkRequest(serverIds)
                dataManager.pocketApi.delinkAccounts(request = request)
            }
        }
    }

    // manage-pocket linkable-accounts LEDGER read — GOAL D13 (`createStore` +
    // CACHE_FIRST_SWR + atomic replacePage). Replaces upstream PR #2057's
    // multiplatform-settings `linkable_accounts` cache in
    // `PocketPreferencesDataSource` (which this branch never wired into the
    // pocket read path — the Store5 architecture serves the same read
    // offline-first through Room SoT).
    //
    // Requires the store-adapter dependencies (linkableAccountsStore +
    // NetworkMonitor + FetchedAtRepository). If any is null (test wiring), we
    // IllegalState — production DI in RepositoryModule wires all three
    // unconditionally.
    //
    // Note: the compound fetch (clientsApi + N shareAccountApi round-trips +
    // pocketDao snapshot filter) lives inside the store's fetcher lambda
    // (`provideLinkableAccountsStore`). This repository method is a pure
    // passthrough to `asScreenStream` — parity with
    // `getDetailedPocketAccountsScreen` above.
    override fun getAvailableAccountsToLinkScreen(
        clientId: Long,
        scope: CoroutineScope,
    ): Flow<ScreenState<List<LinkableAccount>>> {
        val store = checkNotNull(linkableAccountsStore) {
            "getAvailableAccountsToLinkScreen requires the `linkableAccounts` Store5 wiring. " +
                "Verify RepositoryModule bound AppStoreRegistry.LinkableAccounts and injected it here."
        }
        val netMon = checkNotNull(storeNetworkMonitor) {
            "getAvailableAccountsToLinkScreen requires kmptoolkit NetworkMonitor. " +
                "Verify DataModule bound it."
        }
        val fetchedAtRepo = checkNotNull(fetchedAtRepository) {
            "getAvailableAccountsToLinkScreen requires FetchedAtRepository. " +
                "Verify DataModule bound it."
        }
        return store.asScreenStream(
            key = LinkableAccountKey(clientId),
            networkMonitor = netMon,
            fetchedAtRepository = fetchedAtRepo,
            cacheKey = "wallet_linkable_accounts-$clientId",
            scope = scope,
            isEmpty = { it.isEmpty() },
            fetchPolicy = FetchPolicy.CACHE_FIRST_SWR,
            ttl = AppStoreRegistry.Ttl.LINKABLE_ACCOUNTS,
        ).state.toForkScreenStateFlow()
    }

    override fun getAvailableAccountsToLink(clientId: Long): Flow<DataState<List<LinkableAccount>>> {
        return networkMonitor.withNetworkCheck(
            flow {
                val clientAccounts = dataManager.clientsApi.getClientAccounts(clientId)
                val availableAccounts = mutableListOf<LinkableAccount>()

                // Read the already-linked set from the Room SoT snapshot. Pre-store,
                // this consulted the in-memory `detailedPocketCache`; post-store it
                // consults `PocketDao.observeByClient(clientId).first()` — the same
                // rows the read store observes. The DAO is nullable-default for
                // test wiring; if it's absent we fall back to an empty
                // already-linked set (conservative behavior: surface everything
                // as linkable, same as when the pre-store cache was cold).
                val alreadyLinkedAccountIds: List<Long> = pocketDao
                    ?.observeByClient(clientId)
                    ?.first()
                    ?.map { it.toDomain().pocket.accountId }
                    .orEmpty()

                clientAccounts.loanAccounts.forEach { loan ->
                    if (loan.id !in alreadyLinkedAccountIds) {
                        availableAccounts.add(
                            LinkableAccount(
                                accountId = loan.id ?: 0L,
                                productName = loan.productName,
                                accountNumber = loan.accountNo,
                                accountType = AccountType.LOAN,
                                balance = loan.loanBalance,
                                currencyCode = loan.currency?.code,
                                // Upstream PR #2057 (manage-pocket) surfaces a per-currency
                                // display symbol ("$"/"₹"/…) on `LinkableAccount` so the
                                // link-accounts sheet renders balances with the same glyphs
                                // the dashboard uses. Sourced from the Fineract currency
                                // block; nullable-fallback consistent with `currencyCode`.
                                currencyDisplaySymbol = loan.currency?.displaySymbol,
                                decimalPlaces = loan.currency?.decimalPlaces,
                                status = loan.status?.toAccountStatus(),
                            ),
                        )
                    }
                }

                clientAccounts.savingsAccounts.forEach { savings ->
                    if (savings.id !in alreadyLinkedAccountIds) {
                        availableAccounts.add(
                            LinkableAccount(
                                accountId = savings.id,
                                productName = savings.productName,
                                accountNumber = savings.accountNo,
                                accountType = AccountType.SAVINGS,
                                balance = savings.accountBalance,
                                currencyCode = savings.currency.code,
                                currencyDisplaySymbol = savings.currency.displaySymbol,
                                decimalPlaces = savings.currency.decimalPlaces,
                                status = savings.status.toAccountStatus(),
                            ),
                        )
                    }
                }

                clientAccounts.shareAccounts.forEach { share ->
                    if (share.id !in alreadyLinkedAccountIds) {
                        var balance = 0.0
                        var currencyCode: String? = share.currency?.code
                        var currencyDisplaySymbol: String? = share.currency?.displaySymbol
                        var decimalPlaces: Int? = share.currency?.decimalPlaces

                        try {
                            val shareAccountDetails = dataManager.shareAccountApi
                                .getShareAccountDetails(share.id ?: 0L).first()
                            val approvedShares = shareAccountDetails.summary?.totalApprovedShares ?: 0
                            val currentMarketPrice = shareAccountDetails.currentMarketPrice ?: 0.0
                            balance = approvedShares * currentMarketPrice

                            currencyCode = shareAccountDetails.currency?.code ?: currencyCode
                            currencyDisplaySymbol = shareAccountDetails.currency?.displaySymbol
                                ?: currencyDisplaySymbol
                            decimalPlaces = shareAccountDetails.currency?.decimalPlaces ?: decimalPlaces
                        } catch (e: Exception) {
                            // do nothing
                        }

                        availableAccounts.add(
                            LinkableAccount(
                                accountId = share.id ?: 0L,
                                productName = share.productName,
                                accountNumber = share.accountNo,
                                accountType = AccountType.SHARE,
                                balance = balance,
                                currencyCode = currencyCode,
                                currencyDisplaySymbol = currencyDisplaySymbol,
                                decimalPlaces = decimalPlaces,
                                status = share.status?.toAccountStatus(),
                            ),
                        )
                    }
                }
                emit(availableAccounts)
            }.asDataStateFlow(),
        ).flowOn(ioDispatcher)
    }
}
