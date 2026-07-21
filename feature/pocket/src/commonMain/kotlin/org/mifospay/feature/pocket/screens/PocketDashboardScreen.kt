/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.pocket.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mobile_wallet.feature.pocket.generated.resources.Res
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_dashboard_loan_accounts
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_dashboard_manage
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_dashboard_savings_accounts
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_dashboard_share_accounts
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_dashboard_title
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_dashboard_total_balance
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_empty_action
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_empty_description
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_empty_title
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.MifosAccountCard
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.model.pocket.AccountStatus
import org.mifospay.core.ui.ErrorScreenContent
import org.mifospay.core.ui.MifosProgressIndicator
import org.mifospay.core.ui.utils.EventsEffect
import org.mifospay.feature.pocket.viewmodels.DetailedPocket
import org.mifospay.feature.pocket.viewmodels.PocketDashboardAction
import org.mifospay.feature.pocket.viewmodels.PocketDashboardEvent
import org.mifospay.feature.pocket.viewmodels.PocketDashboardState
import org.mifospay.feature.pocket.viewmodels.PocketDashboardUiState
import org.mifospay.feature.pocket.viewmodels.PocketDashboardViewModel
import template.core.base.designsystem.theme.KptTheme

@Composable
internal fun PocketDashboardScreen(
    navigateBack: () -> Unit,
    navigateToManagePocket: () -> Unit,
    navigateToLoanAccountDetail: (Long) -> Unit,
    navigateToShareAccountDetail: (Long) -> Unit,
    navigateToSavingsAccountDetail: (Long) -> Unit,
    viewModel: PocketDashboardViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            PocketDashboardEvent.NavigateBack -> navigateBack.invoke()
            PocketDashboardEvent.ManagePocket -> navigateToManagePocket.invoke()
            is PocketDashboardEvent.NavigateToLoanDetail -> navigateToLoanAccountDetail(event.accountId)
            is PocketDashboardEvent.NavigateToSavingsDetail -> navigateToSavingsAccountDetail(event.accountId)
            is PocketDashboardEvent.NavigateToShareDetail -> navigateToShareAccountDetail(event.accountId)
        }
    }

    PocketDashboardContent(
        state = state,
        onAction = remember(viewModel) {
            { viewModel.trySendAction(it) }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PocketDashboardContent(
    state: PocketDashboardState,
    onAction: (PocketDashboardAction) -> Unit,
) {
    val pullRefreshState = rememberPullToRefreshState()

    MifosScaffold(
        backPress = { onAction(PocketDashboardAction.NavigateBack) },
        topBarTitle = stringResource(Res.string.feature_pocket_dashboard_title),
        containerColor = KptTheme.colorScheme.background,
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { onAction(PocketDashboardAction.Refresh) },
            state = pullRefreshState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter,
        ) {
            when (state.uiState) {
                PocketDashboardUiState.Loading -> {
                    MifosProgressIndicator()
                }
                is PocketDashboardUiState.Error -> {
                    ErrorScreenContent(
                        subTitle = stringResource(state.uiState.message),
                        onClickRetry = { onAction(PocketDashboardAction.Retry) },
                    )
                }
                PocketDashboardUiState.Empty -> {
                    EmptyPocketContent(
                        onLinkFirstAccount = { onAction(PocketDashboardAction.LinkFirstAccount) },
                    )
                }
                PocketDashboardUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                    ) {
                        PocketDashboardCard(
                            totalBalance = state.totalBalance,
                            onManageClick = { onAction(PocketDashboardAction.ManagePocket) },
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        if (state.savingsAccounts.isNotEmpty()) {
                            PocketSectionHeader(title = Res.string.feature_pocket_dashboard_savings_accounts)
                            Spacer(modifier = Modifier.height(16.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                state.savingsAccounts.forEach { account ->
                                    MifosAccountCard(
                                        accountId = account.accountId,
                                        accountType = account.name,
                                        accountNumber = account.accountNumber,
                                        accountStatus = account.balanceOrStatus,
                                        accountStatusColor = account.status.toColor(),
                                        onAccountClick = {
                                            onAction(PocketDashboardAction.NavigateToSavingsDetail(account.accountId))
                                        },
                                        icon = MifosIcons.PersonAccounts,
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(32.dp))
                        }

                        if (state.loanAccounts.isNotEmpty()) {
                            PocketSectionHeader(title = Res.string.feature_pocket_dashboard_loan_accounts)
                            Spacer(modifier = Modifier.height(16.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                state.loanAccounts.forEach { account ->
                                    MifosAccountCard(
                                        accountId = account.accountId,
                                        accountType = account.name,
                                        accountNumber = account.accountNumber,
                                        accountStatus = account.balanceOrStatus,
                                        accountStatusColor = account.status.toColor(),
                                        onAccountClick = {
                                            onAction(PocketDashboardAction.NavigateToLoanDetail(account.accountId))
                                        },
                                        icon = MifosIcons.CoinMultiple,
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(32.dp))
                        }

                        if (state.shareAccounts.isNotEmpty()) {
                            PocketSectionHeader(title = Res.string.feature_pocket_dashboard_share_accounts)
                            Spacer(modifier = Modifier.height(16.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                state.shareAccounts.forEach { account ->
                                    MifosAccountCard(
                                        accountId = account.accountId,
                                        accountType = account.name,
                                        accountNumber = account.accountNumber,
                                        accountStatus = account.balanceOrStatus,
                                        accountStatusColor = account.status.toColor(),
                                        onAccountClick = {
                                            onAction(PocketDashboardAction.NavigateToShareDetail(account.accountId))
                                        },
                                        icon = MifosIcons.CoinMultiple,
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun PocketSectionHeader(
    title: StringResource,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(title),
        modifier = modifier.fillMaxWidth(),
        style = KptTheme.typography.titleMedium,
        color = KptTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
internal fun PocketDashboardCard(
    totalBalance: String,
    onManageClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(KptTheme.shapes.large)
            .height(112.dp)
            .fillMaxWidth()
            .background(KptTheme.colorScheme.primary),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(Res.string.feature_pocket_dashboard_total_balance),
                    style = KptTheme.typography.bodySmall,
                    color = KptTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                )

                Text(
                    text = totalBalance,
                    style = KptTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = KptTheme.colorScheme.onPrimary,
                )
            }

            MifosButton(
                onClick = onManageClick,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = KptTheme.colorScheme.onPrimary,
                    contentColor = KptTheme.colorScheme.primary,
                ),
                content = {
                    Text(
                        text = stringResource(Res.string.feature_pocket_dashboard_manage),
                        style = KptTheme.typography.titleSmall,
                    )
                },
            )
        }
    }
}

@Composable
fun AccountStatus.toColor(): Color =
    when (this) {
        AccountStatus.ACTIVE ->
            KptTheme.colorScheme.primary

        AccountStatus.PENDING,
        AccountStatus.APPROVED,
        ->
            KptTheme.colorScheme.tertiary

        AccountStatus.REJECTED,
        AccountStatus.WITHDRAWN,
        AccountStatus.CLOSED,
        ->
            KptTheme.colorScheme.error

        AccountStatus.OVERPAID,
        AccountStatus.MATURED,
        ->
            KptTheme.colorScheme.secondary

        AccountStatus.UNKNOWN ->
            KptTheme.colorScheme.onSurface
    }

@Composable
internal fun EmptyPocketContent(
    onLinkFirstAccount: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Surface(
            modifier = Modifier.size(180.dp),
            shape = CircleShape,
            color = KptTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = MifosIcons.Pocket,
                    contentDescription = null,
                    tint = KptTheme.colorScheme.primary,
                    modifier = Modifier.size(80.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(Res.string.feature_pocket_empty_title),
            style = KptTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = KptTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(Res.string.feature_pocket_empty_description),
            style = KptTheme.typography.bodyLarge,
            color = KptTheme.colorScheme.secondary,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(32.dp))

        MifosButton(
            onClick = onLinkFirstAccount,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = KptTheme.colorScheme.primary,
                contentColor = KptTheme.colorScheme.onPrimary,
            ),
            content = {
                Text(
                    text = stringResource(Res.string.feature_pocket_empty_action),
                    style = KptTheme.typography.labelLarge,
                )
            },
        )
    }
}

@Preview
@Composable
internal fun PocketDashboardContentPreview() {
    PocketDashboardContent(
        state = PocketDashboardState(
            totalBalance = "$ 18,750.00",
            savingsAccounts = listOf(
                DetailedPocket(
                    accountId = 1L,
                    name = "Emergency Fund",
                    accountNumber = "1004859238",
                    balanceOrStatus = "$ 5,000.00",
                    status = AccountStatus.ACTIVE,
                ),
                DetailedPocket(
                    accountId = 2L,
                    name = "Vacation Savings",
                    accountNumber = "1004859299",
                    balanceOrStatus = "$ 1,250.00",
                    status = AccountStatus.ACTIVE,
                ),
            ),
            loanAccounts = listOf(
                DetailedPocket(
                    accountId = 3L,
                    name = "Personal Loan",
                    accountNumber = "3009284756",
                    balanceOrStatus = "$ 10,000.00",
                    status = AccountStatus.ACTIVE,
                ),
                DetailedPocket(
                    accountId = 4L,
                    name = "Auto Loan",
                    accountNumber = "3009284812",
                    balanceOrStatus = "PENDING",
                    status = AccountStatus.PENDING,
                ),
            ),
            shareAccounts = listOf(
                DetailedPocket(
                    accountId = 5L,
                    name = "Company Shares",
                    accountNumber = "5001129384",
                    balanceOrStatus = "$ 2,500.00",
                    status = AccountStatus.ACTIVE,
                ),
            ),
            uiState = PocketDashboardUiState.Success,
        ),
        onAction = {},
    )
}

@Preview
@Composable
private fun EmptyPocketContentPreview() {
    EmptyPocketContent(
        onLinkFirstAccount = {},
    )
}
