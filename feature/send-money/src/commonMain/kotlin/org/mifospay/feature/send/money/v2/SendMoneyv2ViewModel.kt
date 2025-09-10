/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.send.money.v2

import kotlinx.serialization.Serializable
import org.mifospay.core.ui.utils.BaseViewModel

class SendMoneyV2ViewModel() : BaseViewModel<SendMoneyV2State, SendMoneyV2Event, SendMoneyV2Action>(
    initialState = SendMoneyV2State,
) {

    override fun handleAction(action: SendMoneyV2Action) {
        when (action) {
            SendMoneyV2Action.NavigateBack -> {
                sendEvent(SendMoneyV2Event.NavigateBack)
            }

            SendMoneyV2Action.OnSearchBarClicked -> {
                sendEvent(SendMoneyV2Event.NavigateToSearchAccountSelection)
            }

            SendMoneyV2Action.OnAddPayeeClicked -> {
                sendEvent(SendMoneyV2Event.NavigateToBeneficiary)
            }
        }
    }
}

@Serializable
data object SendMoneyV2State

sealed interface SendMoneyV2Event {
    data object NavigateToSearchAccountSelection : SendMoneyV2Event
    data object NavigateBack : SendMoneyV2Event
    data object NavigateToBeneficiary : SendMoneyV2Event
}

sealed interface SendMoneyV2Action {
    data object NavigateBack : SendMoneyV2Action

    data object OnSearchBarClicked : SendMoneyV2Action

    data object OnAddPayeeClicked : SendMoneyV2Action
}
