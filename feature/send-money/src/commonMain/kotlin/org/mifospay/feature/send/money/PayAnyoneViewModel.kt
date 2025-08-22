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

            is PayAnyoneAction.ContactSelected -> {
                println("PayAnyoneViewModel: Contact selected - ${action.contact.phoneNumber}")
                mutableStateFlow.update {
                    it.copy(
                        selectedContact = action.contact,
                        inputValue = action.contact.phoneNumber,
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
    val selectedContact: Contact? = null,
)

sealed interface PayAnyoneEvent {
    data object NavigateBack : PayAnyoneEvent
    data object NavigateToContactPicker : PayAnyoneEvent
}

sealed interface PayAnyoneAction {
    data class InputValueChanged(val value: String) : PayAnyoneAction
    data class ContactSelected(val contact: Contact) : PayAnyoneAction
    data object ClearInput : PayAnyoneAction
    data object ToggleKeyboardType : PayAnyoneAction
}
