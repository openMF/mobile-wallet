package org.mifospay.feature.make.transfer

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.mifospay.common.Constants
import org.mifospay.core.designsystem.component.MifosLoadingWheel
import org.mifospay.core.designsystem.component.MifosOverlayLoadingWheel

@Composable
fun MakeTransferScreenRoute(
    viewModel: MakeTransferViewModel = hiltViewModel(), 
    onDismiss: () -> Unit
) {
    val fetchPayeeClient by viewModel.fetchPayeeClient.collectAsStateWithLifecycle()
    val makeTransferState by viewModel.makeTransferState.collectAsStateWithLifecycle()
    val showTransactionStatus by viewModel.showTransactionStatus.collectAsStateWithLifecycle()

    MakeTransferScreen(
        uiState = makeTransferState,
        showTransactionStatus = showTransactionStatus,
        makeTransfer = { toClientId, transferAmount ->
            viewModel.makeTransfer(
                toClientId,
                transferAmount
            )
        },
        onDismiss = onDismiss
    )
}

@Composable
fun MakeTransferScreen(
    uiState: MakeTransferState,
    showTransactionStatus: ShowTransactionStatus,
    makeTransfer: (Long, Double) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    when (uiState) {
        MakeTransferState.Loading -> {
            MifosLoadingWheel(
                modifier = Modifier.fillMaxWidth(),
                contentDesc = stringResource(R.string.feature_make_transfer_loading)
            )
        }

        is MakeTransferState.Error -> {
            val message = uiState.message
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        is MakeTransferState.Success -> {

            val showBottomSheet = uiState.showBottomSheet
            val toClientId = uiState.toClientId
            val resultName = uiState.resultName
            val externalId = uiState.externalId
            val transferAmount = uiState.transferAmount
            val showTransactionStatus = showTransactionStatus

            MakeTransferBottomSheetContent(
                showBottomSheet,
                toClientId,
                resultName,
                externalId,
                transferAmount,
                showTransactionStatus,
                makeTransfer = makeTransfer,
                onDismiss = onDismiss
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MakeTransferBottomSheetContent(
    showBottomSheet: Boolean,
    toClientId: Long,
    resultName: String,
    externalId: String,
    transferAmount: Double,
    showTransactionStatus: ShowTransactionStatus,
    makeTransfer: (Long, Double) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    var showBottomSheet by rememberSaveable {
        mutableStateOf(showBottomSheet)
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                showBottomSheet = false
                onDismiss.invoke()
            },
            dragHandle = { BottomSheetDefaults.DragHandle() },
        ) {
            if (showTransactionStatus.showErrorStatus || showTransactionStatus.showSuccessStatus) {
                TransactionStatusContent(showTransactionStatus)
            } else {
                MakeTransferContent(
                    toClientId,
                    resultName,
                    externalId,
                    transferAmount,
                    makeTransfer = makeTransfer,
                    showBottomSheet = showBottomSheet,
                    sheetState
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MakeTransferContent(
    toClientId: Long,
    resultName: String,
    externalId: String,
    transferAmount: Double,
    makeTransfer: (Long, Double) -> Unit,
    showBottomSheet: Boolean,
    sheetState: SheetState?
) {
    val scope = rememberCoroutineScope()
    var showBottomSheet by rememberSaveable {
        mutableStateOf(showBottomSheet)
    }

    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.feature_make_transfer_send_money),
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            ),
            modifier = Modifier
                .padding(vertical = 10.dp)
                .align(Alignment.CenterHorizontally)
        )

        Box(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.feature_make_transfer_sending_to) + Constants.COLON,
                    style = TextStyle(
                        Color.Black,
                        MaterialTheme.typography.bodyMedium.fontSize
                    )
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = resultName,
                    style = TextStyle(
                        Color.Black,
                        MaterialTheme.typography.bodyLarge.fontSize
                    )
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = externalId,
                    style = TextStyle(
                        Color.Black,
                        MaterialTheme.typography.bodyLarge.fontSize
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = stringResource(id = R.string.feature_make_transfer_amount) + Constants.COLON,
                    style = TextStyle(
                        Color.Black,
                        MaterialTheme.typography.bodyMedium.fontSize
                    )
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = transferAmount.toString(),
                    style = TextStyle(
                        Color.Black,
                        MaterialTheme.typography.bodyLarge.fontSize
                    )
                )
            }
        }

        Row(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = {
                    scope.launch {
                        sheetState?.hide()
                    }.invokeOnCompletion {
                        if (sheetState != null) {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                            }
                        }
                    }
                },
                modifier = Modifier
                    .width(100.dp)
                    .height(50.dp)
            ) {
                Text(text = "Cancel")
            }

            Spacer(modifier = Modifier.width(20.dp))

            Button(
                onClick = {
                    makeTransfer(
                        toClientId,
                        transferAmount
                    )
                },
                modifier = Modifier
                    .width(100.dp)
                    .height(50.dp)
            ) {
                Text(text = "Confirm")
            }
        }
    }
}


@Composable
fun TransactionStatusContent(showTransactionStatus: ShowTransactionStatus) {
    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = if (showTransactionStatus.showSuccessStatus) {
                stringResource(id = R.string.feature_make_transfer_transaction_success)
            } else {
                stringResource(id = R.string.feature_make_transfer_transaction_unable_to_process)
            },
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            ),
            modifier = Modifier
                .padding(vertical = 10.dp)
                .align(Alignment.CenterHorizontally)
        )

        Box(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Icon(
                if (showTransactionStatus.showSuccessStatus) {
                    painterResource(R.drawable.transfer_success)
                } else {
                    painterResource(R.drawable.transfer_failure)
                },
                contentDescription = if (showTransactionStatus.showSuccessStatus) {
                    stringResource(id = R.string.feature_make_transfer_transaction_success)
                } else {
                    stringResource(id = R.string.feature_make_transfer_transaction_unable_to_process)
                },
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWithMakeTransferContentLoading() {
    MakeTransferScreen(
        uiState = MakeTransferState.Loading,
        showTransactionStatus = ShowTransactionStatus(
            showSuccessStatus = false,
            showErrorStatus = false
        ),
        makeTransfer = { _, _ -> },
        onDismiss = { }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewWithMakeTransferContentSuccess() {
    MakeTransferScreen(
        uiState = MakeTransferState.Success(
            toClientId = 1234,
            resultName = "John Doe",
            externalId = "example@example.com",
            transferAmount = 100.0,
            showBottomSheet = true,
        ),
        showTransactionStatus = ShowTransactionStatus(
            showSuccessStatus = false,
            showErrorStatus = false
        ),
        makeTransfer = { _, _ -> },
        onDismiss = { }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun PreviewMakeTransferContent() {
    MakeTransferContent(
        toClientId = 1234,
        resultName = "John Doe",
        externalId = "example@example.com",
        transferAmount = 100.0,
        makeTransfer = { _, _ -> },
        sheetState = null,
        showBottomSheet = true
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewMakeTransferBottomSheetContent() {
    MakeTransferBottomSheetContent(
        showBottomSheet = true,
        toClientId = 1234,
        resultName = "John Doe",
        externalId = "example@example.com",
        transferAmount = 100.0,
        showTransactionStatus = ShowTransactionStatus(
            showSuccessStatus = false,
            showErrorStatus = false
        ),
        makeTransfer = { _, _ -> },
        onDismiss = { }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewWithMakeTransferContentError() {
    MakeTransferScreen(
        uiState = MakeTransferState.Error("An error occurred"),
        showTransactionStatus = ShowTransactionStatus(
            showSuccessStatus = false,
            showErrorStatus = false
        ),
        makeTransfer = { _, _ -> },
        onDismiss = { }
    )
}

