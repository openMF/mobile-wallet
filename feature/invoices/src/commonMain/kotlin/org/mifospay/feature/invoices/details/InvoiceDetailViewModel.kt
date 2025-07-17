/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.invoices.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable
import org.mifospay.core.common.DataState
import org.mifospay.core.common.getSerialized
import org.mifospay.core.common.setSerialized
import org.mifospay.core.data.repository.InvoiceRepository
import org.mifospay.core.datastore.UserPreferencesRepository
import org.mifospay.core.model.datatables.invoice.Invoice
import org.mifospay.core.ui.utils.BaseViewModel
import org.mifospay.feature.invoices.details.InvoiceDetailAction.Internal.InvoiceDetailResultReceived
import org.mifospay.feature.invoices.details.InvoiceDetailState.ViewState.Content
import org.mifospay.feature.invoices.details.InvoiceDetailState.ViewState.Error

internal class InvoiceDetailViewModel(
    private val preferencesRepository: UserPreferencesRepository,
    repository: InvoiceRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<InvoiceDetailState, InvoiceDetailEvent, InvoiceDetailAction>(
    initialState = savedStateHandle.getSerialized(KEY_STATE) ?: run {
        val clientId = requireNotNull(preferencesRepository.clientId.value)
        val invoiceId = requireNotNull(savedStateHandle.get<Long>("invoiceId"))

        InvoiceDetailState(
            clientId = clientId,
            invoiceId = invoiceId,
            viewState = InvoiceDetailState.ViewState.Loading,
        )
    },
) {

    companion object {
        private const val KEY_STATE = "invoice_detail_state"
    }

    init {
        stateFlow
            .onEach { savedStateHandle.setSerialized(key = KEY_STATE, value = it) }
            .launchIn(viewModelScope)

        repository.getInvoice(state.clientId, state.invoiceId).onEach {
            sendAction(InvoiceDetailResultReceived(it))
        }.launchIn(viewModelScope)
    }

    override fun handleAction(action: InvoiceDetailAction) {
        when (action) {
            is InvoiceDetailAction.NavigateBack -> {
                sendEvent(InvoiceDetailEvent.OnNavigateBack)
            }

            is InvoiceDetailResultReceived -> handleInvoiceDetailResult(action)
        }
    }

    private fun handleInvoiceDetailResult(action: InvoiceDetailResultReceived) {
        when (action.result) {
            is DataState.Loading -> {
                mutableStateFlow.update {
                    it.copy(viewState = InvoiceDetailState.ViewState.Loading)
                }
            }

            is DataState.Error -> {
                val message = action.result.exception.message.toString()
                mutableStateFlow.update {
                    it.copy(viewState = Error(message))
                }
            }

            is DataState.Success -> {
                mutableStateFlow.update {
                    it.copy(viewState = Content(action.result.data))
                }
            }
        }
    }
}

@Serializable
internal data class InvoiceDetailState(
    val clientId: Long,
    val invoiceId: Long,
    val viewState: ViewState,
) {
    @Serializable
    sealed interface ViewState {
        @Serializable
        data object Loading : ViewState

        @Serializable
        data class Error(val message: String) : ViewState

        @Serializable
        data class Content(val invoice: Invoice) : ViewState
    }
}

internal sealed interface InvoiceDetailEvent {
    data object OnNavigateBack : InvoiceDetailEvent
}

internal sealed interface InvoiceDetailAction {
    data object NavigateBack : InvoiceDetailAction

    sealed interface Internal : InvoiceDetailAction {
        data class InvoiceDetailResultReceived(val result: DataState<Invoice>) : Internal
    }
}
