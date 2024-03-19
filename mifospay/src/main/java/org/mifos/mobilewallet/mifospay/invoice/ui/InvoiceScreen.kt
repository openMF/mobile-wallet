package org.mifos.mobilewallet.mifospay.invoice.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.designsystem.component.MifosErrorLayout
import org.mifos.mobilewallet.mifospay.designsystem.component.MifosLoadingWheel
import org.mifos.mobilewallet.mifospay.invoice.presenter.InvoiceUiState

@Composable
fun InvoiceScreen(invoiceUiState: InvoiceUiState) {
    when (invoiceUiState) {
        is InvoiceUiState.Error -> {
            MifosErrorLayout(
                modifier = Modifier.fillMaxSize(),
                icon = R.drawable.ic_error_state,
                error = R.string.error_no_invoices_found
            )
        }

        is InvoiceUiState.InvoiceList -> {
            Column(modifier = Modifier.fillMaxSize()) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(invoiceUiState.list) {
                        InvoiceItem(
                            invoiceTitle = it?.title.toString(),
                            invoiceAmount = it?.amount.toString(),
                            invoiceStatus = it?.status.toString(),
                            invoiceDate = it?.date.toString(),
                            invoiceId = it?.id.toString(),
                            invoiceStatusIcon = it?.status!!
                        )
                    }
                }
            }
        }

        InvoiceUiState.Loading -> {
            MifosLoadingWheel(
                modifier = Modifier.fillMaxWidth(),
                contentDesc = stringResource(R.string.loading)
            )
        }
    }
}

@Preview
@Composable
fun InvoiceScreenPreview() {
    InvoiceScreen(invoiceUiState = InvoiceUiState.Loading)
}