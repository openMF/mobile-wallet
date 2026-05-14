/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.pocket.dashboard

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable
import org.mifospay.core.common.DataState
import org.mifospay.core.data.repository.PocketRepository
import org.mifospay.core.model.pocket.Pocket
import org.mifospay.core.ui.utils.BaseViewModel

class PocketDashboardViewModel(
    private val pocketRepository: PocketRepository,
) : BaseViewModel<PocketDashboardState, PocketDashboardEvent, PocketDashboardAction>(
    initialState = PocketDashboardState(),
) {
    init {
        loadPocket()
    }

    override fun handleAction(action: PocketDashboardAction) {
        when (action) {
            is PocketDashboardAction.NavigateBack -> sendEvent(PocketDashboardEvent.NavigateBack)
            is PocketDashboardAction.NavigateToLinkAccount -> sendEvent(PocketDashboardEvent.NavigateToLinkAccount)
            is PocketDashboardAction.DelinkAccount -> delinkAccount(action.accountId)
            is PocketDashboardAction.Retry -> loadPocket()
            is PocketDashboardAction.NavigateToAccountDetails -> {
                sendEvent(PocketDashboardEvent.NavigateToAccountDetails(action.accountId))
            }
        }
    }

    private fun loadPocket() {
        pocketRepository.getPocket()
            .onEach { state ->
                when (state) {
                    is DataState.Loading -> mutableStateFlow.update { it.copy(contentState = PocketContentState.Loading) }
                    is DataState.Success -> mutableStateFlow.update {
                        it.copy(
                            contentState = if (state.data.linkedAccounts.isEmpty()) {
                                PocketContentState.Empty
                            } else {
                                PocketContentState.Success
                            },
                            pocket = state.data,
                        )
                    }
                    is DataState.Error -> mutableStateFlow.update {
                        it.copy(contentState = PocketContentState.Error(state.exception.message ?: "Unknown error"))
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun delinkAccount(accountId: Long) {
        pocketRepository.delinkAccount(accountId)
            .onEach { result ->
                when (result) {
                    is DataState.Loading -> mutableStateFlow.update { it.copy(isDelinking = true) }
                    is DataState.Success -> {
                        mutableStateFlow.update { it.copy(isDelinking = false) }
                        loadPocket()
                    }
                    is DataState.Error -> mutableStateFlow.update {
                        it.copy(
                            isDelinking = false,
                            snackbarMessage = result.exception.message ?: "Failed to remove account",
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}

@Serializable
data class PocketDashboardState(
    val contentState: PocketContentState = PocketContentState.Loading,
    @kotlinx.serialization.Transient
    val pocket: Pocket? = null,
    val isDelinking: Boolean = false,
    @kotlinx.serialization.Transient
    val snackbarMessage: String? = null,
)

@Serializable
sealed interface PocketContentState {
    @Serializable data object Loading : PocketContentState
    @Serializable data object Success : PocketContentState
    @Serializable data object Empty : PocketContentState
    @Serializable data class Error(val message: String) : PocketContentState
}

sealed interface PocketDashboardEvent {
    data object NavigateBack : PocketDashboardEvent
    data object NavigateToLinkAccount : PocketDashboardEvent
    data class NavigateToAccountDetails(val accountId: Long) : PocketDashboardEvent
}

sealed interface PocketDashboardAction {
    data object NavigateBack : PocketDashboardAction
    data object NavigateToLinkAccount : PocketDashboardAction
    data object Retry : PocketDashboardAction
    data class DelinkAccount(val accountId: Long) : PocketDashboardAction
    data class NavigateToAccountDetails(val accountId: Long) : PocketDashboardAction
}
