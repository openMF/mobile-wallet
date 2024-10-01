/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.bank.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.library.pullrefresh.PullRefreshIndicator
import com.mifos.library.pullrefresh.pullRefresh
import com.mifos.library.pullrefresh.rememberPullRefreshState
import com.mifospay.core.model.domain.BankAccountDetails
import org.koin.androidx.compose.koinViewModel
import org.mifospay.core.designsystem.component.MfLoadingWheel
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.core.ui.utility.AddCardChip

@Composable
fun AccountsScreen(
    navigateToBankAccountDetailScreen: (BankAccountDetails, Int) -> Unit,
    navigateToLinkBankAccountScreen: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AccountViewModel = koinViewModel(),
) {
    val accountsUiState by viewModel.accountsUiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val bankAccountDetailsList by viewModel.bankAccountDetailsList.collectAsStateWithLifecycle()

    AccountScreen(
        modifier = modifier,
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
            navigateToBankAccountDetailScreen.invoke(bankAccountDetails, index)
        },
    )
}

@Composable
private fun AccountScreen(
    accountsUiState: AccountsUiState,
    onAddAccount: () -> Unit,
    bankAccountDetailsList: List<BankAccountDetails>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onUpdateAccount: (BankAccountDetails, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pullRefreshState = rememberPullRefreshState(isRefreshing, onRefresh)
    Box(modifier.pullRefresh(pullRefreshState)) {
        Column(modifier = Modifier.fillMaxSize()) {
            when (accountsUiState) {
                AccountsUiState.Empty -> {
                    NoLinkedAccountsScreen(
                        onAddBtn = onAddAccount,
                    )
                }

                AccountsUiState.Error -> {
                    EmptyContentScreen(
                        title = stringResource(id = R.string.feature_accounts_error_oops),
                        subTitle = stringResource(id = R.string.feature_accounts_unexpected_error_subtitle),
                        modifier = Modifier,
                        iconTint = MaterialTheme.colorScheme.onSurface,
                        iconImageVector = MifosIcons.Info,
                    )
                }

                is AccountsUiState.LinkedAccounts -> {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                    ) {
                        item {
                            Text(
                                text = stringResource(id = R.string.feature_accounts_linked_bank_account),
                                fontSize = 19.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(
                                    top = 45.dp,
                                    start = 25.dp,
                                    bottom = 15.dp,
                                ),
                            )
                        }
                        items(bankAccountDetailsList) { bankAccountDetails ->
                            val index = bankAccountDetailsList.indexOf(bankAccountDetails)
                            AccountsItem(
                                bankAccountDetails = bankAccountDetails,
                                onAccountClicked = {
                                    onUpdateAccount(bankAccountDetails, index)
                                },
                            )
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                                modifier = Modifier.padding(8.dp),
                            )
                        }
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .background(Color.Transparent),
                            ) {
                                AddCardChip(
                                    text = R.string.feature_accounts_add_account,
                                    btnText = R.string.feature_accounts_add_cards,
                                    onAddBtn = onAddAccount,
                                    modifier = Modifier.align(Alignment.Center),
                                )
                            }
                        }
                    }
                }

                AccountsUiState.Loading -> {
                    MfLoadingWheel(
                        contentDesc = stringResource(R.string.feature_accounts_loading),
                        backgroundColor = MaterialTheme.colorScheme.surface,
                    )
                }
            }
        }
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }
}

@Composable
private fun NoLinkedAccountsScreen(
    onAddBtn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = stringResource(R.string.feature_accounts_no_linked_bank_accounts))
            AddCardChip(
                text = R.string.feature_accounts_add_account,
                btnText = R.string.feature_accounts_add_cards,
                onAddBtn = onAddBtn,
                modifier = Modifier,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AccountScreenLoadingPreview() {
    AccountScreen(
        accountsUiState = AccountsUiState.Loading,
        {},
        emptyList(),
        false,
        {},
        { _, _ -> },
    )
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
        { _, _ -> },
    )
}

@Preview(showBackground = true)
@Composable
private fun AccountErrorScreenPreview() {
    AccountScreen(accountsUiState = AccountsUiState.Error, {}, emptyList(), false, {}, { _, _ -> })
}

val sampleLinkedAccount = List(10) {
    BankAccountDetails(
        "SBI",
        "Ankur Sharma",
        "New Delhi",
        "XXXXXXXX9990XXX " + " ",
        "Savings",
    )
}
