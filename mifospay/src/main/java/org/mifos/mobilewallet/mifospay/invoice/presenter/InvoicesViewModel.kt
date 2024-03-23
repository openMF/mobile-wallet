package org.mifos.mobilewallet.mifospay.invoice.presenter

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.mifos.mobilewallet.model.entity.Invoice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.base.UseCaseHandler
import org.mifos.mobilewallet.core.domain.usecase.invoice.FetchInvoices
import org.mifos.mobilewallet.mifospay.common.Constants.INVOICE_DOMAIN
import org.mifos.mobilewallet.mifospay.core.datastore.PreferencesHelper
import javax.inject.Inject

@HiltViewModel
class InvoicesViewModel @Inject constructor(
    private val mUseCaseHandler: UseCaseHandler,
    private val mPreferencesHelper: PreferencesHelper,
    private val fetchInvoicesUseCase: FetchInvoices
) : ViewModel() {


    private val _invoiceUiState = MutableStateFlow<InvoiceUiState>(InvoiceUiState.Loading)
    val invoiceUiState: StateFlow<InvoiceUiState> = _invoiceUiState

    fun fetchInvoices() {
        _invoiceUiState.value = InvoiceUiState.Loading
        mUseCaseHandler.execute(fetchInvoicesUseCase,
            FetchInvoices.RequestValues(mPreferencesHelper.clientId.toString() + ""),
            object : UseCase.UseCaseCallback<FetchInvoices.ResponseValue> {
                override fun onSuccess(response: FetchInvoices.ResponseValue) {
                    if (response.invoiceList.isNotEmpty())
                        _invoiceUiState.value = InvoiceUiState.InvoiceList(response.invoiceList)
                    else _invoiceUiState.value = InvoiceUiState.Empty
                }

                override fun onError(message: String) {
                    _invoiceUiState.value = InvoiceUiState.Error(message)
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

sealed class InvoiceUiState {
    data object Loading : InvoiceUiState()
    data object Empty : InvoiceUiState()
    data class Error(val message: String) : InvoiceUiState()
    data class InvoiceList(val list: List<Invoice?>) : InvoiceUiState()
}