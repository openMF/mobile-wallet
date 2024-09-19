/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.payments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.mifospay.core.data.base.UseCase.UseCaseCallback
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.account.FetchAccount
import org.mifospay.core.data.repository.local.LocalRepository

class TransferViewModel (
    val mUsecaseHandler: UseCaseHandler,
    val localRepository: LocalRepository,
    val mFetchAccount: FetchAccount,
) : ViewModel() {

    private val mVpa = MutableStateFlow("")
    val vpa: StateFlow<String> = mVpa

    private val mMobile = MutableStateFlow("")
    val mobile: StateFlow<String> = mMobile

    private var mTransferUiState = MutableStateFlow<TransferUiState>(TransferUiState.Loading)
    var transferUiState: StateFlow<TransferUiState> = mTransferUiState

    private var mUpdateSuccess = MutableStateFlow<Boolean>(false)
    var updateSuccess: StateFlow<Boolean> = mUpdateSuccess

    init {
        fetchVpa()
        fetchMobile()
    }

    private fun fetchVpa() {
        viewModelScope.launch {
            mVpa.value = localRepository.clientDetails.externalId.toString()
        }
    }

    private fun fetchMobile() {
        viewModelScope.launch {
            mMobile.value = localRepository.preferencesHelper.mobile.toString()
        }
    }

    fun checkSelfTransfer(externalId: String?): Boolean {
        return externalId == localRepository.clientDetails.externalId
    }

    fun checkBalanceAvailability(externalId: String, transferAmount: Double) {
        mUsecaseHandler.execute(
            mFetchAccount,
            FetchAccount.RequestValues(localRepository.clientDetails.clientId),
            object : UseCaseCallback<FetchAccount.ResponseValue> {
                override fun onSuccess(response: FetchAccount.ResponseValue) {
                    mTransferUiState.value = TransferUiState.Loading
                    if (transferAmount > response.account.balance) {
                        mUpdateSuccess.value = true
                    } else {
                        mTransferUiState.value =
                            TransferUiState.ShowClientDetails(externalId, transferAmount)
                    }
                }

                override fun onError(message: String) {
                    mUpdateSuccess.value = false
                }
            },
        )
    }
}

sealed interface TransferUiState {
    data object Loading : TransferUiState
    data class ShowClientDetails(
        val externalId: String,
        val transferAmount: Double,
    ) : TransferUiState
}
