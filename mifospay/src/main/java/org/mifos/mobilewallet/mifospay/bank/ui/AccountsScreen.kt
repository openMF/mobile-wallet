package org.mifos.mobilewallet.mifospay.bank.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.mobilewallet.model.domain.BankAccountDetails
import org.mifos.mobilewallet.mifospay.R
import org.mifos.mobilewallet.mifospay.bank.presenter.AccountViewModel
import org.mifos.mobilewallet.mifospay.bank.presenter.AccountsUiState
import org.mifos.mobilewallet.mifospay.common.Constants
import org.mifos.mobilewallet.mifospay.designsystem.component.MfLoadingWheel
import org.mifos.mobilewallet.mifospay.ui.EmptyContentScreen
import org.mifos.mobilewallet.mifospay.ui.utility.AddCardChip

@Composable
fun AccountsScreen(
    viewModel: AccountViewModel = hiltViewModel(),
    onAddAccount: () -> Unit,
) {
    val accountsUiState by viewModel.accountsUiState.collectAsStateWithLifecycle()
    val sampleList = viewModel.bankAccountDetailsList
    AccountScreen(
        accountsUiState = accountsUiState,
        onAddAccount = onAddAccount,
        sampleList = sampleList,
    )
}

@Composable
fun AccountScreen(
    accountsUiState: AccountsUiState,
    onAddAccount: () -> Unit,
    sampleList: List<BankAccountDetails>
) {
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize()) {
        when (accountsUiState) {
            AccountsUiState.Empty -> {
                NoLinkedAccountsScreen { onAddAccount.invoke() }
            }

            AccountsUiState.Error -> {
                EmptyContentScreen(
                    modifier = Modifier,
                    title = stringResource(id = R.string.error_oops),
                    subTitle = stringResource(id = R.string.unexpected_error_subtitle),
                    iconTint = Color.Black,
                    iconImageVector = Icons.Rounded.Info
                )
            }

            is AccountsUiState.LinkedAccounts -> {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    item {
                        Text(
                            text = stringResource(id = R.string.linked_bank_account),
                            fontSize = 16.sp,
                            color = colorResource(id = R.color.colorTextPrimary),
                            modifier = Modifier.padding(top = 48.dp, start = 24.dp)
                        )
                    }
                    items(sampleList.size) { index ->
                        AccountsItem(
                            bankAccountDetails = sampleList[index],
                            onAccountClicked = {
                                val intent =
                                    Intent(context, BankAccountDetailActivity::class.java)
                                intent.putExtra(
                                    Constants.BANK_ACCOUNT_DETAILS,
                                    sampleList[index]
                                )
                                intent.putExtra(Constants.INDEX, index)
                                context.startActivity(intent)
                            }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(color = Color.White)
                ) {
                    AddCardChip(
                        modifier = Modifier.align(Alignment.Center),
                        onAddBtn = onAddAccount,
                        text = R.string.add_cards,
                        btnText = R.string.add_cards
                    )
                }
            }

            AccountsUiState.Loading -> {
                MfLoadingWheel(
                    contentDesc = stringResource(R.string.loading),
                    backgroundColor = Color.White
                )
            }
        }
    }
}

@Composable
fun NoLinkedAccountsScreen(onAddBtn: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(R.string.no_linked_bank_accounts))
            AddCardChip(
                modifier = Modifier,
                onAddBtn = onAddBtn,
                text = R.string.add_account,
                btnText = R.string.add_cards
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun AccountScreenLoadingPreview() {
    AccountScreen(accountsUiState = AccountsUiState.Loading, {}, emptyList())
}

@Preview(showBackground = true)
@Composable
private fun AccountEmptyScreenPreview() {
    AccountScreen(accountsUiState = AccountsUiState.Empty, {}, emptyList())
}

@Preview(showBackground = true)
@Composable
private fun AccountListScreenPreview() {
    AccountScreen(
        accountsUiState = AccountsUiState.LinkedAccounts(sampleLinkedAccount), {}, emptyList()
    )
}

@Preview(showBackground = true)
@Composable
private fun AccountErrorScreenPreview() {
    AccountScreen(accountsUiState = AccountsUiState.Error, {}, emptyList())
}

val sampleLinkedAccount = List(10) {
    BankAccountDetails(
        "SBI", "Ankur Sharma", "New Delhi",
        "XXXXXXXX9990XXX " + " ", "Savings"
    )
}