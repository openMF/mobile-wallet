/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
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
    // TODO(phase-5-batch-3): reads off the existing history / transaction store
    //   per GOAL D13 — the receipt screen renders a `Transaction` +
    //   `receiptLink` (see `ReceiptUiState.Success`), which is a projection of
    //   the same transaction data the batch-4 `AppStoreRegistry.History`
    //   LEDGER store already caches (`wallet_transactions`). No dedicated
    //   `ReceiptEntity` / `ReceiptStore` is emitted for this feature — the
    //   implementation should thread `transactionId` (from savedStateHandle)
    //   into `SelfServiceRepository.getTransactionsStream(...).state` or a
    //   future `getTransactionByIdStream(...)` variant on top of the same
    //   store, then
    //   project the row into `ReceiptUiState.Success`. Kept as an
    //   Error("Not implemented yet") stub until that wiring lands (existing
    //   pre-batch-3 behavior — not a Batch-3 regression).
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
