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

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.mifospay.core.ui.utils.BaseViewModel

class SendMoneyOptionsViewModel(
    private val scanner: QrScanner,
) : BaseViewModel<SendMoneyOptionsState, SendMoneyOptionsEvent, SendMoneyOptionsAction>(
    initialState = SendMoneyOptionsState(),
) {

    override fun handleAction(action: SendMoneyOptionsAction) {
        when (action) {
            is SendMoneyOptionsAction.NavigateBack -> {
                sendEvent(SendMoneyOptionsEvent.NavigateBack)
            }
            is SendMoneyOptionsAction.ScanQrClicked -> {
                // Use ML Kit QR scanner directly
                scanner.startScanning().onEach { data ->
                    data?.let { result ->
                        sendEvent(SendMoneyOptionsEvent.QrCodeScanned(result))
                    }
                }.launchIn(viewModelScope)
            }
            is SendMoneyOptionsAction.PayAnyoneClicked -> {
                sendEvent(SendMoneyOptionsEvent.NavigateToPayAnyone)
            }
            is SendMoneyOptionsAction.BankTransferClicked -> {
                sendEvent(SendMoneyOptionsEvent.NavigateToBankTransfer)
            }
            is SendMoneyOptionsAction.FineractPaymentsClicked -> {
                sendEvent(SendMoneyOptionsEvent.NavigateToFineractPayments)
            }
        }
    }
}

data class SendMoneyOptionsState(
    val isLoading: Boolean = false,
)

sealed interface SendMoneyOptionsEvent {
    data object NavigateBack : SendMoneyOptionsEvent
    data object NavigateToPayAnyone : SendMoneyOptionsEvent
    data object NavigateToBankTransfer : SendMoneyOptionsEvent
    data object NavigateToFineractPayments : SendMoneyOptionsEvent
    data class QrCodeScanned(val data: String) : SendMoneyOptionsEvent
}

sealed interface SendMoneyOptionsAction {
    data object NavigateBack : SendMoneyOptionsAction
    data object ScanQrClicked : SendMoneyOptionsAction
    data object PayAnyoneClicked : SendMoneyOptionsAction
    data object BankTransferClicked : SendMoneyOptionsAction
    data object FineractPaymentsClicked : SendMoneyOptionsAction
}
