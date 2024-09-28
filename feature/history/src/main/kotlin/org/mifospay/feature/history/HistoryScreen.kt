/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.history

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifospay.core.model.domain.Currency
import com.mifospay.core.model.domain.Transaction
import com.mifospay.core.model.domain.TransactionType
import com.mifospay.core.model.entity.accounts.savings.TransferDetail
import org.koin.androidx.compose.koinViewModel
import org.mifospay.core.designsystem.component.MifosBottomSheet
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosLoadingWheel
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.designsystem.theme.NewUi
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.core.ui.TransactionItemScreen
import org.mifospay.feature.transaction.detail.TransactionDetailScreen

@Composable
fun HistoryScreen(
    viewReceipt: (String) -> Unit,
    accountClicked: (String, ArrayList<Transaction>) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = koinViewModel(),
) {
    val historyUiState by viewModel.historyUiState.collectAsStateWithLifecycle()

    HistoryScreen(
        historyUiState = historyUiState,
        viewReceipt = viewReceipt,
        accountClicked = accountClicked,
        modifier = modifier,
    )
}

@Composable
private fun HistoryScreen(
    historyUiState: HistoryUiState,
    viewReceipt: (String) -> Unit,
    accountClicked: (String, ArrayList<Transaction>) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedChip by remember { mutableStateOf(TransactionType.OTHER) }
    var filteredTransactions by remember { mutableStateOf(emptyList<Transaction>()) }
    var transactionsList by remember { mutableStateOf(emptyList<Transaction>()) }
    var transactionDetailState by remember { mutableStateOf<Transaction?>(null) }

    when (historyUiState) {
        HistoryUiState.Empty -> {
            EmptyContentScreen(
                title = stringResource(id = R.string.feature_history_error_oops),
                subTitle = stringResource(id = R.string.feature_history_empty_no_transaction_history_title),
                modifier = Modifier,
                iconTint = MaterialTheme.colorScheme.primary,
                iconImageVector = MifosIcons.Info,
            )
        }

        is HistoryUiState.Error -> {
            EmptyContentScreen(
                title = stringResource(id = R.string.feature_history_error_oops),
                subTitle = stringResource(id = R.string.feature_history_unexpected_error_subtitle),
                modifier = Modifier,
                iconTint = MaterialTheme.colorScheme.primary,
                iconImageVector = MifosIcons.Info,
            )
        }

        is HistoryUiState.HistoryList -> {
            LaunchedEffect(selectedChip) {
                transactionsList = historyUiState.list
                filteredTransactions = when (selectedChip) {
                    TransactionType.OTHER -> historyUiState.list
                    else -> historyUiState.list.filter { it.transactionType == selectedChip }
                }
            }
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Chip(
                        selected = selectedChip == TransactionType.OTHER,
                        onClick = { selectedChip = TransactionType.OTHER },
                        label = stringResource(R.string.feature_history_all),
                    )
                    Chip(
                        selected = selectedChip == TransactionType.CREDIT,
                        onClick = { selectedChip = TransactionType.CREDIT },
                        label = stringResource(R.string.feature_history_credits),
                    )
                    Chip(
                        selected = selectedChip == TransactionType.DEBIT,
                        onClick = { selectedChip = TransactionType.DEBIT },
                        label = stringResource(R.string.feature_history_debits),
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filteredTransactions) {
                        Column {
                            TransactionItemScreen(
                                transaction = it,
                                modifier = Modifier
                                    .padding(start = 24.dp, end = 24.dp)
                                    .clickable { transactionDetailState = it },
                            )
                            HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.padding(5.dp))
                            Spacer(modifier = Modifier.height(15.dp))
                        }
                    }
                }
            }
        }

        HistoryUiState.Loading -> {
            MifosLoadingWheel(
                modifier = Modifier.fillMaxWidth(),
                contentDesc = stringResource(R.string.feature_history_loading),
            )
        }
    }

    if (transactionDetailState != null) {
        MifosBottomSheet(
            modifier = modifier,
            content = {
                TransactionDetailScreen(
                    transaction = transactionDetailState!!,
                    viewReceipt = { transactionDetailState?.transactionId?.let { viewReceipt(it) } },
                    accountClicked = { accountClicked(it, ArrayList(transactionsList)) },
                )
            },
            onDismiss = { transactionDetailState = null },
        )
    }
}

@Composable
private fun Chip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val backgroundColor = NewUi.gradientOne
    MifosButton(
        modifier = modifier.then(
            if (selected) {
                Modifier.border(
                    width = 1.dp,
                    color = NewUi.primaryColor,
                    shape = RoundedCornerShape(25.dp),
                )
            } else {
                Modifier
            },
        ),
        onClick = {
            onClick()
            Toast.makeText(context, label, Toast.LENGTH_SHORT).show()
        },
        color = backgroundColor,
    ) {
        Text(
            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp, start = 16.dp, end = 16.dp),
            text = label,
            color = NewUi.onSurface,
        )
    }
}

internal class HistoryPreviewProvider : PreviewParameterProvider<HistoryUiState> {
    override val values: Sequence<HistoryUiState>
        get() = sequenceOf(
            HistoryUiState.Empty,
            HistoryUiState.Loading,
            HistoryUiState.Error("Error Screen"),
            HistoryUiState.HistoryList(sampleHistoryList),
        )
}

internal val sampleHistoryList = List(10) { index ->
    Transaction(
        transactionId = "txn_123456789",
        clientId = 1001L,
        accountId = index.toLong(),
        amount = 1500.0,
        date = "2024-03-23",
        currency = Currency(),
        transactionType = TransactionType.CREDIT,
        transferId = 3003L,
        transferDetail = TransferDetail(),
        receiptId = "receipt_123456789",
    )
}

@Preview(showBackground = true)
@Composable
private fun HistoryScreenPreview(
    @PreviewParameter(HistoryPreviewProvider::class) historyUiState: HistoryUiState,
) {
    HistoryScreen(historyUiState = historyUiState, viewReceipt = {}, accountClicked = { _, _ -> })
}
