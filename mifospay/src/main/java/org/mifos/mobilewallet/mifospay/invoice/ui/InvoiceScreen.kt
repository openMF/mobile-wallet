package org.mifos.mobilewallet.mifospay.invoice.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.designsystem.component.MifosLoadingWheel
import org.mifos.mobilewallet.mifospay.invoice.presenter.InvoiceUiState
import org.mifos.mobilewallet.mifospay.invoice.presenter.InvoicesViewModel
import org.mifos.mobilewallet.mifospay.ui.EmptyContentScreen

@Composable
fun InvoiceScreen(
    viewModel: InvoicesViewModel = hiltViewModel()
) {
    val invoiceUiState by viewModel.invoiceUiState.collectAsStateWithLifecycle()
    InvoiceScreen(
        invoiceUiState = invoiceUiState
    )
}

@Composable
fun InvoiceScreen(invoiceUiState: InvoiceUiState) {
    when (invoiceUiState) {
        is InvoiceUiState.Error -> {
            EmptyContentScreen(
                modifier = Modifier,
                title = stringResource(id = R.string.error_oops),
                subTitle = stringResource(id = R.string.unexpected_error_subtitle),
                iconTint = Color.Black,
                iconImageVector = Icons.Rounded.Info
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

        InvoiceUiState.Empty -> {
            EmptyContentScreen(
                modifier = Modifier,
                title = stringResource(id = R.string.error_oops),
                subTitle = stringResource(id = R.string.error_no_invoices_found),
                iconTint = Color.Black,
                iconImageVector = Icons.Rounded.Info
            )
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