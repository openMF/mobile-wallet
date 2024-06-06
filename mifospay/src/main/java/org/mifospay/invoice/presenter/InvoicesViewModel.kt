package org.mifospay.invoice.presenter

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.mifospay.core.model.entity.Invoice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.base.UseCaseHandler
import org.mifospay.core.data.domain.usecase.invoice.FetchInvoices
import org.mifospay.common.Constants.INVOICE_DOMAIN
import org.mifospay.core.datastore.PreferencesHelper
import javax.inject.Inject

@HiltViewModel
class InvoicesViewModel @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val mPreferencesHelper: PreferencesHelper,
    private val fetchInvoicesUseCase: FetchInvoices
) : ViewModel() {


    private val _invoiceUiState = MutableStateFlow<InvoicesUiState>(InvoicesUiState.Loading)
    val invoiceUiState: StateFlow<InvoicesUiState> = _invoiceUiState

    fun fetchInvoices() {
        _invoiceUiState.value = InvoicesUiState.Loading
        mUseCaseHandler.execute(fetchInvoicesUseCase,
            FetchInvoices.RequestValues(mPreferencesHelper.clientId.toString() + ""),
            object : UseCase.UseCaseCallback<FetchInvoices.ResponseValue> {
                override fun onSuccess(response: FetchInvoices.ResponseValue) {
                    if (response.invoiceList.isNotEmpty())
                        _invoiceUiState.value = InvoicesUiState.InvoiceList(response.invoiceList)
                    else _invoiceUiState.value = InvoicesUiState.Empty
                }

                override fun onError(message: String) {
                    _invoiceUiState.value = InvoicesUiState.Error(message)
                }
            })
    }

    fun getUniqueInvoiceLink(id: Long): Uri? {
        return Uri.parse(
            INVOICE_DOMAIN + mPreferencesHelper.clientId + "/" + id
        )
    }

    init {
        fetchInvoices()
    }
}

sealed class InvoicesUiState {
    data object Loading : InvoicesUiState()
    data object Empty : InvoicesUiState()
    data class Error(val message: String) : InvoicesUiState()
    data class InvoiceList(val list: List<Invoice?>) : InvoicesUiState()
}