/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.transaction.detail

import androidx.lifecycle.ViewModel
import com.mifospay.core.model.entity.accounts.savings.TransferDetail
import kotlinx.coroutines.flow.MutableStateFlow
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.account.FetchAccountTransfer

class TransactionDetailViewModel (
    private val mUseCaseHandler: UseCaseHandler,
    private val mFetchAccountTransferUseCase: FetchAccountTransfer,
) : ViewModel() {

    private val _transactionDetailUiState: MutableStateFlow<TransactionDetailUiState> =
        MutableStateFlow(TransactionDetailUiState.Loading)
    val transactionDetailUiState get() = _transactionDetailUiState

    fun getTransferDetail(transferId: Long) {
        mUseCaseHandler.execute(
            mFetchAccountTransferUseCase,
            FetchAccountTransfer.RequestValues(transferId),
            object : UseCase.UseCaseCallback<FetchAccountTransfer.ResponseValue?> {
                override fun onSuccess(response: FetchAccountTransfer.ResponseValue?) {
                    _transactionDetailUiState.value =
                        TransactionDetailUiState.Success(response?.transferDetail)
                }

                override fun onError(message: String) {
                    _transactionDetailUiState.value = TransactionDetailUiState.Error
                }
            },
        )
    }
}

sealed class TransactionDetailUiState {
    data object Loading : TransactionDetailUiState()
    data object Error : TransactionDetailUiState()
    data class Success(val transferDetail: TransferDetail?) : TransactionDetailUiState()
}
