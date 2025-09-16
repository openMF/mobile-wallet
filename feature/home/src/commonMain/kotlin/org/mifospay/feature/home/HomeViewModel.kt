/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.home

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import mobile_wallet.feature.home.generated.resources.Res
import mobile_wallet.feature.home.generated.resources.feature_home_account_error
import mobile_wallet.feature.home.generated.resources.feature_home_account_success
import mobile_wallet.feature.home.generated.resources.feature_home_no_account
import org.jetbrains.compose.resources.StringResource
import org.mifospay.core.common.DataState
import org.mifospay.core.data.repository.SelfServiceRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.account.Account
import org.mifospay.core.model.account.DefaultAccount
import org.mifospay.core.model.client.Client
import org.mifospay.core.model.savingsaccount.Transaction
import org.mifospay.core.model.savingsaccount.TransactionType
import org.mifospay.core.ui.utils.BaseViewModel

private const val TRANSACTION_LIMIT = 5

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val preferencesRepository: UserPreferencesRepository,
    private val repository: SelfServiceRepository,
) : BaseViewModel<HomeState, HomeEvent, HomeAction>(
    initialState = run {
        val client = requireNotNull(preferencesRepository.client.value)
        val defaultAccount = preferencesRepository.defaultAccountId.value

        HomeState(
            client = client,
            defaultAccountId = defaultAccount,
        )
    },
) {

    fun getAccounts() {
        viewModelScope.launch {
            repository.getActiveAccounts(state.client.id)
                .collect { result ->
                    when (result) {
                        is DataState.Error -> {
                            mutableStateFlow.update {
                                it.copy(
                                    isRefreshing = false,
                                    viewState = ViewState.Error(Res.string.feature_home_no_account),
                                )
                            }
                        }

                        is DataState.Loading -> {
                            mutableStateFlow.update { it.copy(viewState = ViewState.Loading) }
                        }

                        is DataState.Success -> {
                            if (result.data.isEmpty()) {
                                mutableStateFlow.update {
                                    it.copy(
                                        isRefreshing = false,
                                        accounts = emptyList(),
                                        accountsWithTransactions = emptyMap(),
                                        viewState = ViewState.NoAccounts,
                                    )
                                }
                            } else {
                                val selected = result.data.firstOrNull()

                                if (selected != null) {
                                    mutableStateFlow.update {
                                        it.copy(
                                            isRefreshing = false,
                                            accounts = result.data,
                                            accountsWithTransactions = emptyMap(),
                                            viewState = ViewState.Content,
                                            selectedAccount = selected,
                                            currentSelectedAccount = selected,
                                        )
                                    }
                                }

                                if (state.defaultAccountId == null && selected != null) {
                                    sendAction(HomeAction.MarkAsDefault(selected.id, selected.number))
                                }
                                if (selected != null) {
                                    getAccountBasedOnId(selected)
                                }
                            }
                        }
                    }
                }
        }
    }

    private var loadTransactionsJob: Job? = null

    fun getAccountBasedOnId(account: Account) {
        if (state.accountsWithTransactions.containsKey(account)) {
            mutableStateFlow.update {
                it.copy(
                    transactions = state.accountsWithTransactions[account],
                    selectedAccount = account,
                    currentSelectedAccount = account,
                )
            }
            applyFilter()
        } else {
            // cancel the previous job if it's still active
            loadTransactionsJob?.cancel()

            // launch a new job
            loadTransactionsJob = viewModelScope.launch {
                repository.getTransactions(
                    account.id,
                    TRANSACTION_LIMIT,
                ).collect { result ->
                    when (result) {
                        is DataState.Error -> {
                            mutableStateFlow.update {
                                it.copy(
                                    transactionsLoading = false,
                                    transactions = emptyList(),
                                    selectedAccount = account,
                                    currentSelectedAccount = account,
                                )
                            }
                        }
                        DataState.Loading -> {
                            mutableStateFlow.update {
                                it.copy(
                                    transactions = emptyList(),
                                    transactionsLoading = true,
                                )
                            }
                        }
                        is DataState.Success -> {
                            val newMap = state.accountsWithTransactions.toMutableMap()
                            newMap.put(account, result.data)
                            mutableStateFlow.update {
                                it.copy(
                                    transactionsLoading = false,
                                    transactions = result.data,
                                    selectedAccount = account,
                                    currentSelectedAccount = account,
                                    accountsWithTransactions = newMap,
                                )
                            }
                            applyFilter()
                        }
                    }
                }
            }
        }
    }

    override fun handleAction(action: HomeAction) {
        when (action) {
            is HomeAction.RequestClicked -> {
                val vpa = state.client.mobileNo.ifEmpty { state.client.externalId }
                sendEvent(HomeEvent.NavigateToRequestScreen(vpa))
            }

            is HomeAction.SendClicked -> {
                sendEvent(HomeEvent.NavigateToSendScreen)
            }

            is HomeAction.ClientDetailsClicked -> {
                sendEvent(HomeEvent.NavigateToClientDetailScreen)
            }

            is HomeAction.OnClickSeeAllTransactions -> {
                sendEvent(HomeEvent.NavigateToTransactionScreen)
            }

            is HomeAction.TransactionClicked -> {
                sendEvent(
                    HomeEvent.NavigateToTransactionDetail(
                        action.accountId,
                        action.transactionId,
                    ),
                )
            }

            is HomeAction.OnDismissDialog -> {
                mutableStateFlow.update {
                    it.copy(dialogState = null)
                }
            }

            is HomeAction.OnNavigateBack -> {
                sendEvent(HomeEvent.NavigateBack)
            }

            is HomeAction.AccountDetailsClicked -> {
                sendEvent(HomeEvent.NavigateToAccountDetail(action.accountId))
            }

            is HomeAction.MarkAsDefault -> {
                viewModelScope.launch {
                    val result = preferencesRepository.updateDefaultAccount(
                        DefaultAccount(
                            accountId = action.accountId,
                            accountNo = action.accountNo,
                        ),
                    )

                    when (result) {
                        is DataState.Loading -> {}
                        is DataState.Error -> {
                            sendEvent(HomeEvent.ShowToast(Res.string.feature_home_account_error))
                        }

                        is DataState.Success -> {
                            mutableStateFlow.update {
                                it.copy(defaultAccountId = action.accountId)
                            }
                            sendEvent(HomeEvent.ShowToast(Res.string.feature_home_account_success))
                        }
                    }
                }
            }

            is HomeAction.OnRetryClicked -> {
                getAccounts()
            }

            is HomeAction.OnPullToRefresh -> {
                mutableStateFlow.update {
                    it.copy(isRefreshing = true)
                }
                getAccounts()
            }

            is HomeAction.OnSelectedAccountChanged -> {
                getAccountBasedOnId(action.account)
            }

            HomeAction.DismissBottomSheet -> {
                mutableStateFlow.update {
                    it.copy(showBottomSheet = false)
                }
            }

            HomeAction.ShowBottomSheet -> {
                mutableStateFlow.update {
                    it.copy(showBottomSheet = true)
                }
            }

            HomeAction.ClearFilters -> {
                mutableStateFlow.update {
                    it.copy(
                        transactionType = TransactionType.OTHER,
                        currentSelectedTransactionType = TransactionType.OTHER,
                        showBottomSheet = false,
                        transactions = it.accountsWithTransactions[it.selectedAccount],
                    )
                }
            }

            HomeAction.OnApplyFilterClick -> {
                mutableStateFlow.update {
                    it.copy(
                        showBottomSheet = false,
                    )
                }
                if (state.currentSelectedAccount != null) {
                    getAccountBasedOnId(state.currentSelectedAccount!!)
                }
            }

            is HomeAction.SetFilter -> {
                mutableStateFlow.update {
                    it.copy(currentSelectedTransactionType = action.filter)
                }
            }

            is HomeAction.OnFilterAccountSelected -> {
                mutableStateFlow.update {
                    it.copy(
                        currentSelectedAccount = action.account,
                    )
                }
            }

            is HomeAction.OnFilterTransactionTypeSelected -> {
                mutableStateFlow.update {
                    it.copy(
                        currentSelectedTransactionType = action.transactionType,
                    )
                }
            }
        }
    }

    private fun applyFilter() {
        val filter = state.currentSelectedTransactionType
        val transactions = state.accountsWithTransactions[state.currentSelectedAccount]
        val filteredTransactions = transactions?.filter {
            if (filter == TransactionType.OTHER) {
                true
            } else {
                it.transactionType == filter
            }
        }

        mutableStateFlow.update {
            it.copy(
                transactions = filteredTransactions,
                transactionType = filter,
                selectedAccount = it.currentSelectedAccount,
            )
        }
    }
}

@Serializable
data class HomeState(
    val client: Client,
    val defaultAccountId: Long?,
    val transactionType: TransactionType = TransactionType.OTHER,
    val currentTransactionType: TransactionType = TransactionType.OTHER,
    val selectedAccount: Account? = null,
    val currentSelectedAccount: Account? = null,
    val isRefreshing: Boolean = false,
    val dialogState: DialogState? = null,
    val accounts: List<Account> = emptyList(),
    val accountsWithTransactions: Map<Account, List<Transaction>> = emptyMap(),
    val viewState: ViewState = ViewState.Loading,
    val transactions: List<Transaction>? = null,
    val showBottomSheet: Boolean = false,
    val transactionsLoading: Boolean = true,
    val currentSelectedTransactionType: TransactionType = TransactionType.OTHER,
) {

    @Serializable
    sealed class DialogState {

        @Serializable
        data object Loading : DialogState()

        @Serializable
        data class Error(val message: String) : DialogState()
    }
}

sealed interface ViewState {
    data object Loading : ViewState

    data class Error(val message: StringResource) : ViewState

    data object Content : ViewState

    data object NoAccounts : ViewState
}

sealed interface HomeEvent {
    data object NavigateBack : HomeEvent
    data object NavigateToSendScreen : HomeEvent
    data object NavigateToTransactionScreen : HomeEvent
    data object NavigateToClientDetailScreen : HomeEvent
    data class NavigateToRequestScreen(val vpa: String) : HomeEvent
    data class NavigateToTransactionDetail(val accountId: Long, val transactionId: Long) : HomeEvent
    data class NavigateToAccountDetail(val accountId: Long) : HomeEvent

    data class ShowToast(val message: StringResource) : HomeEvent
}

sealed interface HomeAction {
    data object RequestClicked : HomeAction
    data object SendClicked : HomeAction
    data object ClientDetailsClicked : HomeAction
    data object OnClickSeeAllTransactions : HomeAction
    data object OnDismissDialog : HomeAction
    data object OnNavigateBack : HomeAction
    data object OnRetryClicked : HomeAction
    data object OnPullToRefresh : HomeAction

    data class MarkAsDefault(val accountId: Long, val accountNo: String) : HomeAction
    data class AccountDetailsClicked(val accountId: Long) : HomeAction
    data class TransactionClicked(val accountId: Long, val transactionId: Long) : HomeAction

    data class OnSelectedAccountChanged(val account: Account) : HomeAction
    data object ShowBottomSheet : HomeAction
    data object DismissBottomSheet : HomeAction
    data object OnApplyFilterClick : HomeAction
    data object ClearFilters : HomeAction
    data class SetFilter(val filter: TransactionType) : HomeAction

    data class OnFilterTransactionTypeSelected(val transactionType: TransactionType) : HomeAction
    data class OnFilterAccountSelected(val account: Account) : HomeAction
}
