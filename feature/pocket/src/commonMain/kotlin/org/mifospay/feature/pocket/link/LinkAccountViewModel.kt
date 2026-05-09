/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.pocket.link

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable
import org.mifospay.core.common.DataState
import org.mifospay.core.data.repository.PocketRepository
import org.mifospay.core.model.pocket.PocketAccountType
import org.mifospay.core.ui.utils.BaseViewModel

class LinkAccountViewModel(
    private val pocketRepository: PocketRepository,
) : BaseViewModel<LinkAccountState, LinkAccountEvent, LinkAccountAction>(
    initialState = LinkAccountState(),
) {
    override fun handleAction(action: LinkAccountAction) {
        when (action) {
            is LinkAccountAction.NavigateBack -> sendEvent(LinkAccountEvent.NavigateBack)
            is LinkAccountAction.OnAccountIdChanged -> {
                mutableStateFlow.update { it.copy(accountId = action.accountId, error = null) }
            }
            is LinkAccountAction.OnAccountTypeSelected -> {
                mutableStateFlow.update { it.copy(selectedType = action.type) }
            }
            is LinkAccountAction.Submit -> linkAccount()
        }
    }

    private fun linkAccount() {
        val currentState = mutableStateFlow.value
        val accountId = currentState.accountId.toLongOrNull()

        if (accountId == null) {
            mutableStateFlow.update { it.copy(error = "Please enter a valid account ID") }
            return
        }

        pocketRepository.linkAccount(accountId, currentState.selectedType.name)
            .onEach { result ->
                when (result) {
                    is DataState.Loading -> mutableStateFlow.update { it.copy(isLoading = true, error = null) }
                    is DataState.Success -> {
                        mutableStateFlow.update { it.copy(isLoading = false) }
                        sendEvent(LinkAccountEvent.LinkSuccess)
                    }
                    is DataState.Error -> mutableStateFlow.update {
                        it.copy(
                            isLoading = false,
                            error = result.exception.message ?: "Failed to link account",
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}

@Serializable
data class LinkAccountState(
    val accountId: String = "",
    val selectedType: PocketAccountType = PocketAccountType.SAVINGS,
    val isLoading: Boolean = false,
    @kotlinx.serialization.Transient
    val error: String? = null,
)

sealed interface LinkAccountEvent {
    data object NavigateBack : LinkAccountEvent
    data object LinkSuccess : LinkAccountEvent
}

sealed interface LinkAccountAction {
    data object NavigateBack : LinkAccountAction
    data object Submit : LinkAccountAction
    data class OnAccountIdChanged(val accountId: String) : LinkAccountAction
    data class OnAccountTypeSelected(val type: PocketAccountType) : LinkAccountAction
}
