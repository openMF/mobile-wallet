package org.mifospay.kyc.presenter

import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.kyc.UploadKYCDocs
import org.mifospay.common.Constants
import org.mifospay.core.datastore.PreferencesHelper
import java.io.File
import javax.inject.Inject

@HiltViewModel
class KYCLevel2ViewModel @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val preferencesHelper: PreferencesHelper,
    private val uploadKYCDocsUseCase: UploadKYCDocs
) : ViewModel() {

    private val _kyc2uiState =
        MutableStateFlow<KYCLevel2UiState>(KYCLevel2UiState.Loading)
    val kyc2uiState: StateFlow<KYCLevel2UiState> = _kyc2uiState

    fun uploadKYCDocs(identityType: String, result: Uri) {
        val file = result.path?.let { File(it) }
        if (file != null) {
            uploadKYCDocsUseCase.walletRequestValues = identityType.let {
                UploadKYCDocs.RequestValues(
                    org.mifospay.core.data.util.Constants.ENTITY_TYPE_CLIENTS,
                    preferencesHelper.clientId, file.name, it,
                    getRequestFileBody(file)
                )
            }
        }
        val requestValues = uploadKYCDocsUseCase.walletRequestValues
        mUseCaseHandler.execute(uploadKYCDocsUseCase, requestValues,
            object : UseCase.UseCaseCallback<UploadKYCDocs.ResponseValue> {
                override fun onSuccess(response: UploadKYCDocs.ResponseValue) {
                    _kyc2uiState.value = KYCLevel2UiState.Success
                }

                override fun onError(message: String) {
                    _kyc2uiState.value = KYCLevel2UiState.Error
                }
            })
    }

    private fun getRequestFileBody(file: File): MultipartBody.Part {
        // create RequestBody instance from file
        val requestFile = file.asRequestBody(Constants.MULTIPART_FORM_DATA.toMediaTypeOrNull())

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(Constants.FILE, file.name, requestFile)
    }
}

sealed interface KYCLevel2UiState {
    data object Loading : KYCLevel2UiState
    data object Success : KYCLevel2UiState
    data object Error : KYCLevel2UiState
}