package org.mifospay.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.client.FetchClientImage
import org.mifospay.core.datastore.PreferencesHelper
import org.mifospay.common.DebugUtil
import org.mifospay.data.local.LocalRepository
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val mUsecaseHandler: UseCaseHandler,
    private val fetchClientImageUseCase: FetchClientImage,
    private val localRepository: LocalRepository,
    private val mPreferencesHelper: PreferencesHelper
) : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val profileState: StateFlow<ProfileUiState> get() = _profileState

    init {
        fetchClientImage()
        fetchProfileDetails()
    }

    private fun fetchClientImage() {
        viewModelScope.launch {
            mUsecaseHandler.execute(fetchClientImageUseCase,
                FetchClientImage.RequestValues(localRepository.clientDetails.clientId),
                object : UseCase.UseCaseCallback<FetchClientImage.ResponseValue> {
                    override fun onSuccess(response: FetchClientImage.ResponseValue) {
                        val bitmap = convertResponseToBitmap(response?.responseBody)
                        val currentState = _profileState.value as ProfileUiState.Success
                        _profileState.value = currentState.copy(bitmapImage = bitmap)
                    }

                    override fun onError(message: String) {
                        DebugUtil.log("image", message)
                    }
                })
        }
    }

    private fun fetchProfileDetails() {
        val name = mPreferencesHelper.fullName ?: "-"
        val email = mPreferencesHelper.email ?: "-"
        val vpa = mPreferencesHelper.clientVpa ?: "-"
        val mobile = mPreferencesHelper.mobile ?: "-"

        _profileState.value = ProfileUiState.Success(
            name = name,
            email = email,
            vpa = vpa,
            mobile = mobile
        )
    }

    private fun convertResponseToBitmap(responseBody: ResponseBody?): Bitmap? {
        return try {
            responseBody?.byteStream()?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            null
        }
    }
}

sealed class ProfileUiState {
    data object Loading : ProfileUiState()
    data class Success(
        val bitmapImage: Bitmap? = null,
        val name: String?,
        val email: String?,
        val vpa: String?,
        val mobile: String?
    ) : ProfileUiState()
}

