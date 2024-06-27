package org.mifospay.feature.request.money

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.datastore.PreferencesHelper
import javax.inject.Inject

@HiltViewModel
class ShowQrViewModel @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val generateQrUseCase: GenerateQr,
    private val mPreferencesHelper: PreferencesHelper
): ViewModel() {

    private val _showQrUiState: MutableStateFlow<ShowQrUiState> = MutableStateFlow(ShowQrUiState.Loading)
    val showQrUiState get() = _showQrUiState

    private val _vpaId: MutableStateFlow<String> = MutableStateFlow("")
    val vpaId get() = _vpaId

    fun generateQr(requestQrData: RequestQrData? = null) {
        val requestQr = (requestQrData ?: RequestQrData()).copy(
            name = mPreferencesHelper.fullName ?: "",
            vpaId = vpaId.value
        )
        mUseCaseHandler.execute(
            generateQrUseCase, GenerateQr.RequestValues(requestQr),
            object : UseCase.UseCaseCallback<GenerateQr.ResponseValue?> {
                override fun onSuccess(response: GenerateQr.ResponseValue?) {
                    _showQrUiState.value = ShowQrUiState.Success(response?.bitmap)
                }

                override fun onError(message: String) {
                    _showQrUiState.value = ShowQrUiState.Error
                }
            })
    }


    fun setQrData(qrData: String?) {
        if (qrData != null) {
            _vpaId.value = qrData
        }
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