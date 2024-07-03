package org.mifospay.feature.specific.transactions

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
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import org.mifospay.core.designsystem.component.MfLoadingWheel
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.core.designsystem.theme.creditTextColor
import org.mifospay.core.designsystem.theme.debitTextColor
import org.mifospay.core.designsystem.theme.otherTextColor
import org.mifospay.core.designsystem.theme.primaryDarkBlue
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.core.ui.ErrorScreenContent
import org.mifospay.feature.history.R


@Composable
fun SpecificTransactionsScreen(
    viewModel: SpecificTransactionsViewModel = hiltViewModel(),
    backPress: () -> Unit,
    transactionItemClicked: (String) -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.getSpecificTransactions()
    }

    SpecificTransactionsScreen(
        uiState = uiState.value,
        backPress = backPress,
        transactionItemClicked = transactionItemClicked
    )
}

@Composable
fun SpecificTransactionsScreen(
    uiState: SpecificTransactionsUiState,
    backPress: () -> Unit,
    transactionItemClicked: (String) -> Unit,
) {
    MifosScaffold(
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
                            backgroundColor = Color.White
                        )
                    }

                    is SpecificTransactionsUiState.Success -> {
                        if (uiState.transactionsList.isEmpty()) {
                            EmptyContentScreen(
                                modifier = Modifier,
                                title = stringResource(id = R.string.feature_history_error_oops),
                                subTitle = stringResource(id = R.string.feature_history_no_transactions_found),
                                iconTint = Color.Black,
                                iconImageVector = Icons.Rounded.Info
                            )
                        } else {
                            SpecificTransactionsContent(
                                transactionList = uiState.transactionsList,
                                transactionItemClicked = transactionItemClicked
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun SpecificTransactionsContent(
    transactionList: ArrayList<Transaction>,
    transactionItemClicked: (String) -> Unit
) {
    LazyColumn {
        itemsIndexed(items = transactionList) { index, transaction ->
            SpecificTransactionItem(
                transaction = transaction,
                modifier = Modifier
                    .padding(12.dp)
                    .clickable {
                        transaction.transactionId?.let { transactionItemClicked(it) }
                    }
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (index != transactionList.lastIndex) {
                Divider()
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun SpecificTransactionItem(
    transaction: Transaction,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SpecificTransactionAccountInfo(
                modifier = Modifier.weight(1f),
                account = transaction.transferDetail.fromAccount,
                client = transaction.transferDetail.fromClient
            )
            Icon(imageVector = MifosIcons.SendRightTilted, contentDescription = null)
            SpecificTransactionAccountInfo(
                modifier = Modifier.weight(1f),
                account = transaction.transferDetail.toAccount,
                client = transaction.transferDetail.toClient
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(id = R.string.feature_receipt_transaction_id) + transaction.transactionId,
                    style = MaterialTheme.typography.bodyLarge,
                    color = primaryDarkBlue
                )
                Text(
                    text = stringResource(id = R.string.feature_receipt_transaction_date) + transaction.date,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = when (transaction.transactionType) {
                        TransactionType.DEBIT -> stringResource(id = R.string.feature_history_debits)
                        TransactionType.CREDIT -> stringResource(id = R.string.feature_history_credits)
                        TransactionType.OTHER -> stringResource(id = R.string.feature_receipt_other)
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
fun SpecificTransactionAccountInfo(
    modifier: Modifier = Modifier,
    account: SavingAccount,
    client: Client,
    accountClicked: (String) -> Unit = {}
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

class SpecificTransactionsUiStateProvider :
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
fun ShowQrScreenPreview(@PreviewParameter(SpecificTransactionsUiStateProvider::class) uiState: SpecificTransactionsUiState) {
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
fun SpecificTransactionItemPreview() {
    Surface {
        SpecificTransactionItem(transaction = Transaction())
    }
}