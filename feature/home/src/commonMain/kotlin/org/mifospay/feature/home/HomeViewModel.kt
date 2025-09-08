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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
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
    /**
     * Exposes the current view state for the Home screen (loading, content, or error),
     * and automatically updates when a reload is triggered via the state.
     *
     * This state is driven by [HomeState.reloadTrigger], which toggles whenever the user
     * explicitly requests a refresh — for example, by clicking the Retry button on an error screen,
     * or performing a pull-to-refresh gesture.
     *
     * The flow reacts to changes in [reloadTrigger] using `flatMapLatest`, triggering a new
     * call to [SelfServiceRepository.getActiveAccountsWithTransactions]. The result of that call
     * is mapped into a [ViewState], and exposed via a hot [StateFlow] for UI consumption.
     *
     * Additionally, [HomeState.isRefreshing] is reset to false once a response (either success or error)
     * is received, ensuring the UI (e.g. pull-to-refresh indicator) stops spinning.
     */
    val accountState = stateFlow
        .mapLatest { it.reloadTrigger }
        .flatMapLatest {
            repository.getActiveAccountsWithTransactions(
                clientId = state.client.id,
                limit = TRANSACTION_LIMIT,
            )
        }.mapLatest { result ->
            when (result) {
                is DataState.Error -> {
                    mutableStateFlow.update {
                        it.copy(isRefreshing = false)
                    }
                    ViewState.Error(Res.string.feature_home_no_account)
                }

                is DataState.Loading -> {
                    ViewState.Loading
                }

                is DataState.Success -> {
                    mutableStateFlow.update {
                        it.copy(isRefreshing = false)
                    }

                    if (state.defaultAccountId == null && result.data.accounts.isNotEmpty()) {
                        val account = result.data.accounts.first()
                        val accountId = account.id
                        val accountNo = account.number

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
                mutableStateFlow.update {
                    it.copy(reloadTrigger = !it.reloadTrigger)
                }
            }

            is HomeAction.OnPullToRefresh -> {
                mutableStateFlow.update {
                    it.copy(isRefreshing = true, reloadTrigger = !it.reloadTrigger)
                }
            }
        }
    }
}

@Serializable
data class HomeState(
    val client: Client,
    val defaultAccountId: Long?,
    val reloadTrigger: Boolean = false,
    val isRefreshing: Boolean = false,
    val dialogState: DialogState? = null,
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
}
