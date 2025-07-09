/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.accounts.savingsaccount.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable
import org.mifospay.core.common.DataState
import org.mifospay.core.common.getSerialized
import org.mifospay.core.data.repository.SavingsAccountRepository
import org.mifospay.core.model.savingsaccount.SavingAccountDetail
import org.mifospay.core.ui.utils.BaseViewModel
import org.mifospay.feature.accounts.savingsaccount.details.SADAction.Internal.SavingAccountDetailResultReceived
import org.mifospay.feature.accounts.savingsaccount.details.SADState.ViewState.Error

internal class SavingAccountDetailViewModel(
    savedStateHandle: SavedStateHandle,
    repository: SavingsAccountRepository,
) : BaseViewModel<SADState, SADEvent, SADAction>(
    initialState = savedStateHandle.getSerialized(KEY) ?: SADState(
        accountId = requireNotNull(savedStateHandle[ACCOUNT_ID_KEY]),
        viewState = SADState.ViewState.Loading,
    ),
) {

    companion object {
        private const val KEY = "saving_account_detail"
        private const val ACCOUNT_ID_KEY = "accountId"
    }

    init {
        repository.getAccountDetail(state.accountId).onEach {
            sendAction(SavingAccountDetailResultReceived(it))
        }.launchIn(viewModelScope)
    }

    override fun handleAction(action: SADAction) {
        when (action) {
            is SADAction.NavigateBack -> {
                sendEvent(SADEvent.NavigateBack)
            }

            is SADAction.ViewTransaction -> {
                sendEvent(SADEvent.OnViewTransaction(action.clientId, action.accountId))
            }

            is SavingAccountDetailResultReceived -> handleSavingAccountDetailResult(action)
        }
    }

    private fun handleSavingAccountDetailResult(action: SavingAccountDetailResultReceived) {
        when (action.result) {
            is DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(viewState = SADState.ViewState.Loading)
                }
            }

            is DataState.Error -> {
                mutableStateFlow.update {
                    it.copy(viewState = Error(action.result.exception.message.toString()))
                }
            }

            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(viewState = SADState.ViewState.Content(action.result.data))
                }
            }
        }
    }
}

@Serializable
internal data class SADState(
    val accountId: Long,
    val viewState: ViewState,
) {
    @Serializable
    sealed interface ViewState {
        @Serializable
        data object Loading : ViewState

        @Serializable
        data class Error(val message: String) : ViewState

        @Serializable
        data class Content(val data: SavingAccountDetail) : ViewState
    }
}

internal sealed interface SADEvent {
    data class ShowToast(val message: String) : SADEvent
    data class OnViewTransaction(val clientId: Long, val accountId: Long) : SADEvent
    data object NavigateBack : SADEvent
}

internal sealed interface SADAction {
    data object NavigateBack : SADAction
    data class ViewTransaction(val clientId: Long, val accountId: Long) : SADAction

    sealed interface Internal : SADAction {
        data class SavingAccountDetailResultReceived(
            val result: DataState<SavingAccountDetail>,
        ) : Internal
    }
}
