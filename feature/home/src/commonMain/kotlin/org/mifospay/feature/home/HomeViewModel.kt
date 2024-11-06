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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifospay.core.common.DataState
import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize
import org.mifospay.core.data.repository.SelfServiceRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.account.Account
import org.mifospay.core.model.account.DefaultAccount
import org.mifospay.core.model.client.Client
import org.mifospay.core.model.savingsaccount.Transaction
import org.mifospay.core.ui.utils.BaseViewModel

private const val TRANSACTION_LIMIT = 5

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val preferencesRepository: UserPreferencesRepository,
    repository: SelfServiceRepository,
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
    val accountState = repository.getActiveAccountsWithTransactions(
        clientId = state.client.id,
        limit = TRANSACTION_LIMIT,
    ).mapLatest { result ->
        when (result) {
            is DataState.Error -> ViewState.Error("No accounts found")

            is DataState.Loading -> ViewState.Loading

            is DataState.Success -> {
                if (state.defaultAccountId == null && result.data.accounts.isNotEmpty()) {
                    val accountId = result.data.accounts.first().id
                    val accountNo = result.data.accounts.first().number

                    sendAction(HomeAction.MarkAsDefault(accountId, accountNo))
                }

                ViewState.Content(
                    accounts = result.data.accounts,
                    transactions = result.data.transactions,
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ViewState.Loading,
    )

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
                            sendEvent(HomeEvent.ShowToast("Error marking account as default"))
                        }

                        is DataState.Success -> {
                            mutableStateFlow.update {
                                it.copy(defaultAccountId = action.accountId)
                            }
                            sendEvent(HomeEvent.ShowToast("Account marked as default"))
                        }
                    }
                }
            }
        }
    }
}

@Parcelize
data class HomeState(
    val client: Client,
    val defaultAccountId: Long?,
    val dialogState: DialogState? = null,
) : Parcelable {

    @Parcelize
    sealed class DialogState : Parcelable {

        @Parcelize
        data object Loading : DialogState()

        @Parcelize
        data class Error(val message: String) : DialogState()
    }
}

sealed interface ViewState {
    data object Loading : ViewState

    data class Error(val message: String) : ViewState

    data class Content(
        val accounts: List<Account>,
        val transactions: List<Transaction>,
    ) : ViewState
}

sealed interface HomeEvent {
    data object NavigateBack : HomeEvent
    data object NavigateToSendScreen : HomeEvent
    data object NavigateToTransactionScreen : HomeEvent
    data object NavigateToClientDetailScreen : HomeEvent
    data class NavigateToRequestScreen(val vpa: String) : HomeEvent
    data class NavigateToTransactionDetail(val accountId: Long, val transactionId: Long) : HomeEvent
    data class NavigateToAccountDetail(val accountId: Long) : HomeEvent

    data class ShowToast(val message: String) : HomeEvent
}

sealed interface HomeAction {
    data object RequestClicked : HomeAction
    data object SendClicked : HomeAction
    data object ClientDetailsClicked : HomeAction
    data object OnClickSeeAllTransactions : HomeAction
    data object OnDismissDialog : HomeAction
    data object OnNavigateBack : HomeAction

    data class MarkAsDefault(val accountId: Long, val accountNo: String) : HomeAction
    data class AccountDetailsClicked(val accountId: Long) : HomeAction
    data class TransactionClicked(val accountId: Long, val transactionId: Long) : HomeAction
}
