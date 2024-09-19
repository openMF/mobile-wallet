/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.make.transfer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.mifospay.common.PAYEE_EXTERNAL_ID_ARG
import org.mifospay.common.TRANSFER_AMOUNT_ARG
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.account.TransferFunds
import org.mifospay.core.data.domain.usecase.client.SearchClient
import org.mifospay.core.data.repository.local.LocalRepository

class MakeTransferViewModel (
    savedStateHandle: SavedStateHandle,
    private val useCaseHandler: UseCaseHandler,
    private val searchClientUseCase: SearchClient,
    private val transferFundsUseCase: TransferFunds,
    private val localRepository: LocalRepository,
) : ViewModel() {

    private val payeeExternalId: StateFlow<String> =
        savedStateHandle.getStateFlow(PAYEE_EXTERNAL_ID_ARG, "")
    private val transferAmount: StateFlow<String?> =
        savedStateHandle.getStateFlow(TRANSFER_AMOUNT_ARG, null)

    private val _makeTransferState = MutableStateFlow<MakeTransferState>(MakeTransferState.Loading)
    val makeTransferState: StateFlow<MakeTransferState> = _makeTransferState.asStateFlow()

    private val _showTransactionStatus = MutableStateFlow(
        ShowTransactionStatus(
            showSuccessStatus = false,
            showErrorStatus = false,
        ),
    )
    val showTransactionStatus: StateFlow<ShowTransactionStatus> =
        _showTransactionStatus.asStateFlow()

    // Fetch Payee client details
    val fetchPayeeClient = combine(payeeExternalId, transferAmount, ::Pair)
        .map { stringPair ->
            stringPair.takeIf { it.first.isNotEmpty() }?.let {
                fetchClient(it.first, it.second?.toDouble() ?: 0.0)
            }
        }
        .stateIn(scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = null)

    private fun fetchClient(externalId: String, transferAmount: Double) {
        useCaseHandler.execute(
            searchClientUseCase,
            SearchClient.RequestValues(externalId),
            object : UseCase.UseCaseCallback<SearchClient.ResponseValue> {
                override fun onSuccess(response: SearchClient.ResponseValue) {
                    val searchResult = response.results[0]
                    searchResult.resultId.let {
                        _makeTransferState.value = MakeTransferState.Success(
                            it.toLong(),
                            searchResult.resultName,
                            externalId,
                            transferAmount,
                            true,
                        )
                    }
                }

                override fun onError(message: String) {
                    _makeTransferState.value = MakeTransferState.Error(message)
                }
            },
        )
    }

    fun makeTransfer(toClientId: Long, amount: Double) {
        val fromClientId = localRepository.clientDetails.clientId
        useCaseHandler.execute(
            transferFundsUseCase,
            TransferFunds.RequestValues(fromClientId, toClientId, amount),
            object : UseCase.UseCaseCallback<TransferFunds.ResponseValue> {
                override fun onSuccess(response: TransferFunds.ResponseValue) {
                    _showTransactionStatus.value = ShowTransactionStatus(
                        showSuccessStatus = true,
                        showErrorStatus = false,
                    )
                }

                override fun onError(message: String) {
                    _showTransactionStatus.value = ShowTransactionStatus(
                        showSuccessStatus = false,
                        showErrorStatus = true,
                    )
                }
            },
        )
    }
}

data class ShowTransactionStatus(
    val showSuccessStatus: Boolean,
    val showErrorStatus: Boolean,
)

sealed interface MakeTransferState {
    data object Loading : MakeTransferState
    data class Success(
        val toClientId: Long,
        val resultName: String,
        val externalId: String,
        val transferAmount: Double,
        val showBottomSheet: Boolean,
    ) : MakeTransferState

    data class Error(val message: String) : MakeTransferState
}
