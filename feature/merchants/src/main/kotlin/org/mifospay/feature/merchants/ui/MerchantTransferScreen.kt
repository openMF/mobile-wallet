package org.mifospay.feature.merchants.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import org.mifospay.core.designsystem.component.MfLoadingWheel
import org.mifospay.core.designsystem.component.MfOutlinedTextField
import org.mifospay.core.designsystem.component.MifosBottomSheet
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.designsystem.theme.ElectricViolet
import org.mifospay.core.designsystem.theme.InitialAvatarBgColor
import org.mifospay.core.designsystem.theme.MifosTheme
import org.mifospay.core.designsystem.theme.submitButtonColor
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.core.ui.ErrorScreenContent
import org.mifospay.feature.merchants.MerchantTransferUiState
import org.mifospay.feature.merchants.MerchantTransferViewModel
import org.mifospay.feature.merchants.R
import org.mifospay.feature.specific.transactions.SpecificTransactionItem

@Composable
fun MerchantTransferScreenRoute(
    proceedWithMakeTransferFlow: (String, String) -> Unit,
    viewModel: MerchantTransferViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
    merchantName: String,
    merchantVPA: String,
    merchantAccountNumber: String
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.fetchMerchantTransfers(merchantAccountNumber)
    }

    MerchantTransferScreen(
        uiState = uiState,
        onBackPressed = onBackPressed,
        merchantName = merchantName,
        merchantVPA = merchantVPA,
        checkBalanceAvailability = { merchantVPA, transferAmount ->
            viewModel.checkBalanceAvailability(
                proceedWithMakeTransferFlow = { externalId, transferAmount ->
                    proceedWithMakeTransferFlow.invoke(externalId, transferAmount.toString())
                },
                merchantVPA,
                transferAmount.toDoubleOrNull() ?: 0.0
            )
        }
    )
}

@Composable
fun MerchantTransferScreen(
    uiState: MerchantTransferUiState,
    onBackPressed: () -> Unit,
    merchantName: String,
    merchantVPA: String,
    checkBalanceAvailability: (String, String) -> Unit
) {
    var showBottomSheet by remember { mutableStateOf(true) }
    var amount by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    MifosScaffold(
        topBarTitle = R.string.feature_merchants_merchant_transaction,
        backPress = { onBackPressed.invoke() },
        scaffoldContent = { paddingValues ->
            Box(
                modifier = Modifier.padding(paddingValues)
            ) {
                when (uiState) {
                    is MerchantTransferUiState.Loading -> {
                        MfLoadingWheel(
                            contentDesc = stringResource(R.string.feature_merchants_loading),
                            backgroundColor = Color.White
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
                            modifier = Modifier,
                            title = stringResource(id = R.string.feature_merchants_error_oops),
                            subTitle = stringResource(id = R.string.feature_merchants_no_transactions_found),
                            iconTint = Color.Black,
                            iconImageVector = MifosIcons.Info
                        )
                    }

                    is MerchantTransferUiState.Success -> {
                        TransactionList(uiState.transactionsList)
                    }

                    is MerchantTransferUiState.InsufficientBalance -> {
                        Toast.makeText(
                            context,
                            stringResource(id = R.string.feature_merchants_insufficient_balance),
                            Toast.LENGTH_SHORT
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
                        onDismiss = { showBottomSheet = false }
                    )
                }
            }
        }
    )
}

@Composable
fun TransactionList(transactions: List<Transaction>) {
    LazyColumn {
        items(transactions) { transaction ->
            SpecificTransactionItem(transaction)
            Divider()
        }
    }
}

@Composable
fun MerchantBottomSheet(
    merchantName: String,
    merchantVPA: String,
    amount: String,
    onAmountChange: (String) -> Unit,
    checkBalanceAvailability: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    MifosBottomSheet(
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.feature_merchants_transfer_money_to_this_merchant),
                    color = ElectricViolet,
                    style = MaterialTheme.typography.subtitle1
                )
                Spacer(modifier = Modifier.height(24.dp))

                MerchantInfo(
                    merchantName,
                    merchantVPA
                )

                Spacer(modifier = Modifier.height(24.dp))
                MfOutlinedTextField(
                    value = amount,
                    onValueChange = onAmountChange,
                    label = stringResource(id = R.string.feature_merchants_amount),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        checkBalanceAvailability(
                            merchantName,
                            merchantVPA
                        )
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = submitButtonColor),
                    modifier = Modifier.width(155.dp)
                ) {
                    Text(stringResource(id = R.string.feature_merchants_submit), color = Color.White)
                }
            }
        },
        onDismiss = onDismiss
    )
}

@Composable
fun MerchantInfo(
    merchantName: String,
    merchantVPA: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        MerchantInitialAvatar(merchantName)
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = merchantName,
            style = MaterialTheme.typography.h5
        )
        Spacer(modifier = Modifier.height(9.dp))
        Text(
            text = merchantVPA,
            style = MaterialTheme.typography.h6,
            color = Color.Gray
        )
    }
}

@Composable
fun MerchantInitialAvatar(merchantName: String) {
    val initial = merchantName.take(1).uppercase()

    Box(
        modifier = Modifier
            .size(86.dp)
            .background(
                color = InitialAvatarBgColor,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            color = Color.White,
            fontSize = 44.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

class MerchantTransferUiStateProvider :
    PreviewParameterProvider<MerchantTransferUiState> {
    override val values: Sequence<MerchantTransferUiState>
        get() = sequenceOf(
            MerchantTransferUiState.Success(arrayListOf(Transaction())),
            MerchantTransferUiState.Error,
            MerchantTransferUiState.Loading,
            MerchantTransferUiState.Empty,
            MerchantTransferUiState.InsufficientBalance,
        )
}

@Preview(showSystemUi = true)
@Composable
fun Preview(@PreviewParameter(MerchantTransferUiStateProvider::class) uiState: MerchantTransferUiState) {
    MifosTheme {
        MerchantTransferScreen(
            uiState = uiState,
            onBackPressed = {},
            merchantName = "Naman Dwivedi",
            merchantVPA = "naman.dwivedi2@mifos",
            checkBalanceAvailability = { _, _ -> }
        )
    }
}

@Preview
@Composable
fun TransactionItemPreview() {
    MifosTheme {
        SpecificTransactionItem(Transaction())
    }
}

@Preview
@Composable
fun MerchantBottomSheetPreview() {
    MifosTheme {
        MerchantBottomSheet(
            merchantName = "Naman Dwivedi 2",
            merchantVPA = "naman.dwivedi2@mifos",
            amount = "",
            onAmountChange = {},
            checkBalanceAvailability = { _, _ -> },
            onDismiss = {}
        )
    }
}

@Preview
@Composable
fun MerchantInitialAvatarPreview() {
    MifosTheme {
        MerchantInitialAvatar("Naman")
    }
}