/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.specific.transactions

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifospay.core.model.domain.Transaction
import com.mifospay.core.model.domain.TransactionType
import com.mifospay.core.model.domain.client.Client
import com.mifospay.core.model.entity.accounts.savings.SavingAccount
import org.mifospay.common.Utils.toArrayList
import org.mifospay.core.designsystem.component.MfLoadingWheel
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.core.designsystem.theme.creditTextColor
import org.mifospay.core.designsystem.theme.debitTextColor
import org.mifospay.core.designsystem.theme.otherTextColor
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.core.ui.ErrorScreenContent
import org.mifospay.feature.history.R

@Composable
internal fun SpecificTransactionsScreen(
    accountNumber: String,
    transactions: List<Transaction>,
    backPress: () -> Unit,
    transactionItemClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SpecificTransactionsViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.setArguments(transactions.toArrayList(), accountNumber)
        viewModel.getSpecificTransactions()
    }

    SpecificTransactionsScreen(
        uiState = uiState.value,
        backPress = backPress,
        transactionItemClicked = transactionItemClicked,
        modifier = modifier,
    )
}

@Composable
@VisibleForTesting
internal fun SpecificTransactionsScreen(
    uiState: SpecificTransactionsUiState,
    backPress: () -> Unit,
    transactionItemClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosScaffold(
        modifier = modifier,
        topBarTitle = R.string.feature_history_specific_transactions_history,
        backPress = backPress,
        scaffoldContent = { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                when (uiState) {
                    SpecificTransactionsUiState.Error -> {
                        ErrorScreenContent(
                            modifier = Modifier,
                            title = stringResource(id = R.string.feature_history_error_oops),
                            subTitle = stringResource(id = R.string.feature_history_unexpected_error_subtitle),
                        )
                    }

                    SpecificTransactionsUiState.Loading -> {
                        MfLoadingWheel(
                            contentDesc = stringResource(R.string.feature_history_loading),
                            backgroundColor = MaterialTheme.colorScheme.surface,
                        )
                    }

                    is SpecificTransactionsUiState.Success -> {
                        if (uiState.transactionsList.isEmpty()) {
                            EmptyContentScreen(
                                modifier = Modifier,
                                title = stringResource(id = R.string.feature_history_error_oops),
                                subTitle = stringResource(id = R.string.feature_history_no_transactions_found),
                                iconTint = MaterialTheme.colorScheme.onSurface,
                                iconImageVector = Icons.Rounded.Info,
                            )
                        } else {
                            SpecificTransactionsContent(
                                transactionList = uiState.transactionsList,
                                transactionItemClicked = transactionItemClicked,
                            )
                        }
                    }
                }
            }
        },
    )
}

@Composable
private fun SpecificTransactionsContent(
    transactionList: List<Transaction>,
    transactionItemClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        itemsIndexed(
            items = transactionList,
        ) { index, transaction ->
            SpecificTransactionItem(
                transaction = transaction,
                modifier = Modifier
                    .padding(12.dp)
                    .clickable {
                        transaction.transactionId?.let { transactionItemClicked(it) }
                    },
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (index != transactionList.lastIndex) {
                HorizontalDivider()
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun SpecificTransactionItem(
    transaction: Transaction,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(horizontal = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SpecificTransactionAccountInfo(
                account = transaction.transferDetail.fromAccount,
                client = transaction.transferDetail.fromClient,
                modifier = Modifier.weight(1f),
            )
            Icon(imageVector = MifosIcons.SendRightTilted, contentDescription = null)
            SpecificTransactionAccountInfo(
                account = transaction.transferDetail.toAccount,
                client = transaction.transferDetail.toClient,
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = stringResource(id = R.string.feature_history_transaction_id) + transaction.transactionId,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = stringResource(id = R.string.feature_history_transaction_date) + transaction.date,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = when (transaction.transactionType) {
                        TransactionType.DEBIT -> stringResource(id = R.string.feature_history_debits)
                        TransactionType.CREDIT -> stringResource(id = R.string.feature_history_credits)
                        TransactionType.OTHER -> stringResource(id = R.string.feature_history_other)
                    },
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${transaction.currency.code}${transaction.amount}",
                style = MaterialTheme.typography.displaySmall,
                color = when (transaction.transactionType) {
                    TransactionType.DEBIT -> debitTextColor
                    TransactionType.CREDIT -> creditTextColor
                    TransactionType.OTHER -> otherTextColor
                },
            )
        }
    }
}

@Composable
internal fun SpecificTransactionAccountInfo(
    account: SavingAccount,
    client: Client,
    modifier: Modifier = Modifier,
    accountClicked: (String) -> Unit = {},
) {
    Column(
        modifier = modifier.clickable {
            accountClicked(account.accountNo)
        },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(imageVector = MifosIcons.AccountCircle, contentDescription = null)
        Text(
            text = client.displayName,
            style = MaterialTheme.typography.titleSmall,
        )
        Text(
            text = account.accountNo,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

internal class SpecificTransactionsUiStateProvider :
    PreviewParameterProvider<SpecificTransactionsUiState> {
    override val values: Sequence<SpecificTransactionsUiState>
        get() = sequenceOf(
            SpecificTransactionsUiState.Success(arrayListOf()),
            SpecificTransactionsUiState.Success(arrayListOf(Transaction(), Transaction())),
            SpecificTransactionsUiState.Error,
            SpecificTransactionsUiState.Loading,
        )
}

@Preview(showSystemUi = true)
@Composable
private fun ShowQrScreenPreview(
    @PreviewParameter(SpecificTransactionsUiStateProvider::class)
    uiState: SpecificTransactionsUiState,
) {
    MifosTheme {
        SpecificTransactionsScreen(
            uiState = uiState,
            backPress = {},
            transactionItemClicked = {},
        )
    }
}

@Preview
@Composable
private fun SpecificTransactionItemPreview() {
    Surface {
        SpecificTransactionItem(transaction = Transaction())
    }
}
