/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifospay.feature.pocket.viewmodels

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kpt.core.base.store.freshness.FreshnessSignal
import kpt.core.base.store.screen.ScreenState
import org.mifospay.core.common.CurrencyFormatter
import org.mifospay.core.data.repository.PocketRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.enums.AccountType
import org.mifospay.core.model.pocket.AccountStatus
import org.mifospay.core.model.pocket.DetailedPocketAccount
import org.mifospay.core.ui.utils.BaseViewModel

internal class PocketDashboardViewModel(
    private val pocketRepository: PocketRepository,
    userPreferencesRepository: UserPreferencesRepository,
) : BaseViewModel<PocketDashboardState, PocketDashboardEvent, PocketDashboardAction>(
    initialState = PocketDashboardState(
        clientId = requireNotNull(userPreferencesRepository.clientId.value),
    ),
) {
    private val stream = pocketRepository.getDetailedPocketAccountsStream(
        clientId = state.clientId,
        scope = viewModelScope,
    )

    val uiState: StateFlow<ScreenState<PocketBuckets>> = stream.state.map { state ->
        when (state) {
            is ScreenState.Content -> {
                ScreenState.Content(
                    data = state.data.toPocketBuckets(unknownStatus = "Unknown", unknownAccount = "Unknown"),
                    fetchedAt = state.fetchedAt,
                    freshnessSignal = state.freshnessSignal,
                )
            }
            is ScreenState.Error -> state
            ScreenState.Loading -> ScreenState.Loading
            ScreenState.Empty -> ScreenState.Empty
            is ScreenState.NoNetwork -> state
            ScreenState.Unauthenticated -> ScreenState.Unauthenticated
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ScreenState.Loading,
    )

    val freshness: StateFlow<FreshnessSignal> = stream.freshness.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FreshnessSignal.initial(),
    )

    fun retry() = stream.refresh()

    override fun handleAction(action: PocketDashboardAction) {
        when (action) {
            PocketDashboardAction.NavigateBack -> sendEvent(PocketDashboardEvent.NavigateBack)
            PocketDashboardAction.ManagePocket -> sendEvent(PocketDashboardEvent.ManagePocket)
            PocketDashboardAction.LinkFirstAccount -> sendEvent(PocketDashboardEvent.ManagePocket)
            PocketDashboardAction.Retry -> retry()
            PocketDashboardAction.Refresh -> retry()
            is PocketDashboardAction.NavigateToLoanDetail -> {
                sendEvent(PocketDashboardEvent.NavigateToLoanDetail(action.accountId))
            }
            is PocketDashboardAction.NavigateToSavingsDetail -> {
                sendEvent(PocketDashboardEvent.NavigateToSavingsDetail(action.accountId))
            }
            is PocketDashboardAction.NavigateToShareDetail -> {
                sendEvent(PocketDashboardEvent.NavigateToShareDetail(action.accountId))
            }
        }
    }
}

data class PocketDashboardState(
    val clientId: Long = 0,
)

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

internal data class PocketBuckets(
    val totalBalance: String,
    val savingsAccounts: List<DetailedPocket>,
    val loanAccounts: List<DetailedPocket>,
    val shareAccounts: List<DetailedPocket>,
)

private fun formatBalance(
    balance: Double,
    currencyCode: String?,
    displaySymbol: String?,
    decimalPlaces: Int?,
): String {
    val code = currencyCode.orEmpty()
    val symbol = displaySymbol.orEmpty()
    val formattedNum = CurrencyFormatter.format(balance, decimalPlaces)
    return if (code.isNotEmpty()) "$code $symbol$formattedNum" else "$symbol$formattedNum"
}

private fun DetailedPocketAccount.toUiModel(
    unknownStatus: String,
    unknownAccount: String,
): DetailedPocket {
    val balanceStr = if (status == AccountStatus.ACTIVE) {
        balance?.let { formatBalance(it, currencyCode, currencyDisplaySymbol, decimalPlaces) } ?: ""
    } else {
        status?.name ?: unknownStatus
    }

    return DetailedPocket(
        accountId = pocket.accountId,
        name = productName ?: unknownAccount,
        accountNumber = pocket.accountNumber,
        balanceOrStatus = balanceStr,
        status = status ?: AccountStatus.UNKNOWN,
    )
}

private fun List<DetailedPocketAccount>.toPocketBuckets(
    unknownStatus: String,
    unknownAccount: String,
): PocketBuckets {
    val loanList = mutableListOf<DetailedPocket>()
    val savingsList = mutableListOf<DetailedPocket>()
    val shareList = mutableListOf<DetailedPocket>()

    for (account in this) {
        val uiModel = account.toUiModel(unknownStatus, unknownAccount)
        when (account.pocket.accountType) {
            AccountType.LOAN -> loanList.add(uiModel)
            AccountType.SAVINGS -> savingsList.add(uiModel)
            AccountType.SHARE -> shareList.add(uiModel)
        }
    }

    val balancesByCurrency = this
        .filter { it.status == AccountStatus.ACTIVE && it.balance != null && it.currencyCode != null }
        .groupBy { it.currencyCode!! }
        .map { (currencyCode, accounts) ->
            val sum = accounts.sumOf { it.balance ?: 0.0 }
            val first = accounts.first()
            formatBalance(sum, currencyCode, first.currencyDisplaySymbol, first.decimalPlaces)
        }

    val formattedTotal = if (balancesByCurrency.isNotEmpty()) {
        balancesByCurrency.joinToString("\n")
    } else {
        "0.00"
    }

    return PocketBuckets(
        totalBalance = formattedTotal,
        savingsAccounts = savingsList,
        loanAccounts = loanList,
        shareAccounts = shareList,
    )
}
