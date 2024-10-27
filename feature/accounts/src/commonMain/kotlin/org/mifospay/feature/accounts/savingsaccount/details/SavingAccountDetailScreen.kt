/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.accounts.savingsaccount.details

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import mobile_wallet.feature.accounts.generated.resources.Res
import mobile_wallet.feature.accounts.generated.resources.feature_accounts_error_oops
import mobile_wallet.feature.accounts.generated.resources.feature_accounts_loading
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.common.CurrencyFormatter
import org.mifospay.core.designsystem.component.MfLoadingWheel
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.theme.NewUi
import org.mifospay.core.model.account.Account
import org.mifospay.core.model.savingsaccount.SavingAccountDetail
import org.mifospay.core.model.savingsaccount.Status
import org.mifospay.core.model.savingsaccount.Summary
import org.mifospay.core.model.savingsaccount.formatAmount
import org.mifospay.core.model.savingsaccount.toAccount
import org.mifospay.core.ui.EmptyContentScreen
import org.mifospay.core.ui.MifosDivider
import org.mifospay.core.ui.TransactionHistoryCard
import org.mifospay.core.ui.utils.EventsEffect

@Composable
internal fun SavingAccountDetailScreen(
    navigateBack: () -> Unit,
    onViewTransaction: (Long, Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SavingAccountDetailViewModel = koinViewModel(),
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            is SADEvent.NavigateBack -> navigateBack.invoke()

            is SADEvent.ShowToast -> {
                scope.launch {
                    snackbarHostState.showSnackbar(event.message)
                }
            }

            is SADEvent.OnViewTransaction -> {
                onViewTransaction(event.clientId, event.accountId)
            }
        }
    }

    SavingAccountDetailScreen(
        state = state.viewState,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        onAction = remember(viewModel) {
            { viewModel.trySendAction(it) }
        },
    )
}

@Composable
@VisibleForTesting
internal fun SavingAccountDetailScreen(
    state: SADState.ViewState,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onAction: (SADAction) -> Unit,
) {
    MifosScaffold(
        backPress = {
            onAction(SADAction.NavigateBack)
        },
        topBarTitle = "Account Details",
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center,
        ) {
            when (state) {
                is SADState.ViewState.Loading -> {
                    MfLoadingWheel(
                        contentDesc = stringResource(Res.string.feature_accounts_loading),
                        backgroundColor = MaterialTheme.colorScheme.surface,
                    )
                }

                is SADState.ViewState.Error -> {
                    EmptyContentScreen(
                        title = stringResource(Res.string.feature_accounts_error_oops),
                        subTitle = state.message,
                        modifier = Modifier,
                        iconTint = MaterialTheme.colorScheme.onSurface,
                    )
                }

                is SADState.ViewState.Content -> {
                    SavingAccountDetailScreenContent(
                        state = state,
                        onAction = onAction,
                    )
                }
            }
        }
    }
}

@Composable
private fun SavingAccountDetailScreenContent(
    state: SADState.ViewState.Content,
    modifier: Modifier = Modifier,
    onAction: (SADAction) -> Unit,
) {
    SavingAccountDetails(
        savingAccountDetail = state.data,
        modifier = modifier,
        onViewTransaction = { clientId, accountId ->
            onAction(SADAction.ViewTransaction(clientId, accountId))
        },
    )
}

@Composable
private fun SavingAccountDetails(
    savingAccountDetail: SavingAccountDetail,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    onViewTransaction: (Long, Long) -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = lazyListState,
        contentPadding = PaddingValues(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            SavingAccountCard(
                account = savingAccountDetail.toAccount(),
                status = savingAccountDetail.status,
            )
        }

        item {
            SavingAccountSummaryCard(
                summary = savingAccountDetail.summary,
            )
        }

        item {
            TransactionHistoryCard(
                transactions = savingAccountDetail.transactions,
                onViewTransaction = onViewTransaction,
                showLeadingIcon = true,
                showViewAll = false,
            )
        }
    }
}

@Composable
private fun SavingAccountSummaryCard(
    summary: Summary,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "Account Summary",
                color = NewUi.primaryColor,
                fontWeight = FontWeight(500),
                modifier = Modifier.padding(8.dp),
            )

            MifosDivider()

            RowBlock {
                Text(text = "Account Balance")
                Text(
                    text = summary.formatAmount(summary.accountBalance),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            MifosDivider()

            RowBlock {
                Text(text = "Total Deposits")
                Text(
                    text = summary.formatAmount(summary.totalDeposits),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            MifosDivider()

            RowBlock {
                Text(text = "Total Withdrawals")
                Text(
                    text = summary.formatAmount(summary.totalWithdrawals),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            MifosDivider()

            RowBlock {
                Text(text = "Available Balance")
                Text(
                    text = summary.formatAmount(summary.availableBalance),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            MifosDivider()

            RowBlock {
                Text(text = "Total Interest Posted")
                Text(
                    text = summary.totalInterestPosted.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            MifosDivider()

            RowBlock {
                Text(text = "Total Overdraft")
                Text(
                    text = summary.totalOverdraftInterestDerived.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            MifosDivider()

            RowBlock {
                Text(text = "Interest Not Posted")
                Text(
                    text = summary.interestNotPosted.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private inline fun RowBlock(
    crossinline content: @Composable (RowScope.() -> Unit),
) {
    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            content()
        }
    }
}

@Composable
private fun SavingAccountCard(
    account: Account,
    status: Status,
    modifier: Modifier = Modifier,
) {
    val brush = remember {
        Brush.linearGradient(
            colors = listOf(NewUi.walletColor1, NewUi.walletColor2),
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                brush = brush,
                shape = RoundedCornerShape(16.dp),
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "Product Name",
                        fontWeight = FontWeight(300),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.surface,
                    )

                    Text(
                        text = account.name,
                        fontWeight = FontWeight(400),
                        color = MaterialTheme.colorScheme.surface,
                    )
                }

                SavingAccountStatusCard(status)
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = account.number,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.surface,
                    style = MaterialTheme.typography.headlineMedium,
                    letterSpacing = 0.50.sp,
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Column {
                    Text(
                        text = "Wallet Balance",
                        fontWeight = FontWeight(300),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.surface,
                    )

                    val accountBalance = CurrencyFormatter.format(
                        balance = account.balance,
                        currencyCode = account.currency.code,
                        maximumFractionDigits = null,
                    )

                    Text(
                        text = accountBalance,
                        color = MaterialTheme.colorScheme.surface,
                        style = MaterialTheme.typography.headlineLarge,
                    )
                }

                Icon(
                    modifier = Modifier
                        .graphicsLayer(rotationZ = 90f)
                        .padding(4.dp),
                    imageVector = Icons.Filled.KeyboardArrowUp,
                    contentDescription = "arrow",
                    tint = MaterialTheme.colorScheme.surface,
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SavingAccountStatusCard(
    status: Status,
    modifier: Modifier = Modifier,
) {
    val statusChips = listOf(
        "Pending Approval" to status.submittedAndPendingApproval,
        "Approved" to status.approved,
        "Rejected" to status.rejected,
        "Withdrawn" to status.withdrawnByApplicant,
        "Active" to status.active,
        "Closed" to status.closed,
        "Prematurely Closed" to status.prematureClosed,
        "Transfer in Progress" to status.transferInProgress,
        "Transfer on Hold" to status.transferOnHold,
        "Matured" to status.matured,
    )

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier,
    ) {
        statusChips.forEach { (label, isActive) ->
            if (isActive) {
                StatusChip(label)
            }
        }
    }
}

@Composable
private fun StatusChip(label: String) {
    val color = when (label) {
        "Pending Approval" -> Color(0xFFFFF9C4)
        "Approved" -> Color(0xFFC8E6C9)
        "Rejected" -> Color(0xFFFFCDD2)
        "Withdrawn" -> Color(0xFFE1BEE7)
        "Active" -> Color(0xFFBBDEFB)
        "Closed" -> Color(0xFFCFD8DC)
        "Prematurely Closed" -> Color(0xFFD7CCC8)
        "Transfer in Progress" -> Color(0xFFFFE0B2)
        "Transfer on Hold" -> Color(0xFFF0F4C3)
        "Matured" -> Color(0xFFB2DFDB)
        else -> Color(0xFFEFEFEF)
    }

    SuggestionChip(
        onClick = { /* Handle click if needed */ },
        label = { Text(label) },
        border = SuggestionChipDefaults.suggestionChipBorder(
            enabled = true,
            borderColor = color,
        ),
        colors = SuggestionChipDefaults.suggestionChipColors(
            labelColor = color,
        ),
        modifier = Modifier,
    )
}
