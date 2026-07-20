/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.pocket.viewmodels

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mobile_wallet.feature.pocket.generated.resources.Res
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_error_load_accounts
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_unknown_account
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_unknown_status
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.mifospay.core.common.CurrencyFormatter
import org.mifospay.core.common.DataState
import org.mifospay.core.data.repository.PocketRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.enums.AccountType
import org.mifospay.core.model.pocket.AccountStatus
import org.mifospay.core.model.pocket.DetailedPocketAccount
import org.mifospay.core.ui.utils.BaseViewModel

@OptIn(ExperimentalCoroutinesApi::class)
internal class PocketDashboardViewModel(
    private val pocketRepository: PocketRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : BaseViewModel<PocketDashboardState, PocketDashboardEvent, PocketDashboardAction>(
    initialState = PocketDashboardState(
        clientId = requireNotNull(userPreferencesRepository.clientId.value),
    ),
) {
    private val refreshTrigger = MutableSharedFlow<Boolean>(extraBufferCapacity = 1)

    init {
        viewModelScope.launch {
            refreshTrigger.onStart { emit(false) }
                .flatMapLatest { forceRefresh ->
                    pocketRepository.getDetailedPocketAccounts(state.clientId, forceRefresh)
                }
                .collectLatest { dataState ->
                    handleDataState(dataState)
                }
        }
    }

    private fun updateState(update: (PocketDashboardState) -> PocketDashboardState) {
        mutableStateFlow.update(update)
    }

    override fun handleAction(action: PocketDashboardAction) {
        when (action) {
            PocketDashboardAction.NavigateBack -> sendEvent(PocketDashboardEvent.NavigateBack)
            PocketDashboardAction.ManagePocket -> sendEvent(PocketDashboardEvent.ManagePocket)
            PocketDashboardAction.LinkFirstAccount -> sendEvent(PocketDashboardEvent.ManagePocket)
            PocketDashboardAction.Retry -> retry()
            is PocketDashboardAction.NavigateToLoanDetail -> {
                sendEvent(PocketDashboardEvent.NavigateToLoanDetail(action.accountId))
            }
            is PocketDashboardAction.NavigateToSavingsDetail -> {
                sendEvent(PocketDashboardEvent.NavigateToSavingsDetail(action.accountId))
            }
            is PocketDashboardAction.NavigateToShareDetail -> {
                sendEvent(PocketDashboardEvent.NavigateToShareDetail(action.accountId))
            }
            PocketDashboardAction.Refresh -> refresh()
        }
    }

    private fun refresh() {
        updateState { it.copy(isRefreshing = true) }
        refreshTrigger.tryEmit(true)
    }

    private fun retry() {
        updateState { it.copy(uiState = PocketDashboardUiState.Loading) }
        refreshTrigger.tryEmit(true)
    }

    private suspend fun handleDataState(dataState: DataState<List<DetailedPocketAccount>>) {
        when (dataState) {
            is DataState.Loading -> {
                if (!state.isRefreshing) {
                    updateState { it.copy(uiState = PocketDashboardUiState.Loading) }
                }
            }

            is DataState.Error -> {
                updateState {
                    it.copy(
                        uiState = PocketDashboardUiState.Error(Res.string.feature_pocket_error_load_accounts),
                        isRefreshing = false,
                    )
                }
            }

            is DataState.Success -> {
                val detailedAccounts = dataState.data

                suspend fun mapToUiModel(detailed: DetailedPocketAccount): DetailedPocket {
                    val balanceStr = if (detailed.status == AccountStatus.ACTIVE) {
                        if (detailed.balance != null) {
                            CurrencyFormatter.format(
                                detailed.balance,
                                detailed.currencyCode,
                                detailed.decimalPlaces,
                            )
                        } else {
                            ""
                        }
                    } else {
                        detailed.status?.name ?: getString(Res.string.feature_pocket_unknown_status)
                    }

                    return DetailedPocket(
                        accountId = detailed.pocket.accountId,
                        name = detailed.productName ?: getString(Res.string.feature_pocket_unknown_account),
                        accountNumber = detailed.pocket.accountNumber,
                        balanceOrStatus = balanceStr,
                        status = detailed.status ?: AccountStatus.UNKNOWN,
                    )
                }

                val loanList = mutableListOf<DetailedPocket>()
                val savingsList = mutableListOf<DetailedPocket>()
                val shareList = mutableListOf<DetailedPocket>()
                for (account in detailedAccounts) {
                    when (account.pocket.accountType) {
                        AccountType.LOAN -> loanList.add(mapToUiModel(account))
                        AccountType.SAVINGS -> savingsList.add(mapToUiModel(account))
                        AccountType.SHARE -> shareList.add(mapToUiModel(account))
                    }
                }

                val totalSum = detailedAccounts
                    .filter { it.status == AccountStatus.ACTIVE && it.balance != null }
                    .sumOf { it.balance ?: 0.0 }

                val sampleAccount = detailedAccounts.firstOrNull { it.currencyCode != null }
                val formattedTotal = CurrencyFormatter.format(
                    totalSum,
                    sampleAccount?.currencyCode,
                    sampleAccount?.decimalPlaces,
                )

                if (loanList.isEmpty() && shareList.isEmpty() && savingsList.isEmpty()) {
                    updateState {
                        it.copy(
                            uiState = PocketDashboardUiState.Empty,
                            isRefreshing = false,
                        )
                    }
                } else {
                    updateState {
                        it.copy(
                            uiState = PocketDashboardUiState.Success,
                            totalBalance = formattedTotal,
                            loanAccounts = loanList,
                            savingsAccounts = savingsList,
                            shareAccounts = shareList,
                            isRefreshing = false,
                        )
                    }
                }
            }
        }
    }
}
data class PocketDashboardState(
    val clientId: Long = 0,
    val totalBalance: String = "$ 0",
    val loanAccounts: List<DetailedPocket> = emptyList(),
    val savingsAccounts: List<DetailedPocket> = emptyList(),
    val shareAccounts: List<DetailedPocket> = emptyList(),
    val uiState: PocketDashboardUiState = PocketDashboardUiState.Loading,
    val isRefreshing: Boolean = false,
)

sealed interface PocketDashboardUiState {
    data object Loading : PocketDashboardUiState
    data class Error(val message: StringResource) : PocketDashboardUiState
    data object Empty : PocketDashboardUiState
    data object Success : PocketDashboardUiState
}

data class DetailedPocket(
    val accountId: Long,
    val name: String,
    val accountNumber: String,
    val balanceOrStatus: String,
    val status: AccountStatus,
)

internal sealed interface PocketDashboardEvent {
    data object NavigateBack : PocketDashboardEvent
    data object ManagePocket : PocketDashboardEvent
    data class NavigateToLoanDetail(val accountId: Long) : PocketDashboardEvent
    data class NavigateToSavingsDetail(val accountId: Long) : PocketDashboardEvent
    data class NavigateToShareDetail(val accountId: Long) : PocketDashboardEvent
}

internal sealed interface PocketDashboardAction {
    data object NavigateBack : PocketDashboardAction
    data class NavigateToLoanDetail(val accountId: Long) : PocketDashboardAction
    data class NavigateToSavingsDetail(val accountId: Long) : PocketDashboardAction
    data class NavigateToShareDetail(val accountId: Long) : PocketDashboardAction
    data object ManagePocket : PocketDashboardAction
    data object LinkFirstAccount : PocketDashboardAction
    data object Refresh : PocketDashboardAction
    data object Retry : PocketDashboardAction
}
