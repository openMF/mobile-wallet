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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifospay.core.model.entity.invoice.Invoice
import org.koin.androidx.compose.koinViewModel
import org.mifospay.common.Constants
import org.mifospay.core.designsystem.component.MfOverlayLoadingWheel
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.core.ui.ErrorScreenContent
import org.mifospay.core.ui.MifosDivider
import org.mifospay.invoices.R

@Composable
internal fun InvoiceDetailScreen(
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InvoiceDetailViewModel = koinViewModel(),
) {
    val invoiceDetailUiState by viewModel.invoiceDetailUiState.collectAsStateWithLifecycle()

    InvoiceDetailScreen(
        invoiceDetailUiState = invoiceDetailUiState,
        onBackPress = onBackPress,
        modifier = modifier,
    )
}

@Composable
private fun InvoiceDetailScreen(
    invoiceDetailUiState: InvoiceDetailUiState,
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosScaffold(
        modifier = modifier,
        topBarTitle = R.string.feature_invoices_invoice,
        backPress = { onBackPress.invoke() },
        scaffoldContent = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(it),
            ) {
                when (invoiceDetailUiState) {
                    is InvoiceDetailUiState.Error -> {
                        ErrorScreenContent()
                    }

                    InvoiceDetailUiState.Loading -> {
                        MfOverlayLoadingWheel(
                            contentDesc = stringResource(R.string.feature_invoices_loading),
                        )
                    }

                    is InvoiceDetailUiState.Success -> {
                        InvoiceDetailContent(
                            invoiceDetailUiState.invoice,
                        )
                    }
                }
            }
        },
    )
}

@Composable
private fun InvoiceDetailContent(
    invoice: Invoice?,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = lazyListState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            if (invoice != null) {
                InvoiceDetailCard(
                    invoice = invoice,
                    modifier = Modifier,
                )
            }
        }
    }
}

@Composable
private fun InvoiceDetailCard(
    invoice: Invoice,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        RowBlock {
            Text(
                text = stringResource(id = R.string.feature_invoices_merchant_id),
                color =
                MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = invoice.consumerId,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        RowBlock {
            Text(
                text = stringResource(R.string.feature_invoices_consumer_id),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = invoice.consumerName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        RowBlock {
            Text(
                text = stringResource(R.string.feature_invoices_amount),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = invoice.amount.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        RowBlock {
            Text(
                text = stringResource(R.string.feature_invoices_items_bought),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = invoice.itemsBought,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        RowBlock {
            Text(
                text = stringResource(R.string.feature_invoices_status),
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                text = if (invoice.status == 1L) Constants.DONE else Constants.PENDING,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        RowBlock {
            Text(
                text = stringResource(R.string.feature_invoices_transaction_id),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = invoice.transactionId,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        RowBlock(showDivider = false) {
            Text(
                text = stringResource(R.string.feature_invoices_date),
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                text = invoice.date,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private inline fun RowBlock(
    modifier: Modifier = Modifier,
    showDivider: Boolean = true,
    crossinline content: @Composable (RowScope.() -> Unit),
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            content()
        }

        if (showDivider) {
            MifosDivider()
        }
    }
}

class InvoiceDetailScreenProvider : PreviewParameterProvider<InvoiceDetailUiState> {
    override val values: Sequence<InvoiceDetailUiState>
        get() = sequenceOf(
            InvoiceDetailUiState.Loading,
            InvoiceDetailUiState.Error("Some Error Occurred"),
            InvoiceDetailUiState.Success(
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
                ),
            ),
        )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun InvoiceDetailScreenPreview(
    @PreviewParameter(InvoiceDetailScreenProvider::class)
    invoiceDetailUiState: InvoiceDetailUiState,
) {
    MifosTheme {
        InvoiceDetailScreen(
            invoiceDetailUiState = invoiceDetailUiState,
            onBackPress = {},
        )
    }
}
