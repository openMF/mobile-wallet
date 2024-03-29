package org.mifos.mobilewallet.mifospay.editprofile.presenter

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.mifos.mobilewallet.model.domain.user.UpdateUserEntityEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.usecase.client.UpdateClient
import org.mifos.mobilewallet.core.domain.usecase.user.UpdateUser
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.core.datastore.PreferencesHelper
import org.mifos.mobilewallet.mifospay.home.ProfileUiState
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

    init {
        fetchProfileDetails()
    }

    private fun fetchProfileDetails() {
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
                }

                override fun onError(message: String) {
                    _editProfileUiState.value = EditProfileUiState.Error(message)
                }
            })
    }

    fun updateMobile(fullNumber: String?) {
        _editProfileUiState.value = EditProfileUiState.Loading
        mUseCaseHandler.execute(updateClientUseCase,
            UpdateClient.RequestValues(
                com.mifos.mobilewallet.model.domain.client.UpdateClientEntityMobile(
                    fullNumber!!
                ),
                mPreferencesHelper.clientId.toInt().toLong()
            ),
            object : UseCase.UseCaseCallback<UpdateClient.ResponseValue> {
                override fun onSuccess(response: UpdateClient.ResponseValue) {
                    mPreferencesHelper.saveMobile(fullNumber)
                    _editProfileUiState.value = EditProfileUiState.Success(mobile = fullNumber)
                }

                override fun onError(message: String) {
                    _editProfileUiState.value = EditProfileUiState.Error(message)
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