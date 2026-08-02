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
import org.mifospay.core.common.ScreenState
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
    // Phase-5 Batch-2: refresh trigger cadence. Each emission re-subscribes
    // to a fresh `getDetailedPocketAccountsScreen(...)` stream (parity with
    // `BeneficiaryListViewModel`), which fires a new fetch through the store.
    // Replaces the pre-store `Boolean` forceRefresh signal (the store's SWR
    // + explicit re-subscribe handle refresh semantics natively — there is
    // no `forceRefresh` on the store's read path).
    private val refreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    init {
        // Phase-5 Batch-2 LEDGER read (GOAL D13) — switched from the
        // transitional `getDetailedPocketAccounts(clientId, forceRefresh)`
        // (in-memory MutableStateFlow cache + DataState<T>) to the
        // store-native `getDetailedPocketAccountsScreen(...)` that consumes
        // the `pocket` Store5 read (`createStore` + Room SoT + CACHE_FIRST_SWR
        // + atomic replacePage). The in-memory `detailedPocketCache` is gone
        // — Room is the SoT. The pre-Batch-2 `TODO(phase-4)` at the top of
        // this file is REMOVED.
        //
        // Requires `viewModelScope` for the stream's internal reconnect +
        // periodic + SWR side-fetch coroutines.
        viewModelScope.launch {
            refreshTrigger.onStart { emit(Unit) }
                .flatMapLatest {
                    pocketRepository.getDetailedPocketAccountsScreen(
                        clientId = state.clientId,
                        scope = viewModelScope,
                    )
                }
                .collectLatest { screenState ->
                    handleScreenState(screenState)
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
        refreshTrigger.tryEmit(Unit)
    }

    private fun retry() {
        updateState { it.copy(uiState = PocketDashboardUiState.Loading) }
        refreshTrigger.tryEmit(Unit)
    }

    /**
     * Fold the 6-branch [ScreenState] back into the feature's 4-branch UiState
     * (Loading / Empty / Success / Error). The pre-Batch-2 `handleDataState`
     * (which folded a 3-branch DataState) is REMOVED — the same downstream
     * `Success` path drives `loanAccounts` / `savingsAccounts` / `shareAccounts`
     * bucket population + totals; only the entry-point sealed hierarchy changed.
     *
     * `ScreenState.NoNetwork` and `ScreenState.Unauthenticated` fold into the
     * Error branch until Phase-4 wires per-branch surfaces.
     */
    private suspend fun handleScreenState(screenState: ScreenState<List<DetailedPocketAccount>>) {
        when (screenState) {
            is ScreenState.Loading -> {
                if (!state.isRefreshing) {
                    updateState { it.copy(uiState = PocketDashboardUiState.Loading) }
                }
            }

            is ScreenState.Empty -> {
                updateState {
                    it.copy(
                        uiState = PocketDashboardUiState.Empty,
                        isRefreshing = false,
                    )
                }
            }

            is ScreenState.Content -> {
                populateFromContent(screenState.data)
            }

            is ScreenState.Error -> {
                updateState {
                    it.copy(
                        uiState = PocketDashboardUiState.Error(Res.string.feature_pocket_error_load_accounts),
                        isRefreshing = false,
                    )
                }
            }

            is ScreenState.NoNetwork -> {
                updateState {
                    it.copy(
                        uiState = PocketDashboardUiState.Error(Res.string.feature_pocket_error_load_accounts),
                        isRefreshing = false,
                    )
                }
            }

            is ScreenState.Unauthenticated -> {
                updateState {
                    it.copy(
                        uiState = PocketDashboardUiState.Error(Res.string.feature_pocket_error_load_accounts),
                        isRefreshing = false,
                    )
                }
            }
        }
    }

    /**
     * Bucket a non-empty page of [DetailedPocketAccount] into per-account-type
     * lists + a formatted total balance — the same fold the pre-Batch-2
     * `handleDataState(DataState.Success)` branch performed, extracted here so
     * `handleScreenState` doesn't grow a 100-line branch body.
     */
    private suspend fun populateFromContent(detailedAccounts: List<DetailedPocketAccount>) {
        val unknownStatus = getString(Res.string.feature_pocket_unknown_status)
        val unknownAccount = getString(Res.string.feature_pocket_unknown_account)

        // Upstream PR #2057 (manage-pocket) rewired per-account balance rendering
        // to prefix the currency `displaySymbol` in front of the numeric string
        // (`"$code $displaySymbol$formattedNum"`). We keep OUR Store5-native
        // ScreenState pipe unchanged and fold that display-side change in here
        // so the manage-pocket screen + dashboard render identical glyphs.
        fun mapToUiModel(detailed: DetailedPocketAccount): DetailedPocket {
            val balanceStr = if (detailed.status == AccountStatus.ACTIVE) {
                if (detailed.balance != null) {
                    val code = detailed.currencyCode.orEmpty()
                    val displaySymbol = detailed.currencyDisplaySymbol.orEmpty()
                    val formattedNum = CurrencyFormatter.format(detailed.balance, detailed.decimalPlaces)
                    if (code.isNotEmpty()) "$code $displaySymbol$formattedNum" else "$displaySymbol$formattedNum"
                } else {
                    ""
                }
            } else {
                detailed.status?.name ?: unknownStatus
            }

            return DetailedPocket(
                accountId = detailed.pocket.accountId,
                name = detailed.productName ?: unknownAccount,
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

        // Upstream PR #2057 (manage-pocket) replaced the "one currency at a time"
        // total-balance sum with a multi-currency per-code sum, each rendered
        // `"$code $displaySymbol$sum"` and joined by newline (the pocket dashboard
        // widget renders `totalBalance` as multi-line text). Bring that over —
        // OUR Store5 read path is unchanged; only the fold-to-UI is enriched.
        val balancesByCurrency = detailedAccounts
            .filter { it.status == AccountStatus.ACTIVE && it.balance != null && it.currencyCode != null }
            .groupBy { it.currencyCode!! }
            .map { (currencyCode, accounts) ->
                val sum = accounts.sumOf { it.balance ?: 0.0 }
                val decimalPlaces = accounts.first().decimalPlaces
                val displaySymbol = accounts.first().currencyDisplaySymbol.orEmpty()
                val formattedNum = CurrencyFormatter.format(sum, decimalPlaces)
                if (currencyCode.isNotEmpty()) "$currencyCode $displaySymbol$formattedNum" else "$displaySymbol$formattedNum"
            }

        val formattedTotal = if (balancesByCurrency.isNotEmpty()) {
            balancesByCurrency.joinToString("\n")
        } else {
            val sampleAccount = detailedAccounts.firstOrNull { it.currencyCode != null }
            if (sampleAccount != null) {
                val code = sampleAccount.currencyCode.orEmpty()
                val displaySymbol = sampleAccount.currencyDisplaySymbol.orEmpty()
                val formattedNum = CurrencyFormatter.format(0.0, sampleAccount.decimalPlaces)
                if (code.isNotEmpty()) "$code $displaySymbol$formattedNum" else "$displaySymbol$formattedNum"
            } else {
                "0.00"
            }
        }

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
