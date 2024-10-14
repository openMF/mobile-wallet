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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifospay.core.common.Result
import org.mifospay.core.data.repository.SelfServiceRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.savingsaccount.Transaction
import org.mifospay.core.model.savingsaccount.TransactionType
import org.mifospay.core.ui.utils.BaseViewModel

class HistoryViewModel(
    private val preferencesRepository: UserPreferencesRepository,
    private val repository: SelfServiceRepository,
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
        trySendAction(HistoryAction.LoadTransactions)
    }

    override fun handleAction(action: HistoryAction) {
        when (action) {
            is HistoryAction.SetFilter -> applyFilter(action.filter)

            is HistoryAction.ViewTransaction -> {
                sendEvent(HistoryEvent.OnTransactionDetail(action.transferId))
            }

            HistoryAction.LoadTransactions -> loadAccounts(state.clientId)
        }
    }

    private fun loadAccounts(clientId: Long) {
        viewModelScope.launch {
            mutableStateFlow.update { it.copy(viewState = HistoryState.ViewState.Loading) }
            when (val result = repository.getSelfAccounts(clientId)) {
                is Result.Error -> {
                    mutableStateFlow.update {
                        it.copy(viewState = HistoryState.ViewState.Error("No accounts found"))
                    }
                }

                is Result.Loading -> {
                    mutableStateFlow.update { it.copy(viewState = HistoryState.ViewState.Loading) }
                }

                is Result.Success -> {
                    loadTransactions(result.data.first().id)
                }
            }
        }
    }

    private fun loadTransactions(accountId: Long) {
        repository.getSelfAccountTransactions(accountId).onEach { result ->
            mutableStateFlow.update { currentState ->
                currentState.copy(
                    transactions = result,
                    viewState = if (result.isEmpty()) {
                        HistoryState.ViewState.Empty
                    } else {
                        HistoryState.ViewState.Content(result)
                    },
                )
            }
        }.catch {
            mutableStateFlow.update {
                it.copy(viewState = HistoryState.ViewState.Error("Failed to load transactions"))
            }
        }.launchIn(viewModelScope)
    }

    private fun applyFilter(filter: TransactionType) {
        val filteredTransactions = state.transactions.filter {
            if (filter == TransactionType.OTHER) {
                true
            } else {
                it.transactionType == filter
            }
        }

        mutableStateFlow.update {
            it.copy(
                transactionType = filter,
                viewState = if (filteredTransactions.isEmpty()) {
                    HistoryState.ViewState.Empty
                } else {
                    HistoryState.ViewState.Content(filteredTransactions)
                },
            )
        }
    }
}

data class HistoryState(
    val clientId: Long,
    val viewState: ViewState,
    val transactionType: TransactionType,
    val transactions: List<Transaction> = emptyList(),
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
    data object LoadTransactions : HistoryAction

    data class SetFilter(val filter: TransactionType) : HistoryAction
    data class ViewTransaction(val transferId: Long) : HistoryAction
}
