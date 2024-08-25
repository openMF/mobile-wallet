/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.merchants.ui

import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifospay.core.model.domain.Transaction
import com.mifospay.core.model.domain.TransactionType
import com.mifospay.core.model.domain.client.Client
import com.mifospay.core.model.entity.accounts.savings.SavingAccount
import org.mifospay.core.designsystem.component.MfLoadingWheel
import org.mifospay.core.designsystem.component.MfOutlinedTextField
import org.mifospay.core.designsystem.component.MifosBottomSheet
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.designsystem.theme.ElectricViolet
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.core.designsystem.theme.creditTextColor
import org.mifospay.core.designsystem.theme.debitTextColor
import org.mifospay.core.designsystem.theme.otherTextColor
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.core.ui.ErrorScreenContent
import org.mifospay.feature.merchants.MerchantTransferUiState
import org.mifospay.feature.merchants.MerchantTransferViewModel
import org.mifospay.feature.merchants.R

@Composable
internal fun MerchantTransferScreenRoute(
    onBackPressed: () -> Unit,
    proceedWithMakeTransferFlow: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MerchantTransferViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val merchantName by viewModel.merchantName.collectAsStateWithLifecycle()
    val merchantVPA by viewModel.merchantVPA.collectAsStateWithLifecycle()

    MerchantTransferScreen(
        uiState = uiState,
        merchantName = merchantName,
        merchantVPA = merchantVPA,
        onBackPressed = onBackPressed,
        checkBalanceAvailability = { vpa, transferAmount ->
            viewModel.checkBalanceAvailability(
                proceedWithMakeTransferFlow = { externalId, amount ->
                    proceedWithMakeTransferFlow.invoke(externalId, amount.toString())
                },
                externalId = vpa,
                transferAmount = transferAmount.toDoubleOrNull() ?: 0.0,
            )
        },
        modifier = modifier,
    )
}

@Composable
@VisibleForTesting
internal fun MerchantTransferScreen(
    uiState: MerchantTransferUiState,
    merchantName: String,
    merchantVPA: String,
    onBackPressed: () -> Unit,
    checkBalanceAvailability: (String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showBottomSheet by remember { mutableStateOf(true) }
    var amount by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    MifosScaffold(
        modifier = modifier,
        topBarTitle = R.string.feature_merchants_merchant_transaction,
        backPress = onBackPressed,
        scaffoldContent = { paddingValues ->
            Box(
                modifier = Modifier.padding(paddingValues),
            ) {
                when (uiState) {
                    is MerchantTransferUiState.Loading -> {
                        MfLoadingWheel(
                            contentDesc = stringResource(R.string.feature_merchants_loading),
                            backgroundColor = MaterialTheme.colorScheme.surface,
                        )
                    }

                    is MerchantTransferUiState.Error -> {
                        ErrorScreenContent(
                            modifier = Modifier,
                            title = stringResource(id = R.string.feature_merchants_error_oops),
                            subTitle = stringResource(id = R.string.feature_merchants_unexpected_error_subtitle),
                        )
                    }

                    is MerchantTransferUiState.Empty -> {
                        EmptyContentScreen(
                            title = stringResource(id = R.string.feature_merchants_error_oops),
                            subTitle = stringResource(id = R.string.feature_merchants_no_transactions_found),
                            modifier = Modifier,
                            iconTint = MaterialTheme.colorScheme.onSurface,
                            iconImageVector = MifosIcons.Info,
                        )
                    }

                    is MerchantTransferUiState.Success -> {
                        TransactionList(uiState.transactionsList)
                    }

                    is MerchantTransferUiState.InsufficientBalance -> {
                        Toast
                            .makeText(
                                context,
                                stringResource(id = R.string.feature_merchants_insufficient_balance),
                                Toast.LENGTH_SHORT,
                            ).show()
                    }
                }

                if (showBottomSheet) {
                    MerchantBottomSheet(
                        merchantName = merchantName,
                        merchantVPA = merchantVPA,
                        amount = amount,
                        onAmountChange = { amount = it },
                        checkBalanceAvailability = checkBalanceAvailability,
                        onDismiss = { showBottomSheet = false },
                    )
                }
            }
        },
    )
}

@Composable
private fun TransactionList(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier) {
        items(
            items = transactions,
            key = { it.transactionId ?: it.transferId },
        ) { transaction ->
            SpecificTransactionItem(transaction)
            HorizontalDivider()
        }
    }
}

@Composable
private fun MerchantBottomSheet(
    merchantName: String,
    merchantVPA: String,
    amount: String,
    onAmountChange: (String) -> Unit,
    checkBalanceAvailability: (String, String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosBottomSheet(
        content = {
            Column(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.feature_merchants_transfer_money_to_this_merchant),
                    color = ElectricViolet,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(24.dp))

                MerchantInfo(
                    merchantName,
                    merchantVPA,
                )

                Spacer(modifier = Modifier.height(24.dp))
                MfOutlinedTextField(
                    value = amount,
                    label = stringResource(id = R.string.feature_merchants_amount),
                    onValueChange = onAmountChange,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                Spacer(modifier = Modifier.height(24.dp))
                MifosButton(
                    onClick = {
                        checkBalanceAvailability(
                            merchantName,
                            merchantVPA,
                        )
                    },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.width(155.dp),
                ) {
                    Text(
                        stringResource(id = R.string.feature_merchants_submit),
                        color = Color.White,
                    )
                }
            }
        },
        onDismiss = onDismiss,
        modifier = modifier,
    )
}

@Composable
private fun MerchantInfo(
    merchantName: String,
    merchantVPA: String,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier,
    ) {
        MerchantInitialAvatar(merchantName)

        Text(
            text = merchantName,
            style = MaterialTheme.typography.labelMedium,
        )

        Text(
            text = merchantVPA,
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray,
        )
    }
}

@Composable
private fun MerchantInitialAvatar(
    merchantName: String,
    modifier: Modifier = Modifier,
) {
    val initial = merchantName.take(1).uppercase()

    Box(
        modifier =
        modifier
            .size(86.dp)
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initial,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 44.sp,
            fontWeight = FontWeight.Medium,
        )
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
                    text = stringResource(id = R.string.feature_merchants_transaction_id) + transaction.transactionId,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = stringResource(id = R.string.feature_merchants_transaction_date) + transaction.date,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text =
                    when (transaction.transactionType) {
                        TransactionType.DEBIT -> stringResource(id = R.string.feature_merchants_debits)
                        TransactionType.CREDIT -> stringResource(id = R.string.feature_merchants_credits)
                        TransactionType.OTHER -> stringResource(id = R.string.feature_merchants_other)
                    },
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${transaction.currency.code}${transaction.amount}",
                style = MaterialTheme.typography.displaySmall,
                color =
                when (transaction.transactionType) {
                    TransactionType.DEBIT -> debitTextColor
                    TransactionType.CREDIT -> creditTextColor
                    TransactionType.OTHER -> otherTextColor
                },
            )
        }
    }
}

@Composable
private fun SpecificTransactionAccountInfo(
    account: SavingAccount,
    client: Client,
    modifier: Modifier = Modifier,
    accountClicked: (String) -> Unit = {},
) {
    Column(
        modifier =
        modifier.clickable {
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

internal class MerchantTransferUiStateProvider : PreviewParameterProvider<MerchantTransferUiState> {
    override val values: Sequence<MerchantTransferUiState>
        get() =
            sequenceOf(
                MerchantTransferUiState.Success(arrayListOf(Transaction())),
                MerchantTransferUiState.Error,
                MerchantTransferUiState.Loading,
                MerchantTransferUiState.Empty,
                MerchantTransferUiState.InsufficientBalance,
            )
}

@Preview(showSystemUi = true)
@Composable
private fun Preview(
    @PreviewParameter(MerchantTransferUiStateProvider::class)
    uiState: MerchantTransferUiState,
) {
    MifosTheme {
        MerchantTransferScreen(
            uiState = uiState,
            merchantName = "New User",
            merchantVPA = "naman.dwivedi2@mifos",
            onBackPressed = {},
            checkBalanceAvailability = { _, _ -> },
        )
    }
}

@Preview
@Composable
private fun TransactionItemPreview() {
    MifosTheme {
        SpecificTransactionItem(Transaction())
    }
}

@Preview
@Composable
private fun MerchantBottomSheetPreview() {
    MifosTheme {
        MerchantBottomSheet(
            merchantName = "Naman Dwivedi 2",
            merchantVPA = "naman.dwivedi2@mifos",
            amount = "",
            onAmountChange = {},
            checkBalanceAvailability = { _, _ -> },
            onDismiss = {},
        )
    }
}

@Preview
@Composable
private fun MerchantInitialAvatarPreview() {
    MifosTheme {
        MerchantInitialAvatar("Naman")
    }
}
