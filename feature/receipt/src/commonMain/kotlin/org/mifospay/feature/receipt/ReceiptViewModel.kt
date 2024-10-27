/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.receipt

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.mifospay.core.model.savingsaccount.Transaction
import org.mifospay.core.model.savingsaccount.TransferDetail

class ReceiptViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val mReceiptState =
        MutableStateFlow<ReceiptUiState>(ReceiptUiState.Error("Not implemented yet"))
    val receiptUiState: StateFlow<ReceiptUiState> = mReceiptState.asStateFlow()
}

sealed interface ReceiptUiState {
    data class Success(
        val transaction: Transaction,
        val transferDetail: TransferDetail,
        val receiptLink: String,
    ) : ReceiptUiState

    data class Error(
        val message: String,
    ) : ReceiptUiState

    data object Loading : ReceiptUiState
}
