/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.invoices

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import org.mifospay.core.common.DataState
import org.mifospay.core.common.Parcelable
import org.mifospay.core.common.Parcelize
import org.mifospay.core.data.repository.InvoiceRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.datatables.invoice.Invoice
import org.mifospay.core.ui.utils.BaseViewModel

private const val KEY_STATE = "InvoiceViewModel"

@OptIn(ExperimentalCoroutinesApi::class)
class InvoicesViewModel(
    invoiceRepository: InvoiceRepository,
    repository: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<InvoiceState, InvoiceEvent, InvoiceAction>(
    initialState = savedStateHandle[KEY_STATE] ?: run {
        val clientId = requireNotNull(repository.clientId.value)

        InvoiceState(clientId = clientId)
    },
) {
    init {
        stateFlow
            .onEach { savedStateHandle[KEY_STATE] = it }
            .launchIn(viewModelScope)
    }

    val invoiceUiState = invoiceRepository.getInvoices(state.clientId).mapLatest { result ->
        when (result) {
            is DataState.Loading -> InvoicesUiState.Loading
            is DataState.Error -> InvoicesUiState.Error(result.exception.message.toString())
            is DataState.Success -> {
                if (result.data.isEmpty()) {
                    InvoicesUiState.Empty
                } else {
                    InvoicesUiState.InvoiceList(result.data)
                }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = InvoicesUiState.Loading,
    )

    override fun handleAction(action: InvoiceAction) {
        when (action) {
            is InvoiceAction.InvoiceClicked -> {
                sendEvent(InvoiceEvent.NavigateToInvoiceDetail(action.invoiceId))
            }
        }
    }
}

sealed interface InvoicesUiState {
    data object Loading : InvoicesUiState
    data object Empty : InvoicesUiState
    data class Error(val message: String) : InvoicesUiState
    data class InvoiceList(val list: List<Invoice>) : InvoicesUiState
}

@Parcelize
data class InvoiceState(
    val clientId: Long,
) : Parcelable

sealed interface InvoiceAction {
    data class InvoiceClicked(val invoiceId: Long) : InvoiceAction
}

sealed interface InvoiceEvent {
    data class NavigateToInvoiceDetail(val invoiceId: Long) : InvoiceEvent
}
