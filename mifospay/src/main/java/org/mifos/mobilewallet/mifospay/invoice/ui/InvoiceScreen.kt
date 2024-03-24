package org.mifos.mobilewallet.mifospay.invoice.ui

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.mobilewallet.model.entity.Invoice
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
        invoiceUiState = invoiceUiState,
        getUniqueInvoiceLink = { viewModel.getUniqueInvoiceLink(it) }
    )
}

@Composable
fun InvoiceScreen(
    invoiceUiState: InvoiceUiState,
    getUniqueInvoiceLink: (Long) -> Uri?
) {
    val context = LocalContext.current
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
                            invoiceStatusIcon = it?.status!!,
                            onClick = { invoiceId ->
                                val uniqueLink = getUniqueInvoiceLink(invoiceId.toLong())
                                val intent = Intent(Intent.ACTION_VIEW, uniqueLink)
                                context.startActivity(intent)
                            }
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

@Preview(showBackground = true)
@Composable
fun InvoiceScreenLoadingPreview() {
    InvoiceScreen(
        invoiceUiState = InvoiceUiState.Loading, getUniqueInvoiceLink = { null })
}

@Preview(showBackground = true)
@Composable
private fun InvoiceScreenEmptyListPreview() {
    InvoiceScreen(invoiceUiState = InvoiceUiState.Empty, getUniqueInvoiceLink = { null })
}

@Preview(showBackground = true)
@Composable
private fun InvoiceScreenListPreview() {
    InvoiceScreen(
        invoiceUiState = InvoiceUiState.InvoiceList(sampleInvoiceList),
        getUniqueInvoiceLink = { null })
}

@Preview(showBackground = true)
@Composable
private fun InvoiceScreenErrorPreview() {
    InvoiceScreen(
        invoiceUiState = InvoiceUiState.Error("Error Screen"),
        getUniqueInvoiceLink = { null })
}

val sampleInvoiceList = List(10) { index ->
    Invoice(
        consumerId = "123456",
        consumerName = "John Doe",
        amount = 250.75,
        itemsBought = "2x Notebook, 1x Pen",
        status = 1L,
        transactionId = "txn_78910",
        id = index.toLong(),
        title = "Stationery Purchase",
        date = mutableListOf(2024, 3, 23)
    )
}
