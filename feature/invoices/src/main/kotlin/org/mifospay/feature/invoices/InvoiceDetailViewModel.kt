package org.mifospay.feature.invoices

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.mifospay.core.model.entity.Invoice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.invoice.FetchInvoice
import org.mifospay.core.datastore.PreferencesHelper
import javax.inject.Inject

@HiltViewModel
class InvoiceDetailViewModel @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val mPreferencesHelper: PreferencesHelper,
    private val fetchInvoiceUseCase: FetchInvoice
) : ViewModel() {

    private val _invoiceDetailUiState =
        MutableStateFlow<InvoiceDetailUiState>(InvoiceDetailUiState.Loading)
    val invoiceDetailUiState: StateFlow<InvoiceDetailUiState> = _invoiceDetailUiState

    fun getInvoiceDetails(data: Uri?) {
        mUseCaseHandler.execute(fetchInvoiceUseCase, FetchInvoice.RequestValues(data),
            object : UseCase.UseCaseCallback<FetchInvoice.ResponseValue> {
                override fun onSuccess(response: FetchInvoice.ResponseValue) {
                    _invoiceDetailUiState.value = InvoiceDetailUiState.Success(
                        response.invoices[0],
                        mPreferencesHelper.fullName + " "
                                + mPreferencesHelper.clientId, data.toString()
                    )
                }

                override fun onError(message: String) {
                    _invoiceDetailUiState.value = InvoiceDetailUiState.Error(message)
                }
            })
    }
}

sealed interface InvoiceDetailUiState {
    data object Loading : InvoiceDetailUiState
    data class Success(
        val invoice: Invoice?,
        val merchantId: String?,
        val paymentLink: String?
    ) : InvoiceDetailUiState

    data class Error(val message: String) : InvoiceDetailUiState
}