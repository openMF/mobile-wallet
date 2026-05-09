/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.pocket.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mobile_wallet.feature.pocket.generated.resources.Res
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_cancel
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_confirm
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_confirm_delink
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_delink_account
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_error_loading
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_linked_accounts
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_no_accounts
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_no_accounts_subtitle
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_retry
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_title
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_total_balance
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.model.pocket.PocketAccount

@Composable
fun PocketDashboardScreen(
    navigateBack: () -> Unit,
    navigateToLinkAccount: () -> Unit,
    navigateToAccountDetails: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PocketDashboardViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    org.mifospay.core.ui.utils.EventsEffect(viewModel) { event ->
        when (event) {
            PocketDashboardEvent.NavigateBack -> navigateBack()
            PocketDashboardEvent.NavigateToLinkAccount -> navigateToLinkAccount()
            is PocketDashboardEvent.NavigateToAccountDetails -> navigateToAccountDetails(event.accountId)
        }
    }

    LaunchedEffect(state.snackbarMessage) {
        state.snackbarMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    PocketDashboardScreenContent(
        state = state,
        onNavigateBack = { viewModel.trySendAction(PocketDashboardAction.NavigateBack) },
        onLinkAccount = { viewModel.trySendAction(PocketDashboardAction.NavigateToLinkAccount) },
        onDelinkAccount = { viewModel.trySendAction(PocketDashboardAction.DelinkAccount(it)) },
        onViewDetails = { viewModel.trySendAction(PocketDashboardAction.NavigateToAccountDetails(it)) },
        onRetry = { viewModel.trySendAction(PocketDashboardAction.Retry) },
        modifier = modifier,
    )
}

@Composable
private fun PocketDashboardScreenContent(
    state: PocketDashboardState,
    onNavigateBack: () -> Unit,
    onLinkAccount: () -> Unit,
    onDelinkAccount: (Long) -> Unit,
    onViewDetails: (Long) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosScaffold(
        backPress = onNavigateBack,
        topBarTitle = stringResource(Res.string.feature_pocket_title),
        modifier = modifier,
        floatingActionButtonContent = org.mifospay.core.designsystem.component.FloatingActionButtonContent(
            onClick = onLinkAccount,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            when (val content = state.contentState) {
                PocketContentState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                PocketContentState.Empty -> {
                    EmptyPocketState(modifier = Modifier.align(Alignment.Center))
                }

                is PocketContentState.Error -> {
                    ErrorPocketState(
                        message = content.message,
                        onRetry = onRetry,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                PocketContentState.Success -> {
                    state.pocket?.let { pocket ->
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            item {
                                TotalBalanceCard(
                                    totalBalance = pocket.totalBalance,
                                    currency = pocket.linkedAccounts.firstOrNull()?.currency ?: "",
                                )
                            }
                            item {
                                Text(
                                    text = stringResource(Res.string.feature_pocket_linked_accounts),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                            items(pocket.linkedAccounts, key = { it.accountId }) { account ->
                                LinkedAccountCard(
                                    account = account,
                                    onDelink = { onDelinkAccount(account.accountId) },
                                    onViewDetails = { onViewDetails(account.accountId) },
                                )
                            }
                            item { Spacer(Modifier.height(72.dp)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TotalBalanceCard(
    totalBalance: Double,
    currency: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(Res.string.feature_pocket_total_balance),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "$currency ${"%.2f".format(totalBalance)}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
private fun LinkedAccountCard(
    account: PocketAccount,
    onDelink: () -> Unit,
    onViewDetails: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showConfirmDialog by remember { mutableStateOf(false) }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text(stringResource(Res.string.feature_pocket_delink_account)) },
            text = { Text(stringResource(Res.string.feature_pocket_confirm_delink)) },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDialog = false
                    onDelink()
                }) {
                    Text(stringResource(Res.string.feature_pocket_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text(stringResource(Res.string.feature_pocket_cancel))
                }
            },
        )
    }

    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = account.productName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = account.accountNumber,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "${account.currency} ${"%.2f".format(account.balance)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Row {
                IconButton(onClick = onViewDetails) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                IconButton(onClick = { showConfirmDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyPocketState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = Icons.Default.AccountBalanceWallet,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Text(
            text = stringResource(Res.string.feature_pocket_no_accounts),
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = stringResource(Res.string.feature_pocket_no_accounts_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ErrorPocketState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(Res.string.feature_pocket_error_loading),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
        )
        Text(text = message, style = MaterialTheme.typography.bodySmall)
        TextButton(onClick = onRetry) {
            Text(stringResource(Res.string.feature_pocket_retry))
        }
    }
}
