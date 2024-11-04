/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.merchants

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.mifospay.core.model.savingsaccount.Transaction

class MerchantTransferViewModel(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val state = MutableStateFlow<MerchantTransferUiState>(MerchantTransferUiState.Empty)
    val uiState: StateFlow<MerchantTransferUiState> = state.asStateFlow()
}

sealed class MerchantTransferUiState {
    data object Loading : MerchantTransferUiState()
    class Success(val transactionsList: List<Transaction>) : MerchantTransferUiState()
    data object Empty : MerchantTransferUiState()
    data object Error : MerchantTransferUiState()
    data object InsufficientBalance : MerchantTransferUiState()
}
