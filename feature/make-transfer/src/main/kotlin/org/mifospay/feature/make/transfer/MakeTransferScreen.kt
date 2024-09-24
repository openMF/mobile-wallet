/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import org.mifospay.common.Constants
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosLoadingWheel

@Composable
internal fun MakeTransferScreenRoute(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MakeTransferViewModel = koinViewModel(),
) {
    // TODO: commented out because not using it
    // val fetchPayeeClient by viewModel.fetchPayeeClient.collectAsStateWithLifecycle()
    val makeTransferState by viewModel.makeTransferState.collectAsStateWithLifecycle()
    val showTransactionStatus by viewModel.showTransactionStatus.collectAsStateWithLifecycle()

    MakeTransferScreen(
        state = makeTransferState,
        showTransactionStatus = showTransactionStatus,
        makeTransfer = viewModel::makeTransfer,
        onDismiss = onDismiss,
        modifier = modifier,
    )
}

@Composable
private fun MakeTransferScreen(
    state: MakeTransferState,
    showTransactionStatus: ShowTransactionStatus,
    makeTransfer: (Long, Double) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    when (state) {
        MakeTransferState.Loading -> {
            MifosLoadingWheel(
                modifier = Modifier.fillMaxWidth(),
                contentDesc = stringResource(R.string.feature_make_transfer_loading),
            )
        }

        is MakeTransferState.Error -> {
            val message = state.message
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        is MakeTransferState.Success -> {
            MakeTransferBottomSheetContent(
                showBottomSheet = state.showBottomSheet,
                toClientId = state.toClientId,
                resultName = state.resultName,
                externalId = state.externalId,
                transferAmount = state.transferAmount,
                showTransactionStatus = showTransactionStatus,
                makeTransfer = makeTransfer,
                onDismiss = onDismiss,
                modifier = modifier,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MakeTransferBottomSheetContent(
    showBottomSheet: Boolean,
    toClientId: Long,
    resultName: String,
    externalId: String,
    transferAmount: Double,
    showTransactionStatus: ShowTransactionStatus,
    makeTransfer: (Long, Double) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState()

    var visibleBottomSheet by rememberSaveable { mutableStateOf(showBottomSheet) }

    if (visibleBottomSheet) {
        ModalBottomSheet(
            modifier = modifier,
            sheetState = sheetState,
            onDismissRequest = {
                visibleBottomSheet = false
                onDismiss.invoke()
            },
            dragHandle = { BottomSheetDefaults.DragHandle() },
        ) {
            if (showTransactionStatus.showErrorStatus ||
                showTransactionStatus.showSuccessStatus
            ) {
                TransactionStatusContent(showTransactionStatus)
            } else {
                MakeTransferContent(
                    toClientId = toClientId,
                    resultName = resultName,
                    externalId = externalId,
                    transferAmount = transferAmount,
                    makeTransfer = makeTransfer,
                    onCloseBottomSheet = {
                        visibleBottomSheet = false
                        onDismiss.invoke()
                    },
                )
            }
        }
    }
}

@Composable
private fun MakeTransferContent(
    toClientId: Long,
    resultName: String,
    externalId: String,
    transferAmount: Double,
    makeTransfer: (Long, Double) -> Unit,
    onCloseBottomSheet: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(20.dp)
            .fillMaxWidth(),
    ) {
        Text(
            text = stringResource(id = R.string.feature_make_transfer_send_money),
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            ),
            modifier = Modifier
                .padding(vertical = 10.dp)
                .align(Alignment.CenterHorizontally),
        )

        Box(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth()
                .height(180.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.feature_make_transfer_sending_to) + Constants.COLON,
                    style = TextStyle(
                        MaterialTheme.colorScheme.onSurface,
                        MaterialTheme.typography.bodyMedium.fontSize,
                    ),
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = resultName,
                    style = TextStyle(
                        MaterialTheme.colorScheme.onSurface,
                        MaterialTheme.typography.bodyLarge.fontSize,
                    ),
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = externalId,
                    style = TextStyle(
                        MaterialTheme.colorScheme.onSurface,
                        MaterialTheme.typography.bodyLarge.fontSize,
                    ),
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = stringResource(id = R.string.feature_make_transfer_amount) + Constants.COLON,
                    style = TextStyle(
                        MaterialTheme.colorScheme.onSurface,
                        MaterialTheme.typography.bodyMedium.fontSize,
                    ),
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = transferAmount.toString(),
                    style = TextStyle(
                        MaterialTheme.colorScheme.onSurface,
                        MaterialTheme.typography.bodyLarge.fontSize,
                    ),
                )
            }
        }

        Row(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            MifosButton(
                onClick = onCloseBottomSheet,
                modifier = Modifier
                    .width(100.dp)
                    .height(50.dp),
            ) {
                Text(text = "Cancel")
            }

            Spacer(modifier = Modifier.width(20.dp))

            MifosButton(
                onClick = {
                    makeTransfer(
                        toClientId,
                        transferAmount,
                    )
                },
                modifier = Modifier
                    .width(100.dp)
                    .height(50.dp),
            ) {
                Text(text = "Confirm")
            }
        }
    }
}

@Composable
private fun TransactionStatusContent(showTransactionStatus: ShowTransactionStatus) {
    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth(),
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
                color = MaterialTheme.colorScheme.onSurface,
            ),
            modifier = Modifier
                .padding(vertical = 10.dp)
                .align(Alignment.CenterHorizontally),
        )

        Box(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth()
                .height(180.dp),
        ) {
            Icon(
                if (showTransactionStatus.showSuccessStatus) {
                    painterResource(R.drawable.feature_make_transfer_transfer_success)
                } else {
                    painterResource(R.drawable.feature_make_transfer_transfer_failure)
                },
                contentDescription = if (showTransactionStatus.showSuccessStatus) {
                    stringResource(id = R.string.feature_make_transfer_transaction_success)
                } else {
                    stringResource(id = R.string.feature_make_transfer_transaction_unable_to_process)
                },
                modifier = Modifier
                    .align(Alignment.Center),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithMakeTransferContentLoading() {
    MakeTransferScreen(
        state = MakeTransferState.Loading,
        showTransactionStatus = ShowTransactionStatus(
            showSuccessStatus = false,
            showErrorStatus = false,
        ),
        makeTransfer = { _, _ -> },
        onDismiss = { },
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithMakeTransferContentSuccess() {
    MakeTransferScreen(
        state = MakeTransferState.Success(
            toClientId = 1234,
            resultName = "John Doe",
            externalId = "example@example.com",
            transferAmount = 100.0,
            showBottomSheet = true,
        ),
        showTransactionStatus = ShowTransactionStatus(
            showSuccessStatus = false,
            showErrorStatus = false,
        ),
        makeTransfer = { _, _ -> },
        onDismiss = { },
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewMakeTransferContent() {
    MakeTransferContent(
        toClientId = 1234,
        resultName = "John Doe",
        externalId = "example@example.com",
        transferAmount = 100.0,
        makeTransfer = { _, _ -> },
        onCloseBottomSheet = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewMakeTransferBottomSheetContent() {
    MakeTransferBottomSheetContent(
        showBottomSheet = true,
        toClientId = 1234,
        resultName = "John Doe",
        externalId = "example@example.com",
        transferAmount = 100.0,
        showTransactionStatus = ShowTransactionStatus(
            showSuccessStatus = false,
            showErrorStatus = false,
        ),
        makeTransfer = { _, _ -> },
        onDismiss = { },
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithMakeTransferContentError() {
    MakeTransferScreen(
        state = MakeTransferState.Error("An error occurred"),
        showTransactionStatus = ShowTransactionStatus(
            showSuccessStatus = false,
            showErrorStatus = false,
        ),
        makeTransfer = { _, _ -> },
        onDismiss = { },
    )
}
