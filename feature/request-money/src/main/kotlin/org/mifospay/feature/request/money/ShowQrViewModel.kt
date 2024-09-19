/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.request.money

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.datastore.PreferencesHelper
import org.mifospay.feature.request.money.ShowQrUiState.Loading

class ShowQrViewModel (
    private val mUseCaseHandler: UseCaseHandler,
    private val generateQrUseCase: GenerateQr,
    private val mPreferencesHelper: PreferencesHelper,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val vpaId = savedStateHandle.getStateFlow("vpa", "")

    private val mShowQrUiState: MutableStateFlow<ShowQrUiState> = MutableStateFlow(Loading)
    val showQrUiState get() = mShowQrUiState

    init {
        savedStateHandle.get<String>("vpa")?.let {
            generateQr()
        }
    }

    fun generateQr(requestQrData: RequestQrData? = null) {
        val requestQr = (requestQrData ?: RequestQrData()).copy(
            name = mPreferencesHelper.fullName ?: "",
            vpaId = vpaId.value,
        )

        mUseCaseHandler.execute(
            generateQrUseCase,
            GenerateQr.RequestValues(requestQr),
            object : UseCase.UseCaseCallback<GenerateQr.ResponseValue?> {
                override fun onSuccess(response: GenerateQr.ResponseValue?) {
                    mShowQrUiState.value = ShowQrUiState.Success(response?.bitmap)
                }

                override fun onError(message: String) {
                    mShowQrUiState.value = ShowQrUiState.Error
                }
            },
        )
    }
}

data class RequestQrData(
    val amount: String = "",
    val vpaId: String = "",
    val currency: String = "USD",
    val name: String = "",
)

sealed class ShowQrUiState {
    data class Success(val qrDataBitmap: Bitmap?) : ShowQrUiState()
    data object Error : ShowQrUiState()
    data object Loading : ShowQrUiState()
}
