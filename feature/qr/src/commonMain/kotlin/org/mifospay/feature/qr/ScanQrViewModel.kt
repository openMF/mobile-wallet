/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.qr

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import org.mifospay.core.data.util.UpiQrCodeProcessor
import org.mifospay.core.model.beneficiary.Beneficiary

class ScanQrViewModel : ViewModel() {

    private val _eventFlow = MutableStateFlow<ScanQrEvent?>(null)
    val eventFlow = _eventFlow.asSharedFlow()

    fun onScanned(data: String): Boolean {
        return try {
            UpiQrCodeProcessor.decodeUpiString(data)

            _eventFlow.update {
                ScanQrEvent.OnNavigateToSendScreen(data)
            }

            true
        } catch (e: Exception) {
            getQrCodeResult(data)
        }
    }

    private fun getQrCodeResult(data: String): Boolean {
        val trimmedData = data.trim()
        println("scanned $data")

        if (!trimmedData.startsWith("{") || !trimmedData.endsWith("}")) {
            _eventFlow.update {
                ScanQrEvent.ShowToast("Scan a Valid QR Code")
            }
            return false
        }

        return try {
            val beneficiary = parseBeneficiaryFromJson(trimmedData)
            if (beneficiary != null) {
                val beneficiaryString = Json.encodeToString<Beneficiary>(beneficiary)
                _eventFlow.update {
                    ScanQrEvent.OnNavigateToAddBeneficiary(beneficiaryString)
                }
                true
            } else {
                _eventFlow.update {
                    ScanQrEvent.ShowToast("Scan a Valid QR Code")
                }
                false
            }
        } catch (_: Exception) {
            _eventFlow.update {
                ScanQrEvent.ShowToast("Scan a Valid QR Code")
            }
            false
        }
    }

    private fun parseBeneficiaryFromJson(jsonString: String): Beneficiary? {
        return try {
            Json.decodeFromString<Beneficiary>(jsonString)
        } catch (e: Exception) {
            null
        }
    }
}

sealed interface ScanQrEvent {
    data class OnNavigateToSendScreen(val data: String) : ScanQrEvent
    data class OnNavigateToAddBeneficiary(val beneficiary: String) : ScanQrEvent
    data class ShowToast(val message: String) : ScanQrEvent
}
