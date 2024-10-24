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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifospay.core.model.entity.invoice.Invoice
import org.koin.androidx.compose.koinViewModel
import org.mifospay.core.designsystem.component.MifosLoadingWheel
import org.mifospay.core.designsystem.icon.MifosIcons.Info
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.invoices.R

@Composable
fun InvoiceScreenRoute(
    navigateToInvoiceDetailScreen: (Uri) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InvoicesViewModel = koinViewModel(),
) {
    val invoiceUiState by viewModel.invoiceUiState.collectAsStateWithLifecycle()
    InvoiceScreen(
        invoiceUiState = invoiceUiState,
        getUniqueInvoiceLink = viewModel::getUniqueInvoiceLink,
        navigateToInvoiceDetailScreen = navigateToInvoiceDetailScreen,
        modifier = modifier,
    )
}

@Composable
private fun InvoiceScreen(
    invoiceUiState: InvoicesUiState,
    getUniqueInvoiceLink: (Long) -> Uri?,
    navigateToInvoiceDetailScreen: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (invoiceUiState) {
        is InvoicesUiState.Error -> {
            EmptyContentScreen(
                title = stringResource(id = R.string.feature_invoices_error_oops),
                subTitle = stringResource(id = R.string.feature_invoices_unexpected_error_subtitle),
                modifier = Modifier,
                iconTint = MaterialTheme.colorScheme.primary,
                iconImageVector = Info,
            )
        }

        is InvoicesUiState.InvoiceList -> {
            InvoicesList(
                invoiceList = invoiceUiState.list,
                modifier = modifier,
                onClickInvoice = { invoiceId ->
                    val invoiceUri = getUniqueInvoiceLink(invoiceId)
                    invoiceUri?.let { uri ->
                        navigateToInvoiceDetailScreen.invoke(uri)
                    }
                },
            )
        }

        InvoicesUiState.Empty -> {
            EmptyContentScreen(
                title = stringResource(id = R.string.feature_invoices_error_oops),
                subTitle = stringResource(id = R.string.feature_invoices_error_no_invoices_found),
                modifier = Modifier,
                iconTint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }

        InvoicesUiState.Loading -> {
            MifosLoadingWheel(
                modifier = Modifier.fillMaxWidth(),
                contentDesc = stringResource(R.string.feature_invoices_loading),
            )
        }
    }
}

@Composable
private fun InvoicesList(
    invoiceList: List<Invoice?>,
    onClickInvoice: (Long) -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = lazyListState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            items = invoiceList,
            key = { it?.invoiceId ?: 0L },
        ) { invoice ->
            if (invoice != null) {
                InvoiceItem(
                    invoice = invoice,
                    onClick = onClickInvoice,
                )
            }
        }
    }
}

class InvoicesUiStateProvider : PreviewParameterProvider<InvoicesUiState> {
    override val values: Sequence<InvoicesUiState>
        get() = sequenceOf(
            InvoicesUiState.Loading,
            InvoicesUiState.Empty,
            InvoicesUiState.InvoiceList(sampleInvoiceList),
            InvoicesUiState.Error("Some Error Occurred"),
        )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun InvoiceScreenPreview(
    @PreviewParameter(InvoicesUiStateProvider::class) invoiceUiState: InvoicesUiState,
) {
    MifosTheme {
        InvoiceScreen(
            invoiceUiState = invoiceUiState,
            getUniqueInvoiceLink = { Uri.EMPTY },
            navigateToInvoiceDetailScreen = {},
        )
    }
}

val sampleInvoiceList = List(10) { index ->
    Invoice(
        id = 1L,
        clientId = 2L,
        consumerId = "CUST001",
        consumerName = "John Doe",
        amount = 1500.750000,
        itemsBought = "Laptop, Mouse, Keyboard",
        status = 1L,
        transactionId = "TRX12345",
        invoiceId = 1L,
        title = "Invoice for Computer Accessories",
        date = "19 October 2024",
        createdAt = listOf(System.currentTimeMillis()),
        updatedAt = listOf(System.currentTimeMillis()),
    )
}
