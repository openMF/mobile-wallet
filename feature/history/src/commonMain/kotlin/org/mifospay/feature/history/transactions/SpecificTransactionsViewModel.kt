/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.history.transactions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.mifospay.core.common.DataState
import org.mifospay.core.data.repository.AccountRepository
import org.mifospay.core.model.savingsaccount.Transaction
import org.mifospay.core.model.savingsaccount.TransferDetail
import org.mifospay.core.ui.utils.BaseViewModel
import org.mifospay.feature.history.transactions.STState.ViewState.Content
import org.mifospay.feature.history.transactions.STState.ViewState.Error

internal class SpecificTransactionsViewModel(
    private val accountRepository: AccountRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<STState, STEvent, STAction>(
    initialState = STState(viewState = STState.ViewState.Loading),
) {

    init {
        savedStateHandle.get<Long>("accountId")?.let { accountId ->
            savedStateHandle.get<Long>("transactionId")?.let { transactionId ->
                accountRepository.getTransaction(accountId, transactionId).onEach {
                    sendAction(STAction.Internal.TransactionReceive(it))
                }.launchIn(viewModelScope)
            }
        }
    }

    override fun handleAction(action: STAction) {
        when (action) {
            is STAction.NavigateBack -> {
                sendEvent(STEvent.OnNavigateBack)
            }

            is STAction.ViewTransaction -> {
                sendEvent(STEvent.OnViewTransaction(action.transferId))
            }

            is STAction.Internal.TransactionReceive -> handleTransactionReceive(action)
        }
    }

    private fun handleTransactionReceive(action: STAction.Internal.TransactionReceive) {
        when (action.result) {
            is DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(viewState = STState.ViewState.Loading)
                }
            }

            is DataState.Error -> {
                mutableStateFlow.update {
                    it.copy(viewState = Error(action.result.exception.message.toString()))
                }
            }

            is DataState.Success -> {
                handleTransferDetailReceive(action.result.data)
            }
        }
    }

    private fun handleTransferDetailReceive(transaction: Transaction) {
        transaction.transferId?.let { transferId ->
            accountRepository.getAccountTransfer(transferId)
                .onEach { result: DataState<TransferDetail> ->
                    when (result) {
                        is DataState.Error -> {
                            mutableStateFlow.update {
                                it.copy(viewState = Error(result.exception.message.toString()))
                            }
                        }

                        is DataState.Loading -> {
                            mutableStateFlow.update {
                                it.copy(viewState = STState.ViewState.Loading)
                            }
                        }

                        is DataState.Success -> {
                            mutableStateFlow.update {
                                it.copy(viewState = Content(transaction, result.data))
                            }
                        }
                    }
                }.launchIn(viewModelScope)
        } ?: run {
            mutableStateFlow.update {
                it.copy(viewState = Content(transaction, null))
            }
        }
    }
}

internal data class STState(
    val viewState: ViewState,
) {
    sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: String) : ViewState
        data class Content(
            val transaction: Transaction,
            val detail: TransferDetail?,
        ) : ViewState
    }
}

internal sealed interface STEvent {
    data object OnNavigateBack : STEvent
    data class OnViewTransaction(val transferId: Long) : STEvent
}

internal sealed interface STAction {
    data object NavigateBack : STAction
    data class ViewTransaction(val transferId: Long) : STAction

    sealed interface Internal : STAction {
        data class TransactionReceive(val result: DataState<Transaction>) : Internal
    }
}
