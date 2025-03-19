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
import org.mifospay.core.common.DataState
import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize
import org.mifospay.core.data.repository.SavingsAccountRepository
import org.mifospay.core.model.savingsaccount.SavingAccountDetail
import org.mifospay.core.ui.utils.BaseViewModel
import org.mifospay.feature.accounts.savingsaccount.details.SADAction.Internal.SavingAccountDetailResultReceived
import org.mifospay.feature.accounts.savingsaccount.details.SADState.ViewState.Error

private const val KEY = "saving_account_detail"

/**
 * ViewModel for managing the details of a savings account.
 * 
 * @param savedStateHandle The saved state handle for managing state.
 * @param repository The repository for savings account operations.
 */
 
internal class SavingAccountDetailViewModel(
    savedStateHandle: SavedStateHandle,
    repository: SavingsAccountRepository,
) : BaseViewModel<SADState, SADEvent, SADAction>(
    initialState = savedStateHandle[KEY] ?: SADState(
        accountId = requireNotNull(savedStateHandle["accountId"]),
        viewState = SADState.ViewState.Loading,
    ),
) {
    /**
     * Initializes the ViewModel and fetches saving account details.
     */
    init {
        repository.getAccountDetail(state.accountId).onEach {
            sendAction(SavingAccountDetailResultReceived(it))
        }.launchIn(viewModelScope)
    }

    /**
     * Handles user actions related to saving account details.
     * 
     * @param action The action to handle.
     */
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

    /**
     * Handles the result of fetching saving account details.
     * 
     * @param action The action containing the result.
     */
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

/**
 * Data class representing the state of the saving account detail screen.
 * 
 * @property accountId The ID of the savings account.
 * @property viewState The current view state of the screen.
 */
@Parcelize
internal data class SADState(
    val accountId: Long,
    val viewState: ViewState,
) : Parcelable {
    sealed interface ViewState : Parcelable {
        @Parcelize
        data object Loading : ViewState

        @Parcelize
        data class Error(val message: String) : ViewState

        @Parcelize
        data class Content(val data: SavingAccountDetail) : ViewState
    }
}

/**
 * Sealed interface representing events related to saving account details.
 */
internal sealed interface SADEvent {
    data class ShowToast(val message: String) : SADEvent
    data class OnViewTransaction(val clientId: Long, val accountId: Long) : SADEvent
    data object NavigateBack : SADEvent
}

/**
 * Sealed interface representing actions related to saving account details.
 */
internal sealed interface SADAction {
    data object NavigateBack : SADAction
    data class ViewTransaction(val clientId: Long, val accountId: Long) : SADAction

    sealed interface Internal : SADAction {
        data class SavingAccountDetailResultReceived(
            val result: DataState<SavingAccountDetail>,
        ) : Internal
    }
}
