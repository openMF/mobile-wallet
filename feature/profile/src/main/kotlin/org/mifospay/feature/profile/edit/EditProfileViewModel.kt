/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.profile.edit

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.mifospay.core.model.domain.user.UpdateUserEntityEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.client.UpdateClient
import org.mifospay.core.data.domain.usecase.user.UpdateUser
import org.mifospay.core.datastore.PreferencesHelper
import org.mifospay.feature.profile.edit.EditProfileUiState.Loading
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val mPreferencesHelper: PreferencesHelper,
    private val updateUserUseCase: UpdateUser,
    private val updateClientUseCase: UpdateClient,
) : ViewModel() {

    private val mEditProfileUiState = MutableStateFlow<EditProfileUiState>(Loading)
    val editProfileUiState: StateFlow<EditProfileUiState> = mEditProfileUiState

    private val mUpdateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = mUpdateSuccess

    init {
        fetchProfileDetails()
    }

    private fun fetchProfileDetails() {
        val name = mPreferencesHelper.fullName ?: "-"
        val username = mPreferencesHelper.username
        val email = mPreferencesHelper.email ?: "-"
        val vpa = mPreferencesHelper.clientVpa ?: "-"
        val mobile = mPreferencesHelper.mobile ?: "-"

        mEditProfileUiState.value = EditProfileUiState.Success(
            name = name,
            username = username,
            email = email,
            vpa = vpa,
            mobile = mobile,
        )
    }

    fun updateEmail(email: String?) {
        mUseCaseHandler.execute(
            updateUserUseCase,
            UpdateUser.RequestValues(
                UpdateUserEntityEmail(
                    email,
                ),
                mPreferencesHelper.userId.toInt(),
            ),
            object : UseCase.UseCaseCallback<UpdateUser.ResponseValue?> {
                override fun onSuccess(response: UpdateUser.ResponseValue?) {
                    mPreferencesHelper.saveEmail(email)
                    mEditProfileUiState.value = EditProfileUiState.Success(email = email!!)
                    mUpdateSuccess.value = true
                }

                override fun onError(message: String) {
                    mUpdateSuccess.value = false
                }
            },
        )
    }

    fun updateMobile(fullNumber: String?) {
        mUseCaseHandler.execute(
            updateClientUseCase,
            UpdateClient.RequestValues(
                com.mifospay.core.model.domain.client.UpdateClientEntityMobile(
                    fullNumber!!,
                ),
                mPreferencesHelper.clientId.toInt().toLong(),
            ),
            object : UseCase.UseCaseCallback<UpdateClient.ResponseValue> {
                override fun onSuccess(response: UpdateClient.ResponseValue) {
                    mPreferencesHelper.saveMobile(fullNumber)
                    mEditProfileUiState.value = EditProfileUiState.Success(mobile = fullNumber)
                    mUpdateSuccess.value = true
                }

                override fun onError(message: String) {
                    mUpdateSuccess.value = false
                }
            },
        )
    }
}

sealed interface EditProfileUiState {
    data object Loading : EditProfileUiState
    data class Success(
        val bitmapImage: Bitmap? = null,
        val name: String = "",
        var username: String = "",
        val email: String = "",
        val vpa: String = "",
        val mobile: String = "",
    ) : EditProfileUiState
}
