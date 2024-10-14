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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize
import org.mifospay.core.common.Result
import org.mifospay.core.data.repository.SelfServiceRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.account.Account
import org.mifospay.core.model.client.Client
import org.mifospay.core.model.savingsaccount.Transaction
import org.mifospay.core.ui.utils.BaseViewModel
import org.mifospay.feature.home.HomeAction.Internal.SelectAccount

/*
 * Feature Enhancement
 * Show all saving accounts as stacked card
 * Show transaction history of selected account
 */
class HomeViewModel(
    private val preferencesRepository: UserPreferencesRepository,
    private val repository: SelfServiceRepository,
) : BaseViewModel<HomeState, HomeEvent, HomeAction>(
    initialState = run {
        val client = requireNotNull(preferencesRepository.client.value)
        HomeState(
            client = client,
            viewState = HomeState.ViewState.Loading,
        )
    },
) {
    init {
        trySendAction(HomeAction.Internal.LoadAccounts)

        preferencesRepository.client.onEach { data ->
            data?.let { client ->
                mutableStateFlow.update {
                    it.copy(client = client)
                }
            }
        }.launchIn(viewModelScope)
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

            is HomeAction.Internal.LoadAccounts -> loadAccounts(state.client.id)

            is SelectAccount -> selectAccount(action.accountId)
        }
    }

    private fun loadAccounts(clientId: Long) {
        viewModelScope.launch {
            mutableStateFlow.update { it.copy(viewState = HomeState.ViewState.Loading) }
            when (val result = repository.getSelfAccounts(clientId)) {
                is Result.Error -> {
                    mutableStateFlow.update {
                        it.copy(viewState = HomeState.ViewState.Error("No accounts found"))
                    }
                }

                is Result.Loading -> {
                    mutableStateFlow.update { it.copy(viewState = HomeState.ViewState.Loading) }
                }

                is Result.Success -> {
                    mutableStateFlow.update {
                        it.copy(
                            viewState = HomeState.ViewState.Content(
                                account = result.data.first(),
                                selectedAccountId = result.data.firstOrNull()?.id,
                            ),
                        )
                    }
                    sendAction(SelectAccount(result.data.firstOrNull()?.id ?: 0))
                }
            }
        }
    }

    private fun selectAccount(accountId: Long) {
        viewModelScope.launch {
            mutableStateFlow.update { currentState ->
                when (val viewState = currentState.viewState) {
                    is HomeState.ViewState.Content -> {
                        currentState.copy(
                            viewState = viewState.copy(selectedAccountId = accountId),
                        )
                    }

                    else -> currentState
                }
            }
        }
        loadTransactions(accountId)
    }

    private fun loadTransactions(accountId: Long) {
        repository.getSelfAccountTransactions(accountId).onEach { result ->
            mutableStateFlow.update { currentState ->
                val state = currentState.viewState as HomeState.ViewState.Content
                currentState.copy(
                    viewState = HomeState.ViewState.Content(
                        account = state.account,
                        transactions = result,
                        selectedAccountId = accountId,
                    ),
                    dialogState = null,
                )
            }
        }.catch {
            mutableStateFlow.update {
                it.copy(dialogState = HomeState.DialogState.Error("Failed to load transactions"))
            }
        }.launchIn(viewModelScope)
    }
}

@Parcelize
data class HomeState(
    val client: Client,
    val viewState: ViewState,
    val dialogState: DialogState? = null,
) : Parcelable {

    @Parcelize
    sealed class ViewState : Parcelable {
        data object Loading : ViewState()
        data class Error(val message: String) : ViewState()
        data class Content(
            // val accounts: List<Account>,
            val account: Account,
            val transactions: List<Transaction> = emptyList(),
            val selectedAccountId: Long? = null,
        ) : ViewState()
    }

    @Parcelize
    sealed class DialogState : Parcelable {

        @Parcelize
        data object Loading : DialogState()

        @Parcelize
        data class Error(val message: String) : DialogState()
    }
}

sealed interface HomeEvent {
    data object NavigateBack : HomeEvent
    data class NavigateToRequestScreen(val vpa: String) : HomeEvent
    data object NavigateToSendScreen : HomeEvent
    data object NavigateToTransactionScreen : HomeEvent
    data class NavigateToTransactionDetail(val accountId: Long, val transactionId: Long) : HomeEvent
    data object NavigateToClientDetailScreen : HomeEvent
    data class ShowToast(val message: String) : HomeEvent
}

sealed interface HomeAction {
    data object RequestClicked : HomeAction
    data object SendClicked : HomeAction
    data object ClientDetailsClicked : HomeAction
    data object OnClickSeeAllTransactions : HomeAction
    data object OnDismissDialog : HomeAction
    data object OnNavigateBack : HomeAction

    data class TransactionClicked(val accountId: Long, val transactionId: Long) : HomeAction

    sealed interface Internal : HomeAction {
        data object LoadAccounts : HomeAction
        data class SelectAccount(val accountId: Long) : HomeAction
    }
}
