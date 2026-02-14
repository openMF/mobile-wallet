/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.mpay.qr.scan

import androidx.lifecycle.ViewModel
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import org.mifospay.core.data.util.MpayQrCodeProcessor
import org.mifospay.core.model.beneficiary.Beneficiary
import org.mifospay.core.model.utils.QrCodeType
import template.core.base.platform.PlatformBuildConfig

class ScanQrViewModel : ViewModel() {

    private val _eventFlow = MutableStateFlow<ScanQrEvent?>(null)
    val eventFlow = _eventFlow.asSharedFlow()

    private val _isTorchEnabled = MutableStateFlow(false)
    val isTorchEnabled: StateFlow<Boolean> = _isTorchEnabled.asStateFlow()

    private val _showHelpDialog = MutableStateFlow(false)
    val showHelpDialog: StateFlow<Boolean> = _showHelpDialog.asStateFlow()

    private val _isProcessingImage = MutableStateFlow(false)
    val isProcessingImage: StateFlow<Boolean> = _isProcessingImage.asStateFlow()

    private val _selectedImageBytes = MutableStateFlow<ByteArray?>(null)
    val selectedImageBytes: StateFlow<ByteArray?> = _selectedImageBytes.asStateFlow()

    fun toggleTorch() {
        _isTorchEnabled.update { !it }
    }

    fun setTorchEnabled(enabled: Boolean) {
        _isTorchEnabled.value = enabled
    }

    fun showHelpDialog() {
        _showHelpDialog.value = true
    }

    fun hideHelpDialog() {
        _showHelpDialog.value = false
    }

    fun setProcessingImage(processing: Boolean) {
        _isProcessingImage.value = processing
    }

    fun setSelectedImageBytes(bytes: ByteArray?) {
        _selectedImageBytes.value = bytes
    }

    fun clearSelectedImage() {
        _selectedImageBytes.value = null
        _isProcessingImage.value = false
    }

    fun onImageQrScanned(data: String?) {
        _isProcessingImage.value = false
        if (data != null) {
            _eventFlow.update {
                ScanQrEvent.OnScanSuccess
            }
            onScanned(data)
        } else {
            _selectedImageBytes.value = null
            _eventFlow.update {
                ScanQrEvent.OnNoQrFound
            }
        }
    }

    fun onScanned(data: String): Boolean {
        return try {
            val qrCodeData = MpayQrCodeProcessor.decodeMpayString(data)

            _eventFlow.update {
                ScanQrEvent.OnScanSuccess
            }

            // Navigate based on QR type
            when (qrCodeData.type) {
                QrCodeType.INTER_BANK -> {
                    _eventFlow.update {
                        ScanQrEvent.OnNavigateToInterbankTransfer(
                            phoneNumber = qrCodeData.accountExternalId ?: "",
                            recipientName = qrCodeData.clientName,
                            amount = qrCodeData.amount,
                        )
                    }
                }

                else -> {
                    _eventFlow.update {
                        ScanQrEvent.OnNavigateToSendScreen(data)
                    }
                }
            }

            true
        } catch (e: Exception) {
            getQrCodeResult(data)
        }
    }

    private fun getQrCodeResult(data: String): Boolean {
        val trimmedData = data.trim()
        if (PlatformBuildConfig.isDebug) {
            Logger.d { "QR scanned: $data" }
        }

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
                    ScanQrEvent.OnScanSuccess
                }
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
    data class OnNavigateToInterbankTransfer(
        val phoneNumber: String,
        val recipientName: String,
        val amount: String,
    ) : ScanQrEvent
    data class OnNavigateToAddBeneficiary(val beneficiary: String) : ScanQrEvent
    data class ShowToast(val message: String) : ScanQrEvent
    data object OnScanSuccess : ScanQrEvent
    data object OnNoQrFound : ScanQrEvent
}
