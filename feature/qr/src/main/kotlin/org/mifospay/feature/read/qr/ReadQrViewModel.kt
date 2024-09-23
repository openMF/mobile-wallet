/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.read.qr

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.feature.read.qr.utils.ScanQr

class ReadQrViewModel(
    private val useCaseHandler: UseCaseHandler,
    private val scanQrUseCase: ScanQr,
) : ViewModel() {

    private val mReadQrUiState = MutableStateFlow<ReadQrUiState>(ReadQrUiState.Loading)
    val readQrUiState = mReadQrUiState.asStateFlow()

    fun scanQr(bitmap: Bitmap) {
        useCaseHandler.execute(
            scanQrUseCase,
            ScanQr.RequestValues(bitmap),
            object : UseCase.UseCaseCallback<ScanQr.ResponseValue?> {
                override fun onSuccess(response: ScanQr.ResponseValue?) {
                    mReadQrUiState.update { ReadQrUiState.Success(response?.result) }
                }

                override fun onError(message: String) {
                    mReadQrUiState.update { ReadQrUiState.Error }
                }
            },
        )
    }
}

sealed class ReadQrUiState {
    data class Success(val qrData: String?) : ReadQrUiState()
    data object Error : ReadQrUiState()
    data object Loading : ReadQrUiState()
}
