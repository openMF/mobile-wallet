package org.mifos.mobilewallet.mifospay.bank.composeScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mifos.mobilewallet.model.domain.BankAccountDetails
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.bank.viewmodel.BankAccountsViewModel
import org.mifos.mobilewallet.mifospay.designsystem.theme.MifosTheme

@Composable
fun AccountScreen(
    viewModel: BankAccountsViewModel = hiltViewModel(),
    onAddAccountClicked: () -> Unit,
    onAccountClicked: (BankAccountDetails) -> Unit
) {
    val bankAccountDetails = viewModel.bankAccountDetailsList
    AccountScreenContent(bankAccountDetails, onAddAccountClicked, onAccountClicked)
}

@Composable
fun AccountScreenContent(
    bankAccountDetails: List<BankAccountDetails>,
    onAddAccountClicked: () -> Unit,
    onAccountClicked: (BankAccountDetails) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = stringResource(id = R.string.linked_bank_account),
            fontSize = 16.sp,
            color = colorResource(id = R.color.colorTextPrimary),
            modifier = Modifier.padding(
                top = 48.dp,
                start = 24.dp
            )
        )
        if (bankAccountDetails.isEmpty()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(top = 48.dp, bottom = 70.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                EmptyStatePlaceHolder(
                    title = stringResource(id = R.string.empty_no_accounts_title),
                    subtitle = stringResource(id = R.string.empty_no_accounts_subtitle),
                    drawableResId = R.drawable.ic_empty_state
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 48.dp)
                    .fillMaxSize(),
                state = rememberLazyListState()
            ) {
                items(bankAccountDetails.size) { index ->
                    val bankAccount = bankAccountDetails[index]
                    ListItemWithImage(
                        bankAccount = bankAccount,
                        onAccountClicked
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        AddAccountChip(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 128.dp),
            onAddAccountClicked = onAddAccountClicked
        )
    }
}

@Preview
@Composable
fun AccountScreenPreview() {
    var bankAccountDetails = listOf(
        BankAccountDetails(
            "SBI", "Ankur Sharma", "New Delhi",
            "1234567890", "Savings"
        ),
        BankAccountDetails(
            "HDFC", "Mandeep Singh ", "Uttar Pradesh",
            "1234567890", "Savings"
        ),
        BankAccountDetails(
            "ANDHRA", "Rakesh anna ", "Telegana",
            "1234567890", "Savings"
        ),
        BankAccountDetails(
            "PNB", "luv Pro ", "Gujrat",
            "1234567890", "Savings"
        ),
        BankAccountDetails(
            "HDF", "Harry potter ", "Hogwarts",
            "1234567890", "Savings"
        ),
        BankAccountDetails(
            "GCI", "JIGME ", "JAMMU",
            "1234567890", "Savings"
        ),
        BankAccountDetails(
            "FCI", "NISHU BOII ", "ASSAM",
            "1234567890", "Savings"
        ),
        BankAccountDetails(
            "SBI", "Ankur Sharma", "New Delhi",
            "1234567890", "Savings"
        ),
        BankAccountDetails(
            "HDFC", "Mandeep Singh ", "Uttar Pradesh",
            "1234567890", "Savings"
        ),
        BankAccountDetails(
            "ANDHRA", "Rakesh anna ", "Telegana",
            "1234567890", "Savings"
        ),
        BankAccountDetails(
            "PNB", "luv Pro ", "Gujrat",
            "1234567890", "Savings"
        )
    )
    MifosTheme {
        AccountScreenContent(bankAccountDetails, {}, {})
    }
}

@Preview
@Composable
fun EmptyAccountScreenPreview() {
    MifosTheme {
        AccountScreenContent(bankAccountDetails = emptyList(), {}, {})
    }
}