package org.mifospay.editprofile.presenter

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.mifospay.core.model.domain.user.UpdateUserEntityEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.ResponseBody.Companion.toResponseBody
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.client.UpdateClient
import org.mifospay.core.data.domain.usecase.user.UpdateUser
import org.mifospay.core.datastore.PreferencesHelper
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val mPreferencesHelper: PreferencesHelper,
    private val updateUserUseCase: UpdateUser,
    private val updateClientUseCase: UpdateClient,
) : ViewModel() {

    private val _editProfileUiState =
        MutableStateFlow<EditProfileUiState>(EditProfileUiState.Loading)
    val editProfileUiState: StateFlow<EditProfileUiState> = _editProfileUiState

    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess

    fun fetchProfileDetails() {
        val name = mPreferencesHelper.fullName ?: "-"
        val username = mPreferencesHelper.username
        val email = mPreferencesHelper.email ?: "-"
        val vpa = mPreferencesHelper.clientVpa ?: "-"
        val mobile = mPreferencesHelper.mobile ?: "-"

        _editProfileUiState.value = EditProfileUiState.Success(
            name = name,
            username = username,
            email = email,
            vpa = vpa,
            mobile = mobile
        )
    }

    fun updateEmail(email: String?) {
        _editProfileUiState.value = EditProfileUiState.Loading
        mUseCaseHandler.execute(updateUserUseCase,
            UpdateUser.RequestValues(
                UpdateUserEntityEmail(
                    email
                ),
                mPreferencesHelper.userId.toInt()
            ),
            object : UseCase.UseCaseCallback<UpdateUser.ResponseValue?> {
                override fun onSuccess(response: UpdateUser.ResponseValue?) {
                    mPreferencesHelper.saveEmail(email)
                    _editProfileUiState.value = EditProfileUiState.Success(email = email!!)
                    _updateSuccess.value = true
                }

                override fun onError(message: String) {
                    _editProfileUiState.value = EditProfileUiState.Error(message)
                    _updateSuccess.value = false
                }
            })
    }

    fun updateMobile(fullNumber: String?) {
        _editProfileUiState.value = EditProfileUiState.Loading
        mUseCaseHandler.execute(updateClientUseCase,
            UpdateClient.RequestValues(
                com.mifospay.core.model.domain.client.UpdateClientEntityMobile(
                    fullNumber!!
                ),
                mPreferencesHelper.clientId.toInt().toLong()
            ),
            object : UseCase.UseCaseCallback<UpdateClient.ResponseValue> {
                override fun onSuccess(response: UpdateClient.ResponseValue) {
                    mPreferencesHelper.saveMobile(fullNumber)
                    _editProfileUiState.value = EditProfileUiState.Success(mobile = fullNumber)
                    _updateSuccess.value = true
                }

                override fun onError(message: String) {
                    _editProfileUiState.value = EditProfileUiState.Error(message)
                    _updateSuccess.value = false
                }
            })
    }

}

sealed interface EditProfileUiState {
    data object Loading : EditProfileUiState
    data class Error(val message: String) : EditProfileUiState
    data class Success(
        val bitmapImage: Bitmap? = null,
        val name: String = "",
        var username: String = "",
        val email: String = "",
        val vpa: String = "",
        val mobile: String = ""
    ) : EditProfileUiState
}