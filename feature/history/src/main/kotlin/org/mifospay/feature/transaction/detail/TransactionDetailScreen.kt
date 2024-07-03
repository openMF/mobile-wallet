package org.mifospay.feature.transaction.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifospay.core.model.domain.Transaction
import com.mifospay.core.model.domain.TransactionType
import com.mifospay.core.model.entity.accounts.savings.TransferDetail
import org.mifospay.common.Constants
import org.mifospay.core.designsystem.component.MfLoadingWheel
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.core.designsystem.theme.primaryDarkBlue
import org.mifospay.core.ui.ErrorScreenContent
import org.mifospay.feature.history.R
import org.mifospay.feature.specific.transactions.SpecificTransactionAccountInfo


@Composable
fun TransactionDetailScreen(
    viewModel: TransactionDetailViewModel = hiltViewModel(),
    transaction: Transaction,
    viewReceipt: () -> Unit,
    accountClicked: (String) -> Unit,
) {

    val uiState = viewModel.transactionDetailUiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = transaction) {
        viewModel.getTransferDetail(transaction.transferId)
    }

    TransactionDetailScreen(
        uiState = uiState.value,
        transaction = transaction,
        viewReceipt = viewReceipt,
        accountClicked = accountClicked
    )
}

@Composable
fun TransactionDetailScreen(
    uiState: TransactionDetailUiState,
    transaction: Transaction,
    viewReceipt: () -> Unit,
    accountClicked: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(20.dp)
            .height(300.dp)
    ) {
        when (uiState) {
            is TransactionDetailUiState.Error -> {
                ErrorScreenContent()
            }

            is TransactionDetailUiState.Loading -> {
                MfLoadingWheel(
                    backgroundColor = Color.Black.copy(alpha = 0.6f)
                )
            }

            is TransactionDetailUiState.Success -> {
                TransactionsDetailContent(
                    transaction = transaction,
                    viewReceipt = viewReceipt,
                    accountClicked = accountClicked
                )
            }
        }
    }
}

@Composable
fun TransactionsDetailContent(
    modifier: Modifier = Modifier,
    transaction: Transaction,
    viewReceipt: () -> Unit,
    accountClicked: (String) -> Unit,
) {
    Column(
        modifier = modifier.padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .fillMaxWidth(),
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
                if (transaction.receiptId != null) {
                    Text(
                        text = stringResource(id = R.string.feature_receipt_pan_id) + transaction.receiptId,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
                Text(
                    text = when (transaction.transactionType) {
                        TransactionType.DEBIT -> Constants.DEBIT
                        TransactionType.CREDIT -> Constants.CREDIT
                        TransactionType.OTHER -> Constants.OTHER
                    },
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            SpecificTransactionAccountInfo(
                modifier = Modifier.weight(1f),
                account = transaction.transferDetail.fromAccount,
                client = transaction.transferDetail.fromClient,
                accountClicked = accountClicked
            )
            Image(painter = painterResource(id = R.drawable.ic_send), contentDescription = null)
            SpecificTransactionAccountInfo(
                modifier = Modifier.weight(1f),
                account = transaction.transferDetail.toAccount,
                client = transaction.transferDetail.toClient,
                accountClicked = accountClicked
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        Text(
            text = stringResource(id = R.string.feature_receipt_view_Receipt),
            style = MaterialTheme.typography.bodyLarge,
            color = primaryDarkBlue,
            textAlign = TextAlign.Center,
            modifier = Modifier.clickable { viewReceipt() }
        )

    }
}


class TransactionDetailUiStateProvider :
    PreviewParameterProvider<TransactionDetailUiState> {
    override val values: Sequence<TransactionDetailUiState>
        get() = sequenceOf(
            TransactionDetailUiState.Success(TransferDetail()),
            TransactionDetailUiState.Error,
            TransactionDetailUiState.Loading,
        )
}

@Preview(showSystemUi = true)
@Composable
fun ShowQrScreenPreview(@PreviewParameter(TransactionDetailUiStateProvider::class) uiState: TransactionDetailUiState) {
    MifosTheme {
        TransactionDetailScreen(
            uiState = uiState,
            transaction = Transaction(),
            viewReceipt = {},
            accountClicked = {},
        )
    }
}