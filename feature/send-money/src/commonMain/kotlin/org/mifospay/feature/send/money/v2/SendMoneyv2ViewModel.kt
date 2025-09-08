/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package v2

import androidx.lifecycle.SavedStateHandle
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import org.mifospay.core.common.StringResourceSerializer
import org.mifospay.core.data.repository.AccountRepository
import org.mifospay.core.model.search.AccountResult
import org.mifospay.core.ui.utils.BaseViewModel

class SendMoneyV2ViewModel(
    repository: AccountRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<SendMoneyV2State, SendMoneyV2Event, SendMoneyV2Action>(
    initialState = SendMoneyV2State(),
) {

    override fun handleAction(action: SendMoneyV2Action) {
        when (action) {
            SendMoneyV2Action.NavigateBack -> {
                sendEvent(SendMoneyV2Event.NavigateBack)
            }

            SendMoneyV2Action.OnSearchBarClicked -> {
                sendEvent(SendMoneyV2Event.NavigateToSearchAccountSelection)
            }
        }
    }
}

@Serializable
data class SendMoneyV2State(
    val amount: String = "",
    val accountNumber: String = "",
    val selectedAccount: AccountResult? = null,
    val dialogState: DialogState? = null,
) {

    @Serializable
    sealed interface DialogState {
        @Serializable
        data object Loading : DialogState

        @Serializable
        sealed class Error : DialogState {
            @Serializable
            data class ResourceMessage(
                @Serializable(with = StringResourceSerializer::class)
                val message: StringResource,
            ) : Error()

            @Serializable
            data class GenericResourceMessage(
                @Serializable(with = StringResourceSerializer::class)
                val message: StringResource,
                val args: List<String>,
            ) : Error()
        }
    }
}

sealed interface ViewState {
    data object Loading : ViewState
    data class Error(val message: String) : ViewState
    data object Empty : ViewState
    data object InitialEmpty : ViewState
    data class Content(val data: List<AccountResult>) : ViewState
}

sealed interface SendMoneyV2Event {
    data object NavigateToSearchAccountSelection : SendMoneyV2Event
    data object NavigateBack : SendMoneyV2Event
}

sealed interface SendMoneyV2Action {
    data object NavigateBack : SendMoneyV2Action

    data object OnSearchBarClicked : SendMoneyV2Action
}
