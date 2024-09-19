/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.send.money

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.account.FetchAccount
import org.mifospay.core.data.repository.local.LocalRepository

class SendPaymentViewModel (
    private val useCaseHandler: UseCaseHandler,
    private val localRepository: LocalRepository,
    private val fetchAccount: FetchAccount,
) : ViewModel() {

    private val mShowProgress = MutableStateFlow(false)
    val showProgress: StateFlow<Boolean> = mShowProgress

    private val mVpa = MutableStateFlow("")
    val vpa: StateFlow<String> = mVpa

    private val mMobile = MutableStateFlow("")
    val mobile: StateFlow<String> = mMobile

    init {
        fetchVpa()
        fetchMobile()
    }

    fun updateProgressState(isVisible: Boolean) {
        mShowProgress.update { isVisible }
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

    fun checkSelfTransfer(
        selfVpa: String?,
        selfMobile: String?,
        externalIdOrMobile: String?,
        sendMethodType: SendMethodType,
    ): Boolean {
        return when (sendMethodType) {
            SendMethodType.VPA -> {
                selfVpa.takeIf { !it.isNullOrEmpty() }?.let { it == externalIdOrMobile } ?: false
            }

            SendMethodType.MOBILE -> {
                selfMobile.takeIf { !it.isNullOrEmpty() }?.let { it == externalIdOrMobile } ?: false
            }
        }
    }

    fun checkBalanceAvailabilityAndTransfer(
        externalId: String?,
        transferAmount: Double,
        onAnyError: (Int) -> Unit,
        proceedWithTransferFlow: (String, Double) -> Unit,
    ) {
        updateProgressState(true)
        useCaseHandler.execute(
            fetchAccount,
            FetchAccount.RequestValues(localRepository.clientDetails.clientId),
            object : UseCase.UseCaseCallback<FetchAccount.ResponseValue> {
                override fun onSuccess(response: FetchAccount.ResponseValue) {
                    updateProgressState(false)
                    if (transferAmount > response.account.balance) {
                        onAnyError(R.string.feature_send_money_insufficient_balance)
                    } else {
                        if (externalId != null) {
                            proceedWithTransferFlow(externalId, transferAmount)
                        }
                    }
                }

                override fun onError(message: String) {
                    updateProgressState(false)
                    onAnyError.invoke(R.string.feature_send_money_error_fetching_balance)
                }
            },
        )
    }
}
