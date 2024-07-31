package org.mifospay.feature.bank.accounts

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifospay.core.model.domain.BankAccountDetails
import org.mifospay.core.designsystem.component.MfLoadingWheel
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.core.ui.utility.AddCardChip

@Composable
fun AccountsScreen(
    viewModel: AccountViewModel = hiltViewModel(),
    navigateToBankAccountDetailScreen: (BankAccountDetails,Int) -> Unit,
    navigateToLinkBankAccountScreen: () -> Unit
) {
    val accountsUiState by viewModel.accountsUiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val bankAccountDetailsList by viewModel.bankAccountDetailsList.collectAsStateWithLifecycle()

    AccountScreen(
        accountsUiState = accountsUiState,
        onAddAccount = {
            navigateToLinkBankAccountScreen.invoke()
        },
        bankAccountDetailsList = bankAccountDetailsList,
        isRefreshing = isRefreshing,
        onRefresh = {
            viewModel.refresh()
        },
        onUpdateAccount = { bankAccountDetails, index ->
            viewModel.updateBankAccount(index, bankAccountDetails)
            navigateToBankAccountDetailScreen.invoke(bankAccountDetails,index)
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
                        title = stringResource(id = R.string.feature_accounts_error_oops),
                        subTitle = stringResource(id = R.string.feature_accounts_unexpected_error_subtitle),
                        iconTint = MaterialTheme.colorScheme.onSurface,
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
                                text = stringResource(id = R.string.feature_accounts_linked_bank_account),
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.primary,
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
                                    .background(MaterialTheme.colorScheme.surface)
                            ) {
                                AddCardChip(
                                    modifier = Modifier.align(Alignment.Center),
                                    onAddBtn = onAddAccount,
                                    text = R.string.feature_accounts_add_account,
                                    btnText = R.string.feature_accounts_add_cards
                                )
                            }
                        }
                    }
                }

                AccountsUiState.Loading -> {
                    MfLoadingWheel(
                        contentDesc = stringResource(R.string.feature_accounts_loading),
                        backgroundColor = MaterialTheme.colorScheme.surface
                    )
                }
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
            Text(text = stringResource(R.string.feature_accounts_no_linked_bank_accounts))
            AddCardChip(
                modifier = Modifier,
                onAddBtn = onAddBtn,
                text = R.string.feature_accounts_add_account,
                btnText = R.string.feature_accounts_add_cards
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
