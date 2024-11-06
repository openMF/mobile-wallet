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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mobile_wallet.feature.invoices.generated.resources.Res
import mobile_wallet.feature.invoices.generated.resources.feature_invoices_error_no_invoices_found
import mobile_wallet.feature.invoices.generated.resources.feature_invoices_error_oops
import mobile_wallet.feature.invoices.generated.resources.feature_invoices_loading
import mobile_wallet.feature.invoices.generated.resources.feature_invoices_unexpected_error_subtitle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.MifosLoadingWheel
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.model.datatables.invoice.Invoice
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.core.ui.utils.EventsEffect

@Composable
fun InvoiceScreen(
    navigateToInvoiceDetailScreen: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InvoicesViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val invoiceUiState by viewModel.invoiceUiState.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            is InvoiceEvent.NavigateToInvoiceDetail -> {
                navigateToInvoiceDetailScreen(event.invoiceId)
            }
        }
    }

    InvoiceScreen(
        invoiceUiState = invoiceUiState,
        onAction = remember(viewModel) {
            { viewModel.trySendAction(it) }
        },
        modifier = modifier,
    )
}

@Composable
private fun InvoiceScreen(
    invoiceUiState: InvoicesUiState,
    onAction: (InvoiceAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosScaffold(
        modifier = modifier,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            when (invoiceUiState) {
                is InvoicesUiState.Loading -> {
                    MifosLoadingWheel(
                        modifier = Modifier.fillMaxWidth(),
                        contentDesc = stringResource(Res.string.feature_invoices_loading),
                    )
                }

                is InvoicesUiState.Empty -> {
                    EmptyContentScreen(
                        title = stringResource(Res.string.feature_invoices_error_oops),
                        subTitle = stringResource(Res.string.feature_invoices_error_no_invoices_found),
                        modifier = Modifier,
                        iconTint = Color.Black,
                    )
                }

                is InvoicesUiState.Error -> {
                    EmptyContentScreen(
                        title = stringResource(Res.string.feature_invoices_error_oops),
                        subTitle = stringResource(Res.string.feature_invoices_unexpected_error_subtitle),
                        modifier = Modifier,
                        iconTint = Color.Black,
                    )
                }

                is InvoicesUiState.InvoiceList -> {
                    InvoicesList(
                        invoiceList = invoiceUiState.list,
                        onClickInvoice = {
                            onAction(InvoiceAction.InvoiceClicked(it))
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun InvoicesList(
    invoiceList: List<Invoice>,
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
            key = { it.invoiceId },
        ) { invoice ->
            InvoiceItem(
                invoice = invoice,
                onClick = onClickInvoice,
            )
        }
    }
}
