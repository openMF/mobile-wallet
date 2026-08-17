/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-pay/blob/master/LICENSE.md
 */
package org.mifospay.feature.qr

import org.mifospay.core.data.util.StandardUpiQrCodeProcessor
import org.mifospay.core.data.util.UpiQrCodeProcessor
import org.mifospay.core.ui.utils.BaseViewModel

class ScanQrViewModel : BaseViewModel<ScanQrState, ScanQrEvent, ScanQrAction>(
    initialState = ScanQrState,
) {

    override fun handleAction(action: ScanQrAction) {
        when (action) {
            is ScanQrAction.NavigateToSendScreen -> {
                sendEvent(ScanQrEvent.OnNavigateToSendScreen(action.data))
            }

            is ScanQrAction.NavigateToPayeeDetails -> {
                sendEvent(ScanQrEvent.OnNavigateToPayeeDetails(action.data))
            }

            is ScanQrAction.ShowInvalidQrToast -> {
                sendEvent(ScanQrEvent.ShowToast("Scan a Valid Payment QR Code"))
            }
        }
    }

    /**
     * Decodes the scanned [data] synchronously (the QR scanner needs the [Boolean]
     * return to decide whether to stop scanning) and routes the resulting one-shot
     * navigation/toast signal through the MVI action channel.
     */
    fun onScanned(data: String): Boolean {
        return try {
            val isUpiQr = try {
                UpiQrCodeProcessor.decodeUpiString(data)
                true
            } catch (e: Exception) {
                if (StandardUpiQrCodeProcessor.isValidUpiQrCode(data)) {
                    StandardUpiQrCodeProcessor.parseUpiQrCode(data)
                    true
                } else {
                    false
                }
            }

            trySendAction(
                if (isUpiQr) {
                    ScanQrAction.NavigateToPayeeDetails(data)
                } else {
                    ScanQrAction.NavigateToSendScreen(data)
                },
            )

            true
        } catch (e: Exception) {
            trySendAction(ScanQrAction.ShowInvalidQrToast)
            false
        }
    }
}

data object ScanQrState

sealed interface ScanQrEvent {
    data class OnNavigateToSendScreen(val data: String) : ScanQrEvent
    data class OnNavigateToPayeeDetails(val data: String) : ScanQrEvent
    data class ShowToast(val message: String) : ScanQrEvent
}

sealed interface ScanQrAction {
    data class NavigateToSendScreen(val data: String) : ScanQrAction
    data class NavigateToPayeeDetails(val data: String) : ScanQrAction
    data object ShowInvalidQrToast : ScanQrAction
}
