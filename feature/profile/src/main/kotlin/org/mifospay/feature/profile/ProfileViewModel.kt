/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.mifospay.common.DebugUtil
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.client.FetchClientImage
import org.mifospay.core.data.repository.local.LocalRepository
import org.mifospay.core.datastore.PreferencesHelper

class ProfileViewModel(
    private val mUseCaseHandler: UseCaseHandler,
    private val fetchClientImageUseCase: FetchClientImage,
    private val localRepository: LocalRepository,
    private val mPreferencesHelper: PreferencesHelper,
) : ViewModel() {

    private val mProfileState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val profileState: StateFlow<ProfileUiState> get() = mProfileState

    init {
        fetchClientImage()
        fetchProfileDetails()
    }

    private fun fetchClientImage() {
        viewModelScope.launch {
            mUseCaseHandler.execute(
                fetchClientImageUseCase,
                FetchClientImage.RequestValues(localRepository.clientDetails.clientId),
                object : UseCase.UseCaseCallback<FetchClientImage.ResponseValue> {
                    override fun onSuccess(response: FetchClientImage.ResponseValue) {
                        val bitmap = convertResponseToBitmap(response.responseBody)
                        val currentState = mProfileState.value as ProfileUiState.Success
                        mProfileState.value = currentState.copy(bitmapImage = bitmap)
                    }

                    override fun onError(message: String) {
                        DebugUtil.log("image", message)
                    }
                },
            )
        }
    }

    private fun fetchProfileDetails() {
        val name = mPreferencesHelper.fullName ?: "-"
        val email = mPreferencesHelper.email ?: "-"
        val vpa = mPreferencesHelper.clientVpa ?: "-"
        val mobile = mPreferencesHelper.mobile ?: "-"

        mProfileState.value = ProfileUiState.Success(
            name = name,
            email = email,
            vpa = vpa,
            mobile = mobile,
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
        val mobile: String?,
    ) : ProfileUiState()
}
