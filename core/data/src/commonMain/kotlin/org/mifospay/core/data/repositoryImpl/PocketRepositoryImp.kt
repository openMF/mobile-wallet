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

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.mifospay.core.common.DataState
import org.mifospay.core.data.mapper.pocket.toAccountStatus
import org.mifospay.core.data.mapper.pocket.toDomainList
import org.mifospay.core.data.repository.PocketRepository
import org.mifospay.core.data.util.NetworkMonitor
import org.mifospay.core.datastore.PocketPreferencesDataSource
import org.mifospay.core.datastore.model.toDomain
import org.mifospay.core.datastore.model.toEntity
import org.mifospay.core.model.enums.AccountType
import org.mifospay.core.model.payload.PocketLinkPayload
import org.mifospay.core.model.pocket.AccountStatus
import org.mifospay.core.model.pocket.DetailedPocketAccount
import org.mifospay.core.model.pocket.LinkableAccount
import org.mifospay.core.model.pocket.PocketAccount
import org.mifospay.core.network.SelfServiceApiManager
import org.mifospay.core.network.model.entity.client.ClientAccountsEntity
import org.mifospay.core.network.model.entity.pocket.PocketDelinkRequest
import org.mifospay.core.network.model.entity.pocket.PocketLinkRequest

class PocketRepositoryImp(
    private val dataManager: SelfServiceApiManager,
    private val networkMonitor: NetworkMonitor,
    private val pocketPreferencesDataSource: PocketPreferencesDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : PocketRepository {

    private val detailedPocketCache = MutableStateFlow<DataState<List<DetailedPocketAccount>>?>(null)
    private var cachedClientId: Long? = null

    private suspend fun syncPocketsWithServer() {
        if (!networkMonitor.isOnline.first()) return

        try {
            val serverBasicPockets = dataManager.pocketApi.getPocketAccounts().toDomainList()
            var localBasicPockets = pocketPreferencesDataSource.getPocketAccountsSync().map { it.toDomain() }

            if (!pocketPreferencesDataSource.hasSyncedPockets) {
                localBasicPockets = serverBasicPockets
                pocketPreferencesDataSource.updatePocketAccounts(localBasicPockets.map { it.toEntity() })
                pocketPreferencesDataSource.hasSyncedPockets = true
            } else {
                val accountsToLink = localBasicPockets.filter { local ->
                    serverBasicPockets.none { it.accountId == local.accountId && it.accountType == local.accountType }
                }

                val accountsToDelink = serverBasicPockets.filter { server ->
                    localBasicPockets.none { it.accountId == server.accountId && it.accountType == server.accountType }
                }

                if (accountsToDelink.isNotEmpty()) {
                    val delinkIds = accountsToDelink.map { it.id }.filter { it > 0 }
                    if (delinkIds.isNotEmpty()) {
                        try {
                            dataManager.pocketApi.delinkAccounts(request = PocketDelinkRequest(delinkIds))
                        } catch (e: Exception) {
                            // do nothing
                        }
                    }
                }

                if (accountsToLink.isNotEmpty()) {
                    val linkRequest = PocketLinkRequest(
                        accountsDetail = accountsToLink.map {
                            PocketLinkRequest.AccountDetail(accountId = it.accountId.toString(), accountType = it.accountType.name)
                        },
                    )
                    try {
                        dataManager.pocketApi.linkAccounts(request = linkRequest)
                    } catch (e: Exception) {
                        // do nothing
                    }
                }

                val updatedServerPockets = try {
                    if (accountsToLink.isNotEmpty() || accountsToDelink.isNotEmpty()) {
                        dataManager.pocketApi.getPocketAccounts().toDomainList()
                    } else {
                        serverBasicPockets
                    }
                } catch (e: Exception) {
                    serverBasicPockets
                }

                localBasicPockets = localBasicPockets.map { local ->
                    val matchedServer = updatedServerPockets.find { it.accountId == local.accountId && it.accountType == local.accountType }
                    if (matchedServer != null) {
                        local.copy(id = matchedServer.id, pocketId = matchedServer.pocketId)
                    } else {
                        local
                    }
                }
                pocketPreferencesDataSource.updatePocketAccounts(localBasicPockets.map { it.toEntity() })
            }
        } catch (e: Exception) {
            // do nothing
        }
    }

    override suspend fun getPocketAccounts(): DataState<List<PocketAccount>> {
        return try {
            syncPocketsWithServer()
            val localPockets = pocketPreferencesDataSource.getPocketAccountsSync().map { it.toDomain() }
            DataState.Success(localPockets)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    private suspend fun syncPockets(clientId: Long, forceRefresh: Boolean = false) {
        if (cachedClientId != clientId) {
            detailedPocketCache.value = null
            cachedClientId = clientId
        }

        if (!forceRefresh && cachedClientId == clientId && detailedPocketCache.value is DataState.Loading) return
        if (!forceRefresh && cachedClientId == clientId && detailedPocketCache.value is DataState.Success) return

        detailedPocketCache.value = DataState.Loading

        try {
            syncPocketsWithServer()
            val localBasicPockets = pocketPreferencesDataSource.getPocketAccountsSync().map { it.toDomain() }

            if (networkMonitor.isOnline.first()) {
                val clientAccounts = dataManager.clientsApi.getClientAccounts(clientId)

                val detailed = localBasicPockets.map { pocket ->
                    addAccountDetails(pocket, clientAccounts)
                }

                pocketPreferencesDataSource.updateDetailedPocketAccounts(detailed.map { it.toEntity() })
                detailedPocketCache.value = DataState.Success(detailed)
            } else {
                val localDetailed = pocketPreferencesDataSource.getDetailedPocketAccountsSync().map { it.toDomain() }
                detailedPocketCache.value = DataState.Success(localDetailed)
            }
        } catch (e: Exception) {
            val localDetailed = pocketPreferencesDataSource.getDetailedPocketAccountsSync().map { it.toDomain() }
            if (localDetailed.isNotEmpty()) {
                detailedPocketCache.value = DataState.Success(localDetailed)
            } else {
                detailedPocketCache.value = DataState.Error(e)
            }
        }
    }

    private suspend fun addAccountDetails(
        pocket: PocketAccount,
        clientAccounts: ClientAccountsEntity,
    ): DetailedPocketAccount {
        return when (pocket.accountType) {
            AccountType.LOAN -> {
                val detail = clientAccounts.loanAccounts.find { it.id == pocket.accountId }
                DetailedPocketAccount(
                    pocket = pocket,
                    balance = detail?.loanBalance,
                    productName = detail?.productName,
                    currencyCode = detail?.currency?.code,
                    decimalPlaces = detail?.currency?.decimalPlaces,
                    status = detail?.status?.toAccountStatus(),
                    currencyDisplaySymbol = detail?.currency?.displaySymbol,
                )
            }
            AccountType.SAVINGS -> {
                val detail = clientAccounts.savingsAccounts.find { it.id == pocket.accountId }
                DetailedPocketAccount(
                    pocket = pocket,
                    balance = detail?.accountBalance,
                    productName = detail?.productName,
                    currencyCode = detail?.currency?.code,
                    decimalPlaces = detail?.currency?.decimalPlaces,
                    status = detail?.status?.toAccountStatus(),
                    currencyDisplaySymbol = detail?.currency?.displaySymbol,
                )
            }
            AccountType.SHARE -> {
                var balance = 0.0
                var productName: String?
                var currencyCode: String?
                var decimalPlaces: Int?
                var accountStatus: AccountStatus?
                var currencyDisplaySymbol: String?

                try {
                    val shareAccountDetails = dataManager.shareAccountApi
                        .getShareAccountDetails(pocket.accountId).first()
                    productName = shareAccountDetails.productName
                    currencyCode = shareAccountDetails.currency?.code
                    decimalPlaces = shareAccountDetails.currency?.decimalPlaces
                    accountStatus = shareAccountDetails.status?.toAccountStatus()
                    currencyDisplaySymbol = shareAccountDetails.currency?.displaySymbol

                    val approvedShares = shareAccountDetails.summary?.totalApprovedShares ?: 0
                    val currentMarketPrice = shareAccountDetails.currentMarketPrice ?: 0.0
                    balance = approvedShares * currentMarketPrice
                } catch (e: Exception) {
                    val detail = clientAccounts.shareAccounts.find { it.id == pocket.accountId }
                    productName = detail?.productName
                    currencyCode = detail?.currency?.code
                    decimalPlaces = detail?.currency?.decimalPlaces
                    accountStatus = detail?.status?.toAccountStatus()
                    currencyDisplaySymbol = detail?.currency?.displaySymbol
                }

                DetailedPocketAccount(
                    pocket = pocket,
                    balance = balance,
                    productName = productName,
                    currencyCode = currencyCode,
                    decimalPlaces = decimalPlaces,
                    status = accountStatus,
                    currencyDisplaySymbol = currencyDisplaySymbol,
                )
            }
        }
    }

    override fun getDetailedPocketAccounts(
        clientId: Long,
        forceRefresh: Boolean,
    ): Flow<DataState<List<DetailedPocketAccount>>> {
        return flow {
            syncPockets(clientId, forceRefresh)
            detailedPocketCache.collect { state ->
                if (state != null) emit(state)
            }
        }.flowOn(ioDispatcher)
    }

    override suspend fun linkAccounts(
        payload: PocketLinkPayload,
        explicitlyAddedAccounts: List<DetailedPocketAccount>,
        clientId: Long,
    ): DataState<Unit> {
        return try {
            pocketPreferencesDataSource.hasSyncedPockets = true

            val localDetailed = pocketPreferencesDataSource.getDetailedPocketAccountsSync().map { it.toDomain() }.toMutableList()
            explicitlyAddedAccounts.forEach { added ->
                localDetailed.removeAll { it.pocket.accountId == added.pocket.accountId && it.pocket.accountType == added.pocket.accountType }
                localDetailed.add(added)
            }
            pocketPreferencesDataSource.updateDetailedPocketAccounts(localDetailed.map { it.toEntity() })
            detailedPocketCache.value = DataState.Success(localDetailed)

            val localBasic = pocketPreferencesDataSource.getPocketAccountsSync().map { it.toDomain() }.toMutableList()
            explicitlyAddedAccounts.forEach { added ->
                localBasic.removeAll { it.accountId == added.pocket.accountId && it.accountType == added.pocket.accountType }
                localBasic.add(added.pocket)
            }
            pocketPreferencesDataSource.updatePocketAccounts(localBasic.map { it.toEntity() })

            if (networkMonitor.isOnline.first()) {
                try {
                    val request = PocketLinkRequest(
                        accountsDetail = payload.accountsDetail.map {
                            PocketLinkRequest.AccountDetail(
                                accountId = it.accountId.toString(),
                                accountType = it.accountType.name,
                            )
                        },
                    )
                    dataManager.pocketApi.linkAccounts(request = request)
                } catch (e: Exception) {
                    // do nothing
                }
            }

            DataState.Success(Unit)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override suspend fun delinkAccounts(pocketAccountMappingIds: List<Long>, clientId: Long): DataState<Unit> {
        return try {
            pocketPreferencesDataSource.hasSyncedPockets = true

            val localDetailed = pocketPreferencesDataSource.getDetailedPocketAccountsSync().map { it.toDomain() }.toMutableList()
            localDetailed.removeAll { it.pocket.id in pocketAccountMappingIds }
            pocketPreferencesDataSource.updateDetailedPocketAccounts(localDetailed.map { it.toEntity() })
            detailedPocketCache.value = DataState.Success(localDetailed)

            val localBasic = pocketPreferencesDataSource.getPocketAccountsSync().map { it.toDomain() }.toMutableList()
            localBasic.removeAll { it.id in pocketAccountMappingIds }
            pocketPreferencesDataSource.updatePocketAccounts(localBasic.map { it.toEntity() })

            val serverIds = pocketAccountMappingIds.filter { it > 0 }
            if (networkMonitor.isOnline.first() && serverIds.isNotEmpty()) {
                try {
                    val request = PocketDelinkRequest(serverIds)
                    dataManager.pocketApi.delinkAccounts(request = request)
                } catch (e: Exception) {
                    // do nothing
                }
            }

            DataState.Success(Unit)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }

    override fun getAvailableAccountsToLink(clientId: Long): Flow<DataState<List<LinkableAccount>>> {
        return flow {
            try {
                if (!networkMonitor.isOnline.first()) {
                    val local = pocketPreferencesDataSource.getLinkableAccountsSync().map { it.toDomain() }
                    emit(DataState.Success(local))
                    return@flow
                }

                val clientAccounts = dataManager.clientsApi.getClientAccounts(clientId)
                val availableAccounts = mutableListOf<LinkableAccount>()

                val alreadyLinkedAccounts = (detailedPocketCache.value as? DataState.Success)
                    ?.data?.map { Pair(it.pocket.accountId, it.pocket.accountType) } ?: emptyList()

                clientAccounts.loanAccounts.forEach { loan ->
                    if (Pair(loan.id ?: 0L, AccountType.LOAN) !in alreadyLinkedAccounts) {
                        availableAccounts.add(
                            LinkableAccount(
                                accountId = loan.id ?: 0L,
                                productName = loan.productName,
                                accountNumber = loan.accountNo,
                                accountType = AccountType.LOAN,
                                balance = loan.loanBalance,
                                currencyCode = loan.currency?.code,
                                currencyDisplaySymbol = loan.currency?.displaySymbol,
                                decimalPlaces = loan.currency?.decimalPlaces,
                                status = loan.status?.toAccountStatus(),
                            ),
                        )
                    }
                }

                clientAccounts.savingsAccounts.forEach { savings ->
                    if (Pair(savings.id, AccountType.SAVINGS) !in alreadyLinkedAccounts) {
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
                    if (Pair(share.id ?: 0L, AccountType.SHARE) !in alreadyLinkedAccounts) {
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
                            currencyDisplaySymbol = shareAccountDetails.currency?.displaySymbol ?: currencyDisplaySymbol
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

                if (availableAccounts.isEmpty()) {
                    val local = pocketPreferencesDataSource.getLinkableAccountsSync().map { it.toDomain() }
                    emit(DataState.Success(local))
                } else {
                    pocketPreferencesDataSource.updateLinkableAccounts(availableAccounts.map { it.toEntity() })
                    emit(DataState.Success(availableAccounts))
                }
            } catch (e: Exception) {
                val local = pocketPreferencesDataSource.getLinkableAccountsSync().map { it.toDomain() }
                if (local.isNotEmpty()) {
                    emit(DataState.Success(local))
                } else {
                    emit(DataState.Error(e))
                }
            }
        }.flowOn(ioDispatcher)
    }
}
