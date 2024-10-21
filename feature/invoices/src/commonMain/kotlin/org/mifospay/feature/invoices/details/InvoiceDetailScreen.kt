/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.invoices.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mobile_wallet.feature.invoices.generated.resources.Res
import mobile_wallet.feature.invoices.generated.resources.feature_invoices_amount
import mobile_wallet.feature.invoices.generated.resources.feature_invoices_consumer_id
import mobile_wallet.feature.invoices.generated.resources.feature_invoices_date
import mobile_wallet.feature.invoices.generated.resources.feature_invoices_invoice_details
import mobile_wallet.feature.invoices.generated.resources.feature_invoices_items_bought
import mobile_wallet.feature.invoices.generated.resources.feature_invoices_loading
import mobile_wallet.feature.invoices.generated.resources.feature_invoices_merchant_id
import mobile_wallet.feature.invoices.generated.resources.feature_invoices_status
import mobile_wallet.feature.invoices.generated.resources.feature_invoices_transaction_id
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.common.Constants
import org.mifospay.core.designsystem.component.MifosLoadingWheel
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.model.datatables.invoice.Invoice
import org.mifospay.core.ui.ErrorScreenContent
import org.mifospay.core.ui.MifosDivider
import org.mifospay.core.ui.utils.EventsEffect
import org.mifospay.feature.invoices.details.InvoiceDetailState.ViewState.Content
import org.mifospay.feature.invoices.details.InvoiceDetailState.ViewState.Error
import org.mifospay.feature.invoices.details.InvoiceDetailState.ViewState.Loading

@Composable
internal fun InvoiceDetailScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InvoiceDetailViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            is InvoiceDetailEvent.OnNavigateBack -> navigateBack.invoke()
        }
    }

    InvoiceDetailScreen(
        state = state.viewState,
        modifier = modifier,
        onAction = remember(viewModel) {
            { action -> viewModel.trySendAction(action) }
        },
    )
}

@Composable
internal fun InvoiceDetailScreen(
    state: InvoiceDetailState.ViewState,
    modifier: Modifier = Modifier,
    onAction: (InvoiceDetailAction) -> Unit,
) {
    MifosScaffold(
        topBarTitle = stringResource(Res.string.feature_invoices_invoice_details),
        backPress = { onAction(InvoiceDetailAction.NavigateBack) },
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center,
        ) {
            when (state) {
                is Loading -> {
                    MifosLoadingWheel(
                        modifier = Modifier.align(Alignment.Center),
                        contentDesc = stringResource(Res.string.feature_invoices_loading),
                    )
                }

                is Error -> {
                    ErrorScreenContent(
                        subTitle = state.message,
                    )
                }

                is Content -> {
                    InvoiceDetailContent(
                        state = state,
                        modifier = Modifier,
                    )
                }
            }
        }
    }
}

@Composable
private fun InvoiceDetailContent(
    state: Content,
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
            InvoiceDetailCard(
                invoice = state.invoice,
                modifier = Modifier,
            )
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
            Text(text = stringResource(Res.string.feature_invoices_merchant_id))
            Text(
                text = invoice.consumerId,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }

        RowBlock {
            Text(text = stringResource(Res.string.feature_invoices_consumer_id))
            Text(
                text = invoice.consumerName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }

        RowBlock {
            Text(text = stringResource(Res.string.feature_invoices_amount))
            Text(
                text = invoice.amount.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }

        RowBlock {
            Text(text = stringResource(Res.string.feature_invoices_items_bought))
            Text(
                text = invoice.itemsBought,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }

        RowBlock {
            Text(text = stringResource(Res.string.feature_invoices_status))

            Text(
                text = if (invoice.status == 1L) Constants.DONE else Constants.PENDING,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }

        RowBlock {
            Text(text = stringResource(Res.string.feature_invoices_transaction_id))
            Text(
                text = invoice.transactionId,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }

        RowBlock(showDivider = false) {
            Text(text = stringResource(Res.string.feature_invoices_date))

            Text(
                text = invoice.date,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
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
