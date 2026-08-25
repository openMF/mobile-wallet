/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifospay.core.data.repositoryImpl

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kpt.core.base.store.infra.FetchedAtRepository
import kpt.core.base.store.screen.FetchPolicy
import kpt.core.base.store.screen.ScreenDataStream
import kpt.core.base.store.screen.asScreenStream
import kpt.core.database.wallet.pocket.PocketDao
import kpt.core.database.wallet.pocket.toPendingLinkEntity
import kpt.core.store.AppStoreRegistry
import kpt.core.store.wallet.linkableaccount.LinkableAccountKey
import kpt.core.store.wallet.pocket.PocketKey
import org.mifospay.core.data.repository.PocketRepository
import org.mifospay.core.data.util.NetworkMonitor
import org.mifospay.core.model.pocket.DetailedPocketAccount
import org.mifospay.core.model.pocket.LinkableAccount
import org.mifospay.core.network.SelfServiceApiManager
import org.mifospay.core.network.model.entity.pocket.PocketDelinkRequest
import org.mifospay.core.network.model.entity.pocket.PocketLinkRequest
import org.mobilenativefoundation.store.store5.Store
import kotlin.time.Clock
import kpt.core.data.infra.NetworkMonitor as StoreNetworkMonitor

class PocketRepositoryImp(
    private val dataManager: SelfServiceApiManager,
    private val networkMonitor: NetworkMonitor,
    private val ioDispatcher: CoroutineDispatcher,
    private val pocketStore: Store<PocketKey, List<DetailedPocketAccount>>? = null,
    private val linkableAccountsStore: Store<
        LinkableAccountKey,
        List<LinkableAccount>,
        >? = null,
    private val storeNetworkMonitor: StoreNetworkMonitor? = null,
    private val fetchedAtRepository: FetchedAtRepository? = null,
    private val pocketDao: PocketDao? = null,
) : PocketRepository {

    override fun getDetailedPocketAccountsStream(
        clientId: Long,
        scope: CoroutineScope,
    ): ScreenDataStream<List<DetailedPocketAccount>> {
        val store = checkNotNull(pocketStore) { "Requires pocketStore." }
        val netMon = checkNotNull(storeNetworkMonitor) { "Requires kmptoolkit NetworkMonitor." }
        val fetchedAtRepo = checkNotNull(fetchedAtRepository) { "Requires FetchedAtRepository." }
        return store.asScreenStream(
            key = PocketKey(clientId),
            networkMonitor = netMon,
            fetchedAtRepository = fetchedAtRepo,
            cacheKey = "wallet_pockets_detailed-$clientId",
            scope = scope,
            isEmpty = { it.isEmpty() },
            fetchPolicy = FetchPolicy.NETWORK_ONLY,
            ttl = AppStoreRegistry.Ttl.POCKET,
        )
    }

    override fun getLinkedPocketAccountsStream(
        clientId: Long,
        scope: CoroutineScope,
    ): ScreenDataStream<List<DetailedPocketAccount>> {
        val store = checkNotNull(pocketStore) { "Requires pocketStore." }
        val netMon = checkNotNull(storeNetworkMonitor) { "Requires kmptoolkit NetworkMonitor." }
        val fetchedAtRepo = checkNotNull(fetchedAtRepository) { "Requires FetchedAtRepository." }
        return store.asScreenStream(
            key = PocketKey(clientId),
            networkMonitor = netMon,
            fetchedAtRepository = fetchedAtRepo,
            cacheKey = "wallet_pockets_linked-$clientId",
            scope = scope,
            isEmpty = { it.isEmpty() },
            fetchPolicy = FetchPolicy.CACHE_FIRST_SWR,
            ttl = AppStoreRegistry.Ttl.POCKET,
        )
    }

    override suspend fun linkAccounts(
        explicitlyAddedAccounts: List<DetailedPocketAccount>,
        clientId: Long,
    ) {
        withContext(ioDispatcher) {
            val dao = pocketDao ?: return@withContext
            val stamp = Clock.System.now().toEpochMilliseconds()

            try {
                // Try to push to network directly to avoid overhead of waiting for the Fetcher
                val request = PocketLinkRequest(
                    accountsDetail = explicitlyAddedAccounts.map {
                        PocketLinkRequest.AccountDetail(
                            accountId = it.pocket.accountId.toString(),
                            accountType = it.pocket.accountType.name,
                        )
                    },
                )
                dataManager.pocketApi.linkAccounts(request = request)

                // If successful, save directly as SYNCED so the UI updates instantly
                explicitlyAddedAccounts.forEach {
                    dao.upsert(it.pocket.toPendingLinkEntity(clientId, stamp).copy(syncStatus = "SYNCED"))
                }
            } catch (e: Exception) {
                // If network fails (e.g. offline), fallback to PENDING_LINK
                explicitlyAddedAccounts.forEach {
                    dao.upsert(it.pocket.toPendingLinkEntity(clientId, stamp))
                }
                // Do NOT throw. Swallow the error so the UI loading dialog closes normally
                // and the user sees an immediate success state (optimistic UI), since the
                // action was successfully saved offline.
            }
        }
    }

    override suspend fun delinkAccounts(
        pocketAccountMappingIds: List<Long>,
        clientId: Long,
    ) {
        withContext(ioDispatcher) {
            val dao = pocketDao ?: return@withContext
            val currentPockets = dao.observeLinkedByClient(clientId).first()
            val accountsToDelink = currentPockets.filter { it.id in pocketAccountMappingIds }

            try {
                // Try to push to network directly
                val serverIds = accountsToDelink.filter { it.id > 0 }.map { it.id }
                if (serverIds.isNotEmpty()) {
                    val request = PocketDelinkRequest(serverIds)
                    dataManager.pocketApi.delinkAccounts(request = request)
                }

                // If successful, we can just delete them from Room (or mark them as SYNCED
                // if we want to keep history, but usually delink means delete)
                // The subsequent Fetcher run will wipe them anyway. For instant UI update, we delete.
                accountsToDelink.forEach {
                    dao.deleteById(it.id, clientId)
                }
            } catch (e: Exception) {
                // If network fails, mark as PENDING_DELINK
                accountsToDelink.forEach {
                    dao.upsert(it.copy(syncStatus = "PENDING_DELINK"))
                }
                // Do NOT throw. Swallow the error so the UI loading dialog closes normally
                // and the user sees an immediate success state (optimistic UI), since the
                // action was successfully saved offline.
            }
        }
    }

    override fun getAvailableAccountsToLinkStream(
        clientId: Long,
        scope: CoroutineScope,
    ): ScreenDataStream<List<LinkableAccount>> {
        val store = checkNotNull(linkableAccountsStore) { "Requires linkableAccountsStore." }
        val netMon = checkNotNull(storeNetworkMonitor) { "Requires kmptoolkit NetworkMonitor." }
        val fetchedAtRepo = checkNotNull(fetchedAtRepository) { "Requires FetchedAtRepository." }
        return store.asScreenStream(
            key = LinkableAccountKey(clientId),
            networkMonitor = netMon,
            fetchedAtRepository = fetchedAtRepo,
            cacheKey = "wallet_linkable_accounts-$clientId",
            scope = scope,
            isEmpty = { it.isEmpty() },
            fetchPolicy = FetchPolicy.CACHE_FIRST_SWR,
            ttl = AppStoreRegistry.Ttl.LINKABLE_ACCOUNTS,
        )
    }
}
