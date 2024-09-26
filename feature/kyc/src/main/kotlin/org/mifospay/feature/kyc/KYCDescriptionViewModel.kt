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
import androidx.lifecycle.viewModelScope
import org.mifospay.core.model.entity.kyc.KYCLevel1Details
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.kyc.FetchKYCLevel1Details
import org.mifospay.core.data.repository.local.LocalRepository
import org.mifospay.feature.kyc.KYCDescriptionUiState.Loading

class KYCDescriptionViewModel(
    private val mUseCaseHandler: UseCaseHandler,
    private val mLocalRepository: LocalRepository,
    private val fetchKYCLevel1DetailsUseCase: FetchKYCLevel1Details,
) : ViewModel() {
    private val descriptionState = MutableStateFlow<KYCDescriptionUiState>(Loading)
    val kycDescriptionState: StateFlow<KYCDescriptionUiState> = descriptionState

    init {
        fetchCurrentLevel()
    }

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> get() = _isRefreshing.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.emit(true)
            delay(2000)
            fetchCurrentLevel()
            _isRefreshing.emit(false)
        }
    }

    private fun fetchCurrentLevel() {
        fetchKYCLevel1DetailsUseCase.walletRequestValues =
            FetchKYCLevel1Details.RequestValues(mLocalRepository.clientDetails.clientId.toInt())
        val requestValues = fetchKYCLevel1DetailsUseCase.walletRequestValues
        mUseCaseHandler.execute(
            fetchKYCLevel1DetailsUseCase,
            requestValues,
            object : UseCase.UseCaseCallback<FetchKYCLevel1Details.ResponseValue> {
                override fun onSuccess(response: FetchKYCLevel1Details.ResponseValue) {
                    if (response.kycLevel1DetailsList.size == 1) {
                        descriptionState.value = KYCDescriptionUiState.KYCDescription(
                            response.kycLevel1DetailsList.first()!!,
                        )
                    } else {
                        descriptionState.value = KYCDescriptionUiState.Error
                    }
                }

                override fun onError(message: String) {
                    descriptionState.value = KYCDescriptionUiState.Error
                }
            },
        )
    }
}

sealed interface KYCDescriptionUiState {
    data class KYCDescription(val kycLevel1Details: org.mifospay.core.model.entity.kyc.KYCLevel1Details?) : KYCDescriptionUiState
    data object Error : KYCDescriptionUiState
    data object Loading : KYCDescriptionUiState
}
