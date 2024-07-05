package org.mifospay.feature.bank.accounts

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
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
import com.mifospay.core.model.domain.BankAccountDetails
import org.mifospay.common.Constants
import org.mifospay.core.designsystem.component.MfLoadingWheel
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.core.ui.utility.AddCardChip
import org.mifospay.feature.bank.accounts.details.BankAccountDetailActivity
import org.mifospay.feature.bank.accounts.link.LinkBankAccountActivity

@Composable
fun AccountsScreen(
    viewModel: AccountViewModel = hiltViewModel()
) {
    val accountsUiState by viewModel.accountsUiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val bankAccountDetailsList by viewModel.bankAccountDetailsList.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val updateBankAccountLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val bundle = result.data?.extras
            if (bundle != null) {
                val bankAccountDetails = bundle.getParcelable<BankAccountDetails>(Constants.UPDATED_BANK_ACCOUNT)
                val index = bundle.getInt(Constants.INDEX)
                if (bankAccountDetails != null) {
                    viewModel.updateBankAccount(index, bankAccountDetails)
                }
            }
        }
    }

    AccountScreen(
        accountsUiState = accountsUiState,
        onAddAccount = {
            val intent = Intent(context, LinkBankAccountActivity::class.java)
            context.startActivity(intent)
        },
        bankAccountDetailsList = bankAccountDetailsList,
        isRefreshing = isRefreshing,
        onRefresh = {
            viewModel.refresh()
        },
        onUpdateAccount = { bankAccountDetails, index ->
            val intent = Intent(context, BankAccountDetailActivity::class.java).apply {
                putExtra(Constants.BANK_ACCOUNT_DETAILS, bankAccountDetails)
                putExtra(Constants.INDEX, index)
            }
            updateBankAccountLauncher.launch(intent)
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AccountScreen(
    accountsUiState: AccountsUiState,
    onAddAccount: () -> Unit,
    bankAccountDetailsList: List<BankAccountDetails>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onUpdateAccount: (BankAccountDetails, Int) -> Unit
) {
    val pullRefreshState = rememberPullRefreshState(isRefreshing, onRefresh)
    Box(Modifier.pullRefresh(pullRefreshState)) {
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
                        items(bankAccountDetailsList) { bankAccountDetails ->
                            val index = bankAccountDetailsList.indexOf(bankAccountDetails)
                            AccountsItem(
                                bankAccountDetails = bankAccountDetails,
                                onAccountClicked = {
                                    onUpdateAccount(bankAccountDetails, index)
                                }
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .background(color = Color.White)
                            ) {
                                AddCardChip(
                                    modifier = Modifier.align(Alignment.Center),
                                    onAddBtn = onAddAccount,
                                    text = R.string.add_account,
                                    btnText = R.string.add_cards
                                )
                            }
                        }
                    }
                }

                AccountsUiState.Loading -> {
                    MfLoadingWheel(
                        contentDesc = stringResource(R.string.loading),
                        backgroundColor = Color.White
                    )
                }

                else -> {}
            }
        }
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
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
    AccountScreen(accountsUiState = AccountsUiState.Loading, {}, emptyList(), false, {}, { _, _ -> })
}

@Preview(showBackground = true)
@Composable
private fun AccountEmptyScreenPreview() {
    AccountScreen(accountsUiState = AccountsUiState.Empty, {}, emptyList(), false, {}, { _, _ -> })
}

@Preview(showBackground = true)
@Composable
private fun AccountListScreenPreview() {
    AccountScreen(
        accountsUiState = AccountsUiState.LinkedAccounts(sampleLinkedAccount),
        {},
        sampleLinkedAccount,
        false,
        {},
        { _, _ -> }
    )
}

@Preview(showBackground = true)
@Composable
private fun AccountErrorScreenPreview() {
    AccountScreen(accountsUiState = AccountsUiState.Error, {}, emptyList(), false, {}, { _, _ -> })
}

val sampleLinkedAccount = List(10) {
    BankAccountDetails(
        "SBI", "Ankur Sharma", "New Delhi",
        "XXXXXXXX9990XXX " + " ", "Savings"
    )
}
