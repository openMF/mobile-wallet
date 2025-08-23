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
    initialState = BankTransferState(
        selectedFromBankAccount = BankTransferState().bankAccounts.first { it.isDefault },
        selectedToBankAccount = BankTransferState().bankAccounts.first { it.isDefault },
        isFromBankAccountDropdownExpanded = true,
    ),
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
                sendEvent(BankTransferEvent.ShowIfscSearch)
            }
            is BankTransferAction.Continue -> {
                if (state.isFormValid) {
                    sendEvent(BankTransferEvent.NavigateToNext)
                }
            }
            is BankTransferAction.ContinueSelfTransfer -> {
                if (state.isSelfTransferValid) {
                    sendEvent(BankTransferEvent.NavigateToNext)
                }
            }
            is BankTransferAction.ToggleFromBankAccountDropdown -> {
                mutableStateFlow.update {
                    it.copy(
                        isFromBankAccountDropdownExpanded = action.expanded,
                        isToBankAccountDropdownExpanded = false,
                    )
                }
            }
            is BankTransferAction.ToggleToBankAccountDropdown -> {
                mutableStateFlow.update {
                    it.copy(
                        isToBankAccountDropdownExpanded = action.expanded,
                        isFromBankAccountDropdownExpanded = false,
                    )
                }
            }
            is BankTransferAction.SelectFromBankAccount -> {
                mutableStateFlow.update {
                    it.copy(
                        selectedFromBankAccount = action.bankAccount,
                        isFromBankAccountDropdownExpanded = false,
                        isToBankAccountDropdownExpanded = true,
                    )
                }
            }
            is BankTransferAction.SelectToBankAccount -> {
                mutableStateFlow.update {
                    it.copy(
                        selectedToBankAccount = action.bankAccount,
                        isToBankAccountDropdownExpanded = false,
                    )
                }
            }
            is BankTransferAction.AddBankAccount -> {
                sendEvent(BankTransferEvent.AddBankAccount)
            }
            is BankTransferAction.SelectIfscCode -> {
                mutableStateFlow.update { it.copy(ifscCode = action.ifscCode.code) }
                sendEvent(BankTransferEvent.IfscCodeSelected(action.ifscCode))
            }
        }
    }
}

data class BankAccount(
    val id: String,
    val bankName: String,
    val accountNumber: String,
    val accountType: String,
    val isDefault: Boolean = false,
) {
    val maskedAccountNumber: String
        get() = "****${accountNumber.takeLast(4)}"
}

data class BankTransferState(
    val isLoading: Boolean = false,
    val accountNumber: String = "",
    val ifscCode: String = "",
    val selectedFromBankAccount: BankAccount? = null,
    val selectedToBankAccount: BankAccount? = null,
    val isFromBankAccountDropdownExpanded: Boolean = false,
    val isToBankAccountDropdownExpanded: Boolean = false,
) {
    val isAccountNumberValid: Boolean
        get() = accountNumber.isNotEmpty() && accountNumber.all { it.isDigit() }

    val isIfscCodeValid: Boolean
        get() = ifscCode.isNotEmpty()

    val isFormValid: Boolean
        get() = isAccountNumberValid && isIfscCodeValid

    val isSelfTransferValid: Boolean
        get() = selectedFromBankAccount != null &&
            selectedToBankAccount != null &&
            selectedFromBankAccount.id != selectedToBankAccount.id

    val bankAccounts: List<BankAccount> = listOf(
        BankAccount(
            id = "1",
            bankName = "State Bank of India",
            accountNumber = "1234567890",
            accountType = "Savings Account",
            isDefault = true,
        ),
        BankAccount(
            id = "2",
            bankName = "HDFC Bank",
            accountNumber = "0987654321",
            accountType = "Current Account",
        ),
    )
}

sealed interface BankTransferEvent {
    data object NavigateBack : BankTransferEvent
    data object ShowIfscSearch : BankTransferEvent
    data object NavigateToNext : BankTransferEvent
    data object AddBankAccount : BankTransferEvent
    data class IfscCodeSelected(val ifscCode: IfscCode) : BankTransferEvent
}

sealed interface BankTransferAction {
    data object NavigateBack : BankTransferAction
    data class UpdateAccountNumber(val accountNumber: String) : BankTransferAction
    data class UpdateIfscCode(val ifscCode: String) : BankTransferAction
    data object SearchIfsc : BankTransferAction
    data object Continue : BankTransferAction
    data object ContinueSelfTransfer : BankTransferAction
    data class ToggleFromBankAccountDropdown(val expanded: Boolean) : BankTransferAction
    data class ToggleToBankAccountDropdown(val expanded: Boolean) : BankTransferAction
    data class SelectFromBankAccount(val bankAccount: BankAccount) : BankTransferAction
    data class SelectToBankAccount(val bankAccount: BankAccount) : BankTransferAction
    data object AddBankAccount : BankTransferAction
    data class SelectIfscCode(val ifscCode: IfscCode) : BankTransferAction
}
