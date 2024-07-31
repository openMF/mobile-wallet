package org.mifospay.feature.invoices

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifospay.core.model.entity.Invoice
import com.mifospay.core.model.utils.DateHelper
import org.mifospay.common.Constants
import org.mifospay.core.designsystem.component.MfOverlayLoadingWheel
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.core.ui.ErrorScreenContent
import org.mifospay.invoices.R

@Composable
fun InvoiceDetailScreen(
    viewModel: InvoiceDetailViewModel = hiltViewModel(),
    data: Uri?,
    onBackPress: () -> Unit,
    navigateToReceiptScreen: (String) -> Unit
) {
    val invoiceDetailUiState by viewModel.invoiceDetailUiState.collectAsStateWithLifecycle()
    InvoiceDetailScreen(
        invoiceDetailUiState = invoiceDetailUiState,
        onBackPress = onBackPress,
        navigateToReceiptScreen = navigateToReceiptScreen
    )
    LaunchedEffect(key1 = true) {
        viewModel.getInvoiceDetails(data)
    }
}

@Composable
fun InvoiceDetailScreen(
    invoiceDetailUiState: InvoiceDetailUiState,
    onBackPress: () -> Unit,
    navigateToReceiptScreen: (String) -> Unit
) {
    MifosScaffold(
        topBarTitle = R.string.feature_invoices_invoice,
        backPress = { onBackPress.invoke() },
        scaffoldContent = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(it)
            ) {
                when (invoiceDetailUiState) {
                    is InvoiceDetailUiState.Error -> {
                        ErrorScreenContent()
                    }

                    InvoiceDetailUiState.Loading -> {
                        MfOverlayLoadingWheel(
                            contentDesc = stringResource(R.string.feature_invoices_loading)
                        )
                    }

                    is InvoiceDetailUiState.Success -> {
                        InvoiceDetailsContent(
                            invoiceDetailUiState.invoice,
                            invoiceDetailUiState.merchantId,
                            invoiceDetailUiState.paymentLink,
                            navigateToReceiptScreen = navigateToReceiptScreen
                        )
                    }
                }
            }
        })
}

@Composable
fun InvoiceDetailsContent(
    invoice: Invoice?,
    merchantId: String?,
    paymentLink: String?,
    navigateToReceiptScreen: (String) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        val context = LocalContext.current
        Text(
            text = stringResource(R.string.feature_invoices_invoice_details),
            modifier = Modifier.padding(top = 16.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.feature_invoices_merchant_id),
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = merchantId.toString(),
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.feature_invoices_consumer_id),
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = (invoice?.consumerName + " " + invoice?.consumerId),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.feature_invoices_amount),
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = Constants.INR + " " + invoice?.amount + "",
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.feature_invoices_items_bought),
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = invoice?.itemsBought.toString(),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (invoice?.status == 1L) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.feature_invoices_status),
                    modifier = Modifier.padding(top = 8.dp)
                )

                Text(
                    text = Constants.DONE,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.feature_invoices_transaction_id),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(top = 10.dp)
                )
                Text(
                    text = invoice.transactionId ?: "",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .then(Modifier.height(0.dp))
                )
            }
            Divider()
            Text(
                text = stringResource(id = R.string.feature_invoices_unique_receipt_link),
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Text(
                text = invoice.transactionId ?: "",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                invoice.transactionId?.let { it1 ->
                                    navigateToReceiptScreen.invoke(
                                        it1
                                    )
                                }
                            },
                            onLongPress = {
                                clipboardManager.setText(
                                    AnnotatedString(
                                        Constants.RECEIPT_DOMAIN + invoice.transactionId
                                    )
                                )
                            })
                    }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.feature_invoices_date),
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = DateHelper.getDateAsString(invoice!!.date),
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        Divider()
        Text(
            text = stringResource(id = R.string.feature_invoices_payment_options_will_be_fetched_from_upi),
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(vertical = 10.dp)
        )
        Divider()
        Text(
            text = stringResource(id = R.string.feature_invoices_unique_payment_link),
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = paymentLink.toString(),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                clipboardManager.setText(AnnotatedString(paymentLink.toString()))
                            })
                    }
            )
        }

    }
}

@Composable
fun Divider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0x44000000))
    )
}

class InvoiceDetailScreenProvider : PreviewParameterProvider<InvoiceDetailUiState> {
    override val values: Sequence<InvoiceDetailUiState>
        get() = sequenceOf(
            InvoiceDetailUiState.Loading,
            InvoiceDetailUiState.Error("Some Error Occurred"),
            InvoiceDetailUiState.Success(Invoice(), "", "")
        )

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InvoiceDetailScreenPreview(
    @PreviewParameter(InvoiceDetailScreenProvider::class) invoiceDetailUiState: InvoiceDetailUiState
) {
    MifosTheme {
        InvoiceDetailScreen(
            invoiceDetailUiState = invoiceDetailUiState,
            onBackPress = {},
            navigateToReceiptScreen = {}
        )
    }
}