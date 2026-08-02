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
import kotlinx.serialization.Serializable
import org.mifospay.core.common.ScreenState
import org.mifospay.core.common.setSerialized
import org.mifospay.core.data.repository.InvoiceRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.datatables.invoice.Invoice
import org.mifospay.core.ui.utils.BaseViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class InvoicesViewModel(
    invoiceRepository: InvoiceRepository,
    repository: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<InvoiceState, InvoiceEvent, InvoiceAction>(
    initialState = savedStateHandle.get(KEY_STATE) ?: run {
        val clientId = requireNotNull(repository.clientId.value)

        InvoiceState(clientId = clientId)
    },
) {

    companion object {
        private const val KEY_STATE = "InvoiceViewModel"
    }

    init {
        stateFlow
            .onEach { savedStateHandle.setSerialized(key = KEY_STATE, value = it) }
            .launchIn(viewModelScope)
    }

    val invoiceUiState = invoiceRepository.getInvoicesScreen(
        // Phase-5 Batch-2 LEDGER read (GOAL D13) — switched from the
        // transitional `getInvoices(clientId)` (`asScreenStateFlow` shim over
        // the raw Ktorfit flow) to the store-native `getInvoicesScreen(...)`
        // that consumes the `invoice` Store5 read (`createStore` + Room SoT +
        // CACHE_FIRST_SWR + atomic replacePage). Same `Flow<ScreenState<List<Invoice>>>`
        // shape; consumer branches are unchanged. Requires `viewModelScope`
        // for the stream's internal reconnect + periodic + SWR side-fetch
        // coroutines.
        clientId = state.clientId,
        scope = viewModelScope,
    ).mapLatest { result ->
        // Fold ScreenState → the existing 4-branch UiState. Repo emits
        // ScreenState.Empty when the underlying list is empty, so we route it
        // directly to InvoicesUiState.Empty and drop the ex `isEmpty()` check.
        // NoNetwork/Unauthenticated → Error until Phase-4 differentiates.
        when (result) {
            is ScreenState.Loading -> InvoicesUiState.Loading

            is ScreenState.Empty -> InvoicesUiState.Empty

            is ScreenState.Content -> InvoicesUiState.InvoiceList(result.data)

            is ScreenState.Error -> InvoicesUiState.Error(result.error.message.toString())

            is ScreenState.NoNetwork ->
                InvoicesUiState.Error("No network. Please check your connection.")

            is ScreenState.Unauthenticated ->
                InvoicesUiState.Error("Session expired. Please log in again.")
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

@Serializable
data class InvoiceState(
    val clientId: Long,
)

sealed interface InvoiceAction {
    data class InvoiceClicked(val invoiceId: Long) : InvoiceAction
}

sealed interface InvoiceEvent {
    data class NavigateToInvoiceDetail(val invoiceId: Long) : InvoiceEvent
}
