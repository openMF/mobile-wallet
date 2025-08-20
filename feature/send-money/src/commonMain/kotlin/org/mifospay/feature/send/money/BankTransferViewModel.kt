/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.send.money

import kotlinx.coroutines.flow.update
import org.mifospay.core.ui.utils.BaseViewModel

class BankTransferViewModel : BaseViewModel<BankTransferState, BankTransferEvent, BankTransferAction>(
    initialState = BankTransferState(),
) {

    override fun handleAction(action: BankTransferAction) {
        when (action) {
            is BankTransferAction.NavigateBack -> {
                sendEvent(BankTransferEvent.NavigateBack)
            }
            is BankTransferAction.UpdateAccountNumber -> {
                mutableStateFlow.update { it.copy(accountNumber = action.accountNumber) }
            }
            is BankTransferAction.UpdateIfscCode -> {
                mutableStateFlow.update { it.copy(ifscCode = action.ifscCode) }
            }
            is BankTransferAction.SearchIfsc -> {
                // TODO: Implement IFSC search functionality
                sendEvent(BankTransferEvent.ShowIfscSearch)
            }
            is BankTransferAction.Continue -> {
                if (state.isFormValid) {
                    sendEvent(BankTransferEvent.NavigateToNext)
                }
            }
        }
    }
}

data class BankTransferState(
    val isLoading: Boolean = false,
    val accountNumber: String = "",
    val ifscCode: String = "",
) {
    val isAccountNumberValid: Boolean
        get() = accountNumber.isNotEmpty() && accountNumber.all { it.isDigit() }

    val isIfscCodeValid: Boolean
        get() = ifscCode.isNotEmpty()

    val isFormValid: Boolean
        get() = isAccountNumberValid && isIfscCodeValid
}

sealed interface BankTransferEvent {
    data object NavigateBack : BankTransferEvent
    data object ShowIfscSearch : BankTransferEvent
    data object NavigateToNext : BankTransferEvent
}

sealed interface BankTransferAction {
    data object NavigateBack : BankTransferAction
    data class UpdateAccountNumber(val accountNumber: String) : BankTransferAction
    data class UpdateIfscCode(val ifscCode: String) : BankTransferAction
    data object SearchIfsc : BankTransferAction
    data object Continue : BankTransferAction
}
