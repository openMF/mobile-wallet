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

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.mifospay.core.model.entity.invoice.Invoice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.invoice.FetchInvoice
import org.mifospay.feature.invoices.navigation.INVOICE_DATA_ARG

class InvoiceDetailViewModel(
    private val mUseCaseHandler: UseCaseHandler,
    private val fetchInvoiceUseCase: FetchInvoice,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _invoiceDetailUiState =
        MutableStateFlow<InvoiceDetailUiState>(InvoiceDetailUiState.Loading)
    val invoiceDetailUiState: StateFlow<InvoiceDetailUiState> = _invoiceDetailUiState
    init {
        savedStateHandle.get<String>(INVOICE_DATA_ARG)?.let { invoiceData ->
            Uri.decode(invoiceData)?.let {
                getInvoiceDetails(Uri.parse(it))
            }
        }
    }

    private fun getInvoiceDetails(data: Uri?) {
        mUseCaseHandler.execute(
            fetchInvoiceUseCase,
            data?.let { FetchInvoice.RequestValues(it) },
            object : UseCase.UseCaseCallback<FetchInvoice.ResponseValue> {
                override fun onSuccess(response: FetchInvoice.ResponseValue) {
                    _invoiceDetailUiState.value = InvoiceDetailUiState.Success(
                        response.invoices[0],
                    )
                }

                override fun onError(message: String) {
                    _invoiceDetailUiState.value = InvoiceDetailUiState.Error(message)
                }
            },
        )
    }
}

sealed interface InvoiceDetailUiState {
    data object Loading : InvoiceDetailUiState
    data class Success(
        val invoice: Invoice?,
    ) : InvoiceDetailUiState

    data class Error(val message: String) : InvoiceDetailUiState
}
