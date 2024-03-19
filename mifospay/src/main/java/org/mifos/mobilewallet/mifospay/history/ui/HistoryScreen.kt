package org.mifos.mobilewallet.mifospay.history.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mifos.mobilewallet.model.domain.Transaction
import com.mifos.mobilewallet.model.domain.TransactionType
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.designsystem.component.MifosErrorLayout
import org.mifos.mobilewallet.mifospay.designsystem.component.MifosLoadingWheel
import org.mifos.mobilewallet.mifospay.designsystem.theme.chipSelectedColor
import org.mifos.mobilewallet.mifospay.designsystem.theme.lightGrey
import org.mifos.mobilewallet.mifospay.history.presenter.HistoryUiState

@Composable
fun HistoryScreen(
    historyUiState: HistoryUiState
) {
    var selectedChip by remember { mutableStateOf(TransactionType.OTHER) }
    var filteredTransactions by remember { mutableStateOf(emptyList<Transaction>()) }

    when (historyUiState) {
        HistoryUiState.EmptyList -> {
            MifosErrorLayout(
                modifier = Modifier.fillMaxSize(),
                icon = R.drawable.ic_empty_state,
                error = R.string.empty_no_transaction_history_title
            )
        }

        is HistoryUiState.Error -> {
            MifosErrorLayout(
                modifier = Modifier.fillMaxSize(),
                icon = R.drawable.ic_empty_state,
                error = R.string.error_no_transaction_history_subtitle
            )
        }

        is HistoryUiState.HistoryList -> {
            LaunchedEffect(selectedChip) {
                filteredTransactions = when (selectedChip) {
                    TransactionType.OTHER -> historyUiState.list!!
                    else -> historyUiState.list!!.filter { it.transactionType == selectedChip }
                }
            }
            Column(modifier = Modifier.fillMaxSize()) {
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

        HistoryUiState.Initial -> {}
        HistoryUiState.Loading -> {
            MifosLoadingWheel(
                modifier = Modifier.fillMaxWidth(),
                contentDesc = stringResource(R.string.loading)
            )
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

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun HistoryScreenPreview() {
    HistoryScreen(historyUiState = HistoryUiState.Loading)
}