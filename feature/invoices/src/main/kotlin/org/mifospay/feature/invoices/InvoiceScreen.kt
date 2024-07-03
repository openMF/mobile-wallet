package org.mifospay.feature.invoices

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifospay.core.model.entity.Invoice
import org.mifospay.core.designsystem.component.MifosLoadingWheel
import org.mifospay.core.designsystem.icon.MifosIcons.Info
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.invoices.R

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
    invoiceUiState: InvoicesUiState,
    getUniqueInvoiceLink: (Long) -> Uri?
) {
    val context = LocalContext.current
    when (invoiceUiState) {
        is InvoicesUiState.Error -> {
            EmptyContentScreen(
                modifier = Modifier,
                title = stringResource(id = R.string.feature_invoices_error_oops),
                subTitle = stringResource(id = R.string.feature_invoices_unexpected_error_subtitle),
                iconTint = Color.Black,
                iconImageVector = Info
            )
        }

        is InvoicesUiState.InvoiceList -> {
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
                                val intent = Intent(context, InvoiceActivity::class.java)
                                intent.data = getUniqueInvoiceLink(invoiceId.toLong())
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }

        InvoicesUiState.Empty -> {
            EmptyContentScreen(
                modifier = Modifier,
                title = stringResource(id = R.string.feature_invoices_error_oops),
                subTitle = stringResource(id = R.string.feature_invoices_error_no_invoices_found),
                iconTint = Color.Black,
            )
        }

        InvoicesUiState.Loading -> {
            MifosLoadingWheel(
                modifier = Modifier.fillMaxWidth(),
                contentDesc = stringResource(R.string.feature_invoices_loading)
            )
        }
    }
}

class InvoicesUiStateProvider : PreviewParameterProvider<InvoicesUiState> {
    override val values: Sequence<InvoicesUiState>
        get() = sequenceOf(
            InvoicesUiState.Loading,
            InvoicesUiState.Empty,
            InvoicesUiState.InvoiceList(sampleInvoiceList),
            InvoicesUiState.Error("Some Error Occurred")
        )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun InvoiceScreenPreview(
    @PreviewParameter(InvoicesUiStateProvider::class) invoiceUiState: InvoicesUiState
) {
    MifosTheme {
        InvoiceScreen(invoiceUiState = invoiceUiState, getUniqueInvoiceLink = { Uri.EMPTY })
    }
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
