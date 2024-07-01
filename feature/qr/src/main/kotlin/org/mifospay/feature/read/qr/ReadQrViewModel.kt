package org.mifospay.feature.read.qr

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.feature.read.qr.utils.ScanQr
import javax.inject.Inject

@HiltViewModel
class ReadQrViewModel @Inject constructor(
    private val useCaseHandler: UseCaseHandler,
    private val scanQrUseCase: ScanQr
) : ViewModel() {

    private val _readQrUiState = MutableStateFlow<ReadQrUiState>(ReadQrUiState.Loading)
    val readQrUiState = _readQrUiState.asStateFlow()

    fun scanQr(bitmap: Bitmap) {
        useCaseHandler.execute(
            scanQrUseCase, ScanQr.RequestValues(bitmap),
            object : UseCase.UseCaseCallback<ScanQr.ResponseValue?> {
                override fun onSuccess(response: ScanQr.ResponseValue?) {
                    _readQrUiState.update { ReadQrUiState.Success(response?.result) }
                }

                override fun onError(message: String) {
                    _readQrUiState.update { ReadQrUiState.Error }
                }
            })
    }
}

sealed class ReadQrUiState {
    data class Success(val qrData: String?) : ReadQrUiState()
    data object Error : ReadQrUiState()
    data object Loading : ReadQrUiState()
}