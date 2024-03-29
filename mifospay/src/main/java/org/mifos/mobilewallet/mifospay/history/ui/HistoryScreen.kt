package org.mifos.mobilewallet.mifospay.history.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.mifos.mobilewallet.model.domain.Currency
import com.mifos.mobilewallet.model.domain.Transaction
import com.mifos.mobilewallet.model.domain.TransactionType
import com.mifos.mobilewallet.model.entity.accounts.savings.TransferDetail
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.designsystem.component.MifosLoadingWheel
import org.mifos.mobilewallet.mifospay.designsystem.theme.chipSelectedColor
import org.mifos.mobilewallet.mifospay.designsystem.theme.lightGrey
import org.mifos.mobilewallet.mifospay.history.presenter.HistoryUiState
import org.mifos.mobilewallet.mifospay.history.presenter.HistoryViewModel
import org.mifos.mobilewallet.mifospay.ui.EmptyContentScreen

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val historyUiState by viewModel.historyUiState.collectAsStateWithLifecycle()

    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = viewModel::refreshTransactionHistory
    ){
        HistoryScreen(
            historyUiState = historyUiState
        )
    }
}

@Composable
fun HistoryScreen(
    historyUiState: HistoryUiState
) {
    var selectedChip by remember { mutableStateOf(TransactionType.OTHER) }
    var filteredTransactions by remember { mutableStateOf(emptyList<Transaction>()) }

    Box(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ){
        when (historyUiState) {
            HistoryUiState.Empty -> {
                EmptyContentScreen(
                    modifier = Modifier,
                    title = stringResource(id = R.string.error_oops),
                    subTitle = stringResource(id = R.string.empty_no_transaction_history_title),
                    iconTint = Color.Black,
                    iconImageVector = Icons.Rounded.Info
                )
            }

            is HistoryUiState.Error -> {
                EmptyContentScreen(
                    modifier = Modifier,
                    title = stringResource(id = R.string.error_oops),
                    subTitle = stringResource(id = R.string.unexpected_error_subtitle),
                    iconTint = Color.Black,
                    iconImageVector = Icons.Rounded.Info
                )
            }

            is HistoryUiState.HistoryList -> {
                LaunchedEffect(selectedChip) {
                    filteredTransactions = when (selectedChip) {
                        TransactionType.OTHER -> historyUiState.list!!
                        else -> historyUiState.list!!.filter { it.transactionType == selectedChip }
                    }
                }
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Chip(
                            selected = selectedChip == TransactionType.OTHER,
                            onClick = { selectedChip = TransactionType.OTHER },
                            label = stringResource(R.string.all)
                        )
                        Chip(
                            selected = selectedChip == TransactionType.CREDIT,
                            onClick = { selectedChip = TransactionType.CREDIT },
                            label = stringResource(R.string.credits)
                        )
                        Chip(
                            selected = selectedChip == TransactionType.DEBIT,
                            onClick = { selectedChip = TransactionType.DEBIT },
                            label = stringResource(R.string.debits)
                        )
                    }
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(filteredTransactions) {
                            HistoryItem(
                                date = it.date.toString(),
                                amount = it.amount.toString(),
                                transactionType = it.transactionType
                            )
                        }
                    }
                }

            }

            HistoryUiState.Loading -> {
                MifosLoadingWheel(
                    modifier = Modifier.fillMaxWidth(),
                    contentDesc = stringResource(R.string.loading)
                )
            }
        }
    }
}

@Composable
fun Chip(selected: Boolean, onClick: () -> Unit, label: String) {
    val context = LocalContext.current
    val backgroundColor = if (selected) chipSelectedColor else lightGrey
    Button(
        onClick = {
            onClick()
            Toast.makeText(context, label, Toast.LENGTH_SHORT).show()
        },
        colors = ButtonDefaults.buttonColors(backgroundColor)
    ) {
        Text(
            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp, start = 16.dp, end = 16.dp),
            text = label,
            color = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenLoadingPreview() {
    HistoryScreen(historyUiState = HistoryUiState.Loading)
}

@Preview(showBackground = true)
@Composable
private fun HistoryScreenEmptyPreview() {
    HistoryScreen(historyUiState = HistoryUiState.Empty)
}

@Preview(showBackground = true)
@Composable
private fun HistoryScreenErrorPreview() {
    HistoryScreen(historyUiState = HistoryUiState.Error("Error Screen"))
}

@Preview(showBackground = true)
@Composable
private fun HistoryScreenListPreview() {
    HistoryScreen(historyUiState = HistoryUiState.HistoryList(sampleHistoryList))
}

val sampleHistoryList = List(10) {index ->
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
        receiptId = "receipt_123456789"
    )
}