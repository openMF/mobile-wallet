/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.history

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.mifospay.core.common.DataState
import org.mifospay.core.data.repository.SelfServiceRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.account.Account
import org.mifospay.core.model.savingsaccount.Transaction
import org.mifospay.core.model.savingsaccount.TransactionType
import org.mifospay.core.ui.utils.BaseViewModel
import org.mifospay.feature.history.HistoryAction.Internal.TransactionsLoaded

class HistoryViewModel(
    private val preferencesRepository: UserPreferencesRepository,
    repository: SelfServiceRepository,
) : BaseViewModel<HistoryState, HistoryEvent, HistoryAction>(
    initialState = run {
        val clientId = requireNotNull(preferencesRepository.clientId.value)
        HistoryState(
            clientId = clientId,
            viewState = HistoryState.ViewState.Loading,
            transactionType = TransactionType.OTHER,
        )
    },
) {
    init {
        repository.getActiveAccountsWithTransactionsPerAccount(state.clientId, null).onEach {
            sendAction(TransactionsLoaded(it))
        }.launchIn(viewModelScope)
    }

    override fun handleAction(action: HistoryAction) {
        when (action) {
            is HistoryAction.SetFilter -> handleSetFilter(action.filter)

            is HistoryAction.ViewTransaction -> handleViewTransaction(action.transferId)

            is TransactionsLoaded -> handleTransactionLoaded(action)

            is HistoryAction.OnFilterClick -> handleFilterClick()

            is HistoryAction.OnApplyFilterClick -> handleApplyFilterClick()

            is HistoryAction.SetSelectedAccount -> handleSetSelectedAccount(action.account)

            HistoryAction.ClearFilters -> handleClearFilters()
        }
    }

    private fun handleClearFilters() {
        mutableStateFlow.update {
            it.copy(
                currentSelectedTransactionType = TransactionType.OTHER,
                showFilter = false,
            )
        }
        applyFilter(state.transactions)
    }
    private fun handleApplyFilterClick() {
        val transactions = state.transactionsWithAccounts[state.currentSelectedAccount]
        mutableStateFlow.update {
            it.copy(
                showFilter = false,
                filteredEmpty = transactions.isNullOrEmpty(),
                selectedAccount = state.currentSelectedAccount,
                transactions = transactions ?: emptyList(),
            )
        }
        applyFilter(transactions)
    }

    private fun handleSetFilter(filter: TransactionType) {
        mutableStateFlow.update {
            it.copy(currentSelectedTransactionType = filter)
        }
    }

    private fun handleSetSelectedAccount(account: Account) {
        mutableStateFlow.update {
            it.copy(currentSelectedAccount = account)
        }
    }

    private fun handleViewTransaction(transferId: Long) {
        sendEvent(HistoryEvent.OnTransactionDetail(transferId))
    }

    private fun handleFilterClick() {
        mutableStateFlow.update {
            it.copy(showFilter = !state.showFilter)
        }
    }

    private fun applyFilter(transactions: List<Transaction>?) {
        val filter = state.currentSelectedTransactionType
        val filteredTransactions = transactions?.filter {
            if (filter == TransactionType.OTHER) {
                true
            } else {
                it.transactionType == filter
            }
        }

        mutableStateFlow.update {
            it.copy(
                transactionType = filter,
                viewState = HistoryState.ViewState.Content(filteredTransactions ?: emptyList()),
                filteredEmpty = filteredTransactions.isNullOrEmpty(),
            )
        }
    }

    private fun handleTransactionLoaded(action: TransactionsLoaded) {
        when (action.result) {
            is DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(viewState = HistoryState.ViewState.Loading)
                }
            }

            is DataState.Error -> {
                val message = action.result.exception.message.toString()
                mutableStateFlow.update {
                    it.copy(viewState = HistoryState.ViewState.Error(message))
                }
            }

            is DataState.Success -> {
                val result = action.result.data
                val firstAccount = result.keys.first()
                mutableStateFlow.update {
                    it.copy(
                        transactionsWithAccounts = result,
                        accounts = result.keys.toList(),
                        selectedAccount = firstAccount,
                        currentSelectedAccount = firstAccount,
                        transactions = result[firstAccount] ?: emptyList(),
                        viewState = if (action.result.data.isEmpty()) {
                            HistoryState.ViewState.Empty
                        } else {
                            HistoryState.ViewState.Content(result[firstAccount] ?: emptyList())
                        },
                    )
                }
            }
        }
    }
}

data class HistoryState(
    val clientId: Long,
    val viewState: ViewState,
    val transactionType: TransactionType,
    val currentSelectedTransactionType: TransactionType = TransactionType.OTHER,
    val transactions: List<Transaction> = emptyList(),
    val transactionsWithAccounts: Map<Account, List<Transaction>> = emptyMap(),
    val accounts: List<Account> = emptyList(),
    val selectedAccount: Account? = null,
    val currentSelectedAccount: Account? = null,
    val showFilter: Boolean = false,
    val filteredEmpty: Boolean = false,
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data object Empty : ViewState
        data class Error(val message: String) : ViewState
        data class Content(val list: List<Transaction>) : ViewState
    }
}

sealed interface HistoryEvent {
    data class OnTransactionDetail(val transferId: Long) : HistoryEvent
}

sealed interface HistoryAction {
    data class SetFilter(val filter: TransactionType) : HistoryAction
    data class ViewTransaction(val transferId: Long) : HistoryAction
    data object OnFilterClick : HistoryAction
    data class SetSelectedAccount(val account: Account) : HistoryAction
    data object OnApplyFilterClick : HistoryAction
    data object ClearFilters : HistoryAction

    sealed interface Internal : HistoryAction {
        data class TransactionsLoaded(val result: DataState<Map<Account, List<Transaction>>>) : Internal
    }
}
