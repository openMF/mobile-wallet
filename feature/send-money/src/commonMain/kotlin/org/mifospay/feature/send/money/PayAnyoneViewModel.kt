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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable
import org.mifospay.core.common.getSerialized
import org.mifospay.core.common.setSerialized
import org.mifospay.core.ui.utils.BaseViewModel

class PayAnyoneViewModel(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<PayAnyoneState, PayAnyoneEvent, PayAnyoneAction>(
    initialState = savedStateHandle.getSerialized(KEY_STATE) ?: PayAnyoneState(),
) {

    companion object {
        private const val KEY_STATE = "pay_anyone_state"
    }

    init {
        stateFlow
            .onEach { savedStateHandle.setSerialized(key = KEY_STATE, value = it) }
            .launchIn(viewModelScope)
    }

    override fun handleAction(action: PayAnyoneAction) {
        when (action) {
            is PayAnyoneAction.InputValueChanged -> {
                mutableStateFlow.update {
                    it.copy(
                        inputValue = action.value,
                        showClearIcon = action.value.isNotEmpty(),
                    )
                }
            }

            is PayAnyoneAction.PhoneNumberSelected -> {
                mutableStateFlow.update {
                    it.copy(
                        inputValue = action.phoneNumber,
                        showClearIcon = true,
                    )
                }
            }

            PayAnyoneAction.ClearInput -> {
                mutableStateFlow.update {
                    it.copy(
                        inputValue = "",
                        showClearIcon = false,
                    )
                }
            }

            PayAnyoneAction.ToggleKeyboardType -> {
                mutableStateFlow.update {
                    it.copy(
                        isKeyboardNumeric = !it.isKeyboardNumeric,
                    )
                }
            }
        }
    }
}

@Serializable
data class PayAnyoneState(
    val inputValue: String = "",
    val isKeyboardNumeric: Boolean = false,
    val showClearIcon: Boolean = false,
    val recentContacts: List<Contact> = sampleRecentContacts,
    val allContacts: List<Contact> = sampleAllContacts,
)

private val sampleRecentContacts = listOf(
    Contact(
        id = "1",
        name = "John Doe",
        phoneNumber = "+91 98765 43210",
        upiId = "john.doe@upi",
    ),
    Contact(
        id = "2",
        name = "Jane Smith",
        phoneNumber = "+91 98765 43211",
        upiId = "jane.smith@okicici",
    ),
    Contact(
        id = "3",
        name = "Mike Johnson",
        phoneNumber = "+91 98765 43212",
        upiId = "mike.johnson@paytm",
    ),
    Contact(
        id = "4",
        name = "Sarah Wilson",
        phoneNumber = "+91 98765 43213",
        upiId = "sarah.wilson@phonepe",
    ),
)

private val sampleAllContacts = listOf(
    Contact(
        id = "5",
        name = "Alice Brown",
        phoneNumber = "+91 98765 43214",
        upiId = "alice.brown@upi",
    ),
    Contact(
        id = "6",
        name = "Bob Davis",
        phoneNumber = "+91 98765 43215",
        upiId = "bob.davis@okicici",
    ),
    Contact(
        id = "7",
        name = "Carol Miller",
        phoneNumber = "+91 98765 43216",
        upiId = "carol.miller@paytm",
    ),
    Contact(
        id = "8",
        name = "David Garcia",
        phoneNumber = "+91 98765 43217",
        upiId = "david.garcia@phonepe",
    ),
    Contact(
        id = "9",
        name = "Emma Rodriguez",
        phoneNumber = "+91 98765 43218",
        upiId = "emma.rodriguez@upi",
    ),
    Contact(
        id = "10",
        name = "Frank Martinez",
        phoneNumber = "+91 98765 43219",
        upiId = "frank.martinez@okicici",
    ),
    Contact(
        id = "11",
        name = "Grace Lee",
        phoneNumber = "+91 98765 43220",
        upiId = "grace.lee@paytm",
    ),
    Contact(
        id = "12",
        name = "Henry Taylor",
        phoneNumber = "+91 98765 43221",
        upiId = "henry.taylor@phonepe",
    ),
)

sealed interface PayAnyoneEvent {
    data object NavigateBack : PayAnyoneEvent
    data object NavigateToContactPicker : PayAnyoneEvent
}

sealed interface PayAnyoneAction {
    data class InputValueChanged(val value: String) : PayAnyoneAction
    data class PhoneNumberSelected(val phoneNumber: String) : PayAnyoneAction
    data object ClearInput : PayAnyoneAction
    data object ToggleKeyboardType : PayAnyoneAction
}
