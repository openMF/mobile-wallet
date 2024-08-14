/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.kyc

import androidx.lifecycle.ViewModel
import com.mifospay.core.model.entity.kyc.KYCLevel1Details
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.kyc.UploadKYCLevel1Details
import org.mifospay.core.data.repository.local.LocalRepository
import org.mifospay.feature.kyc.KYCLevel1UiState.Loading
import javax.inject.Inject

@HiltViewModel
class KYCLevel1ViewModel @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val mLocalRepository: LocalRepository,
    private val uploadKYCLevel1DetailsUseCase: UploadKYCLevel1Details,
) : ViewModel() {

    private val kycUiState = MutableStateFlow<KYCLevel1UiState>(Loading)
    val kyc1uiState: StateFlow<KYCLevel1UiState> = kycUiState

    fun submitData(kycLevel1Details: KYCLevel1DetailsState) {
        uploadKYCLevel1DetailsUseCase.walletRequestValues = UploadKYCLevel1Details.RequestValues(
            mLocalRepository.clientDetails.clientId.toInt(),
            kycLevel1Details.toModel(),
        )
        val requestValues = uploadKYCLevel1DetailsUseCase.walletRequestValues
        mUseCaseHandler.execute(
            uploadKYCLevel1DetailsUseCase,
            requestValues,
            object : UseCase.UseCaseCallback<UploadKYCLevel1Details.ResponseValue> {
                override fun onSuccess(response: UploadKYCLevel1Details.ResponseValue) {
                    kycUiState.value = KYCLevel1UiState.Success
                }

                override fun onError(message: String) {
                    kycUiState.value = KYCLevel1UiState.Error
                }
            },
        )
    }
}

sealed interface KYCLevel1UiState {
    data object Loading : KYCLevel1UiState
    data object Success : KYCLevel1UiState
    data object Error : KYCLevel1UiState
}

data class KYCLevel1DetailsState(
    val firstName: String,

    val lastName: String,

    val addressLine1: String,

    val addressLine2: String,

    val mobileNo: String,

    val dob: String,

    val currentLevel: String = "1",
)

internal fun KYCLevel1DetailsState.toModel(): KYCLevel1Details {
    return KYCLevel1Details(
        firstName = firstName.trim(),
        lastName = lastName.trim(),
        addressLine1 = addressLine1.trim(),
        addressLine2 = addressLine2.trim(),
        mobileNo = mobileNo.trim(),
        dob = dob.trim(),
        currentLevel = currentLevel,
    )
}
