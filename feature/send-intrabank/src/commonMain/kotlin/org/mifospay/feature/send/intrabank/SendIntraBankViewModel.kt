/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.send.intrabank

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.mifospay.core.common.DataState
import org.mifospay.core.data.repository.RecentPayeeRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.account.RecentPayee
import org.mifospay.core.ui.utils.BaseViewModel

class SendIntraBankViewModel(
    private val recentPayeeRepository: RecentPayeeRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : BaseViewModel<SendIntraBankState, SendIntraBankEvent, SendIntraBankAction>(
    initialState = SendIntraBankState(),
) {

    init {
        loadRecentPayees()
    }

    override fun handleAction(action: SendIntraBankAction) {
        when (action) {
            SendIntraBankAction.NavigateBack -> {
                sendEvent(SendIntraBankEvent.NavigateBack)
            }

            SendIntraBankAction.OnSearchBarClicked -> {
                sendEvent(SendIntraBankEvent.NavigateToSearchAccountSelection)
            }

            SendIntraBankAction.OnAddPayeeClicked -> {
                sendEvent(SendIntraBankEvent.NavigateToBeneficiary)
            }

            is SendIntraBankAction.OnPayRecentPayee -> {
                sendEvent(
                    SendIntraBankEvent.NavigateToTransfer(
                        toOfficeId = action.payee.officeId.toInt(),
                        toClientId = action.payee.clientId,
                        toAccountId = action.payee.accountId.toInt(),
                        accountName = action.payee.clientName,
                        accountNo = action.payee.accountNo,
                    ),
                )
            }
        }
    }

    private fun loadRecentPayees() {
        val accountId = userPreferencesRepository.defaultAccountId.value
        if (accountId == null) {
            Logger.d { "RecentPayee: No default account ID found" }
            mutableStateFlow.update {
                it.copy(recentPayeesState = RecentPayeesState.Empty)
            }
            return
        }

        Logger.d { "RecentPayee: Loading recent payees for account $accountId" }

        recentPayeeRepository.getRecentPayees(accountId, RECENT_PAYEES_LIMIT)
            .onEach { result ->
                when (result) {
                    is DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(recentPayeesState = RecentPayeesState.Loading)
                        }
                    }
                    is DataState.Success -> {
                        Logger.d { "RecentPayee: Loaded ${result.data.size} recent payees" }
                        mutableStateFlow.update {
                            it.copy(
                                recentPayees = result.data,
                                recentPayeesState = if (result.data.isEmpty()) {
                                    RecentPayeesState.Empty
                                } else {
                                    RecentPayeesState.Success
                                },
                            )
                        }
                    }
                    is DataState.Error -> {
                        Logger.e(result.exception) { "RecentPayee: Failed to load" }
                        mutableStateFlow.update {
                            it.copy(recentPayeesState = RecentPayeesState.Error)
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    companion object {
        private const val RECENT_PAYEES_LIMIT = 10
    }
}

@Serializable
data class SendIntraBankState(
    @Transient
    val recentPayees: List<RecentPayee> = emptyList(),
    @Transient
    val recentPayeesState: RecentPayeesState = RecentPayeesState.Loading,
)

enum class RecentPayeesState {
    Loading,
    Success,
    Empty,
    Error,
}

sealed interface SendIntraBankEvent {
    data object NavigateToSearchAccountSelection : SendIntraBankEvent
    data object NavigateBack : SendIntraBankEvent
    data object NavigateToBeneficiary : SendIntraBankEvent
    data class NavigateToTransfer(
        val toOfficeId: Int,
        val toClientId: Long,
        val toAccountId: Int,
        val accountName: String,
        val accountNo: String,
    ) : SendIntraBankEvent
}

sealed interface SendIntraBankAction {
    data object NavigateBack : SendIntraBankAction
    data object OnSearchBarClicked : SendIntraBankAction
    data object OnAddPayeeClicked : SendIntraBankAction
    data class OnPayRecentPayee(val payee: RecentPayee) : SendIntraBankAction
}
