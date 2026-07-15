/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.mifospay.core.common.DataState
import org.mifospay.core.common.asDataStateFlow
import org.mifospay.core.data.mapper.pocket.toAccountStatus
import org.mifospay.core.data.mapper.pocket.toDomainList
import org.mifospay.core.data.util.NetworkMonitor
import org.mifospay.core.data.util.runAsDataState
import org.mifospay.core.data.util.withNetworkCheck
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
    private val ioDispatcher: CoroutineDispatcher,
) : PocketRepository {

    private val detailedPocketCache = MutableStateFlow<DataState<List<DetailedPocketAccount>>?>(null)

    private var cachedClientId: Long? = null

    private suspend fun fetchBasicPocketsFromNetwork(): List<PocketAccount> {
        return dataManager.pocketApi.getPocketAccounts().toDomainList()
    }

    override suspend fun getPocketAccounts(): DataState<List<PocketAccount>> {
        return runAsDataState(networkMonitor, ioDispatcher) {
            fetchBasicPocketsFromNetwork()
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

        detailedPocketCache.value = runAsDataState(networkMonitor, ioDispatcher) {
            val basicPockets = fetchBasicPocketsFromNetwork()
            val clientAccounts = dataManager.clientsApi.getClientAccounts(clientId)

            basicPockets.map { pocket ->
                addAccountDetails(pocket, clientAccounts)
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
                )
            }
            AccountType.SHARE -> {
                var balance = 0.0
                var productName: String?
                var currencyCode: String?
                var decimalPlaces: Int?
                var accountStatus: AccountStatus?

                try {
                    val shareAccountDetails = dataManager.shareAccountApi
                        .getShareAccountDetails(pocket.accountId).first()
                    productName = shareAccountDetails.productName
                    currencyCode = shareAccountDetails.currency?.code
                    decimalPlaces = shareAccountDetails.currency?.decimalPlaces
                    accountStatus = shareAccountDetails.status?.toAccountStatus()

                    val approvedShares = shareAccountDetails.summary?.totalApprovedShares ?: 0
                    val currentMarketPrice = shareAccountDetails.currentMarketPrice ?: 0.0
                    balance = approvedShares * currentMarketPrice
                } catch (e: Exception) {
                    val detail = clientAccounts.shareAccounts.find { it.id == pocket.accountId }
                    productName = detail?.productName
                    currencyCode = detail?.currency?.code
                    decimalPlaces = detail?.currency?.decimalPlaces
                    accountStatus = detail?.status?.toAccountStatus()
                }

                DetailedPocketAccount(
                    pocket = pocket,
                    balance = balance,
                    productName = productName,
                    currencyCode = currencyCode,
                    decimalPlaces = decimalPlaces,
                    status = accountStatus,
                )
            }
        }
    }

    override fun getDetailedPocketAccounts(
        clientId: Long,
        forceRefresh: Boolean,
    ): Flow<DataState<List<DetailedPocketAccount>>> {
        return networkMonitor.withNetworkCheck(
            flow {
                syncPockets(clientId, forceRefresh)

                detailedPocketCache.collect { state ->
                    if (state != null) emit(state)
                }
            },
        ).flowOn(ioDispatcher)
    }

    override suspend fun linkAccounts(
        payload: PocketLinkPayload,
        explicitlyAddedAccounts: List<DetailedPocketAccount>,
        clientId: Long,
    ): DataState<Unit> {
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
            val updatedBasicPockets = fetchBasicPocketsFromNetwork()

            val currentState = detailedPocketCache.value
            if (currentState is DataState.Success) {
                val updatedList = currentState.data.toMutableList()
                explicitlyAddedAccounts.forEach { explicitlyAddedAccount ->
                    val newlyGeneratedPocket = updatedBasicPockets.find {
                        it.accountId == explicitlyAddedAccount.pocket.accountId
                    }
                    if (newlyGeneratedPocket != null) {
                        val finalAccount = explicitlyAddedAccount.copy(pocket = newlyGeneratedPocket)
                        updatedList.add(finalAccount)
                    }
                }
                detailedPocketCache.value = DataState.Success(updatedList)
            } else {
                syncPockets(clientId = clientId, forceRefresh = true)
            }
        }
    }

    override suspend fun delinkAccounts(pocketAccountMappingIds: List<Long>, clientId: Long): DataState<Unit> {
        return runAsDataState(networkMonitor, ioDispatcher) {
            val serverIds = pocketAccountMappingIds.filter { it > 0 }
            if (serverIds.isNotEmpty()) {
                val request = PocketDelinkRequest(serverIds)
                dataManager.pocketApi.delinkAccounts(request = request)
            }

            val currentState = detailedPocketCache.value
            if (currentState is DataState.Success) {
                val updatedList = currentState.data.filter { it.pocket.id !in pocketAccountMappingIds }
                detailedPocketCache.value = DataState.Success(updatedList)
            } else {
                syncPockets(clientId = clientId, forceRefresh = true)
            }
        }
    }

    override fun getAvailableAccountsToLink(clientId: Long): Flow<DataState<List<LinkableAccount>>> {
        return networkMonitor.withNetworkCheck(
            flow {
                val clientAccounts = dataManager.clientsApi.getClientAccounts(clientId)
                val availableAccounts = mutableListOf<LinkableAccount>()

                val alreadyLinkedAccountIds = (detailedPocketCache.value as? DataState.Success)
                    ?.data?.map { it.pocket.accountId } ?: emptyList()

                clientAccounts.loanAccounts.forEach { loan ->
                    if (loan.id !in alreadyLinkedAccountIds) {
                        availableAccounts.add(
                            LinkableAccount(
                                accountId = loan.id,
                                productName = loan.productName,
                                accountNumber = loan.accountNo,
                                accountType = AccountType.LOAN,
                                balance = loan.loanBalance,
                                currencyCode = loan.currency?.code,
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
                        var decimalPlaces: Int? = share.currency?.decimalPlaces

                        try {
                            val shareAccountDetails = dataManager.shareAccountApi
                                .getShareAccountDetails(share.id).first()
                            val approvedShares = shareAccountDetails.summary?.totalApprovedShares ?: 0
                            val currentMarketPrice = shareAccountDetails.currentMarketPrice ?: 0.0
                            balance = approvedShares * currentMarketPrice

                            currencyCode = shareAccountDetails.currency?.code ?: currencyCode
                            decimalPlaces = shareAccountDetails.currency?.decimalPlaces ?: decimalPlaces
                        } catch (e: Exception) {
                            // do nothing
                        }

                        availableAccounts.add(
                            LinkableAccount(
                                accountId = share.id,
                                productName = share.productName,
                                accountNumber = share.accountNo,
                                accountType = AccountType.SHARE,
                                balance = balance,
                                currencyCode = currencyCode,
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
