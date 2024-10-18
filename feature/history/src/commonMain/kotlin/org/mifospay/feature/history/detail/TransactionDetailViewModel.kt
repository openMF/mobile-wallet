/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.history.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.mifospay.core.common.DataState
import org.mifospay.core.data.repository.AccountRepository
import org.mifospay.core.model.savingsaccount.TransferDetail
import org.mifospay.core.ui.utils.BaseViewModel
import org.mifospay.feature.history.detail.TransactionDetailAction.TransferDetailReceive
import org.mifospay.feature.history.detail.TransactionDetailState.ViewState.Content
import org.mifospay.feature.history.detail.TransactionDetailState.ViewState.Error

internal class TransactionDetailViewModel(
    savedStateHandle: SavedStateHandle,
    accountRepository: AccountRepository,
) : BaseViewModel<TransactionDetailState, TransactionDetailEvent, TransactionDetailAction>(
    initialState = TransactionDetailState(TransactionDetailState.ViewState.Loading),
) {

    init {
        savedStateHandle.get<Long>("transferId")?.let { transferId ->
            accountRepository.getAccountTransfer(transferId).onEach {
                sendAction(TransferDetailReceive(it))
            }.launchIn(viewModelScope)
        }
    }

    override fun handleAction(action: TransactionDetailAction) {
        when (action) {
            is TransactionDetailAction.NavigateBack -> {
                sendEvent(TransactionDetailEvent.OnNavigateBack)
            }

            TransactionDetailAction.ShareTransaction -> {
                sendEvent(TransactionDetailEvent.OnShareTransaction)
            }

            is TransferDetailReceive -> handleTransferDetailReceive(action)
        }
    }

    private fun handleTransferDetailReceive(action: TransferDetailReceive) {
        when (action.result) {
            is DataState.Error -> {
                mutableStateFlow.update {
                    it.copy(viewState = Error(action.result.exception.message ?: "Error"))
                }
            }

            is DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(viewState = TransactionDetailState.ViewState.Loading)
                }
            }

            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(viewState = Content(action.result.data))
                }
            }
        }
    }
}

internal data class TransactionDetailState(
    val viewState: ViewState,
) {
    internal sealed interface ViewState {
        data object Loading : ViewState
        data class Error(val message: String) : ViewState
        data class Content(val transaction: TransferDetail) : ViewState
    }
}

internal sealed interface TransactionDetailEvent {
    data object OnNavigateBack : TransactionDetailEvent
    data object OnShareTransaction : TransactionDetailEvent
}

internal sealed interface TransactionDetailAction {
    data object NavigateBack : TransactionDetailAction
    data object ShareTransaction : TransactionDetailAction
    data class TransferDetailReceive(val result: DataState<TransferDetail>) : TransactionDetailAction
}
