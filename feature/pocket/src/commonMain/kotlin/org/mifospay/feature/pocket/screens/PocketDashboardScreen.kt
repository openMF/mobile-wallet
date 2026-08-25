/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import kpt.core.base.designsystem.theme.KptTheme
import kpt.core.base.store.freshness.FreshnessSignal
import kpt.core.base.store.screen.ScreenState
import kpt.core.base.ui.freshness.FreshnessIndicator
import kpt.core.base.ui.screen.ScreenContent
import mifos_pay.feature.pocket.generated.resources.Res
import mifos_pay.feature.pocket.generated.resources.feature_pocket_dashboard_loan_accounts
import mifos_pay.feature.pocket.generated.resources.feature_pocket_dashboard_manage
import mifos_pay.feature.pocket.generated.resources.feature_pocket_dashboard_savings_accounts
import mifos_pay.feature.pocket.generated.resources.feature_pocket_dashboard_share_accounts
import mifos_pay.feature.pocket.generated.resources.feature_pocket_dashboard_title
import mifos_pay.feature.pocket.generated.resources.feature_pocket_dashboard_total_balance
import mifos_pay.feature.pocket.generated.resources.feature_pocket_empty_action
import mifos_pay.feature.pocket.generated.resources.feature_pocket_empty_description
import mifos_pay.feature.pocket.generated.resources.feature_pocket_empty_title
import mifos_pay.feature.pocket.generated.resources.feature_pocket_unknown_account
import mifos_pay.feature.pocket.generated.resources.feature_pocket_unknown_status
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.common.CurrencyFormatter
import org.mifospay.core.designsystem.component.MifosAccountCard
import org.mifospay.core.designsystem.component.MifosButton
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.designsystem.icon.MifosIcons
import org.mifospay.core.model.enums.AccountType
import org.mifospay.core.model.pocket.AccountStatus
import org.mifospay.core.model.pocket.DetailedPocketAccount
import org.mifospay.core.model.pocket.PocketAccount
import org.mifospay.core.ui.utils.EventsEffect
import org.mifospay.feature.pocket.viewmodels.DetailedPocket
import org.mifospay.feature.pocket.viewmodels.PocketDashboardAction
import org.mifospay.feature.pocket.viewmodels.PocketDashboardEvent
import org.mifospay.feature.pocket.viewmodels.PocketDashboardViewModel

@Composable
internal fun PocketDashboardScreen(
    navigateBack: () -> Unit,
    navigateToManagePocket: () -> Unit,
    navigateToLoanAccountDetail: (Long) -> Unit,
    navigateToShareAccountDetail: (Long) -> Unit,
    navigateToSavingsAccountDetail: (Long) -> Unit,
    viewModel: PocketDashboardViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val freshness by viewModel.freshness.collectAsStateWithLifecycle()

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
        freshness = freshness,
        onAction = remember(viewModel) {
            { viewModel.trySendAction(it) }
        },
        onRetry = viewModel::retry,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PocketDashboardContent(
    state: ScreenState<List<DetailedPocketAccount>>,
    freshness: FreshnessSignal,
    onAction: (PocketDashboardAction) -> Unit,
    onRetry: () -> Unit,
) {
    val pullRefreshState = rememberPullToRefreshState()
    // Pull-to-refresh spinner is driven by the stream's own freshness signal —
    // true while a background/manual revalidation is in flight over existing
    // content. Loading / Empty / Error are handled inside ScreenContent.
    val isRefreshing = (state as? ScreenState.Content)?.freshnessSignal?.isRefreshing == true

    MifosScaffold(
        backPress = { onAction(PocketDashboardAction.NavigateBack) },
        topBarTitle = stringResource(Res.string.feature_pocket_dashboard_title),
        containerColor = KptTheme.colorScheme.background,
        actions = {
            FreshnessIndicator(
                signal = freshness,
                onRefresh = { onAction(PocketDashboardAction.Refresh) },
            )
            IconButton(onClick = { onAction(PocketDashboardAction.ManagePocket) }) {
                Icon(
                    imageVector = MifosIcons.Edit2,
                    contentDescription = "Manage Pockets",
                    tint = KptTheme.colorScheme.onSurface,
                )
            }
        },
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { onAction(PocketDashboardAction.Refresh) },
            state = pullRefreshState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter,
        ) {
            // Template idiom: `ScreenContent` (core-base/ui) owns every render
            // branch — loading / empty / no-network / unauthenticated /
            // error+retry — driven by the stream's pre-decided `ScreenState`.
            // Only the Content body is authored here; the empty state keeps the
            // feature's existing "link your first account" call-to-action. The
            // in-flight banner is suppressed (`refreshingIndicator = null`)
            // because the PullToRefreshBox spinner already signals refresh.
            ScreenContent(
                state = state,
                onRetry = onRetry,
                modifier = Modifier.fillMaxSize(),
                refreshingIndicator = null,
                empty = {
                    EmptyPocketContent(
                        onLinkFirstAccount = { onAction(PocketDashboardAction.LinkFirstAccount) },
                    )
                },
            ) { pockets, _ ->
                // Fallbacks resolved here (Composable scope) so the pure bucket
                // fold below stays free of `stringResource` / suspend `getString`.
                val unknownStatus = stringResource(Res.string.feature_pocket_unknown_status)
                val unknownAccount = stringResource(Res.string.feature_pocket_unknown_account)
                val buckets = remember(pockets, unknownStatus, unknownAccount) {
                    pockets.toPocketBuckets(
                        unknownStatus = unknownStatus,
                        unknownAccount = unknownAccount,
                    )
                }
                PocketDashboardSuccessContent(buckets = buckets, onAction = onAction)
            }
        }
    }
}

@Composable
private fun PocketDashboardSuccessContent(
    buckets: PocketBuckets,
    onAction: (PocketDashboardAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(KptTheme.spacing.lg),
    ) {
        PocketDashboardCard(
            totalBalance = buckets.totalBalance,
            onManageClick = { onAction(PocketDashboardAction.ManagePocket) },
        )

        Spacer(modifier = Modifier.height(KptTheme.spacing.xl))

        if (buckets.savingsAccounts.isNotEmpty()) {
            PocketSectionHeader(title = Res.string.feature_pocket_dashboard_savings_accounts)
            Spacer(modifier = Modifier.height(KptTheme.spacing.md))
            Column(verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md)) {
                buckets.savingsAccounts.forEach { account ->
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
            Spacer(modifier = Modifier.height(KptTheme.spacing.xl))
        }

        if (buckets.loanAccounts.isNotEmpty()) {
            PocketSectionHeader(title = Res.string.feature_pocket_dashboard_loan_accounts)
            Spacer(modifier = Modifier.height(KptTheme.spacing.md))
            Column(verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md)) {
                buckets.loanAccounts.forEach { account ->
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
            Spacer(modifier = Modifier.height(KptTheme.spacing.xl))
        }

        if (buckets.shareAccounts.isNotEmpty()) {
            PocketSectionHeader(title = Res.string.feature_pocket_dashboard_share_accounts)
            Spacer(modifier = Modifier.height(KptTheme.spacing.md))
            Column(verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md)) {
                buckets.shareAccounts.forEach { account ->
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
            Spacer(modifier = Modifier.height(KptTheme.spacing.xl))
        }
    }
}

/**
 * Screen-side presentation fold of the raw [DetailedPocketAccount] page into
 * per-account-type sections + a formatted multi-currency total. This is the
 * pure (non-suspend, non-Composable) extraction of the pre-migration
 * `PocketDashboardViewModel.populateFromContent` — moved here because it now
 * runs on the stream's `ScreenState.Content` payload directly, and the two
 * missing-value fallbacks resolve from `stringResource` at the call site.
 */
internal data class PocketBuckets(
    val totalBalance: String,
    val savingsAccounts: List<DetailedPocket>,
    val loanAccounts: List<DetailedPocket>,
    val shareAccounts: List<DetailedPocket>,
)

private fun List<DetailedPocketAccount>.toPocketBuckets(
    unknownStatus: String,
    unknownAccount: String,
): PocketBuckets {
    // Upstream PR #2057 (manage-pocket) rewired per-account balance rendering to
    // prefix the currency `displaySymbol` in front of the numeric string
    // (`"$code $displaySymbol$formattedNum"`); kept identical here.
    fun mapToUiModel(detailed: DetailedPocketAccount): DetailedPocket {
        val balanceStr = if (detailed.status == AccountStatus.ACTIVE) {
            if (detailed.balance != null) {
                val code = detailed.currencyCode.orEmpty()
                val displaySymbol = detailed.currencyDisplaySymbol.orEmpty()
                val formattedNum = CurrencyFormatter.format(detailed.balance, detailed.decimalPlaces)
                if (code.isNotEmpty()) "$code $displaySymbol$formattedNum" else "$displaySymbol$formattedNum"
            } else {
                ""
            }
        } else {
            detailed.status?.name ?: unknownStatus
        }

        return DetailedPocket(
            accountId = detailed.pocket.accountId,
            name = detailed.productName ?: unknownAccount,
            accountNumber = detailed.pocket.accountNumber,
            balanceOrStatus = balanceStr,
            status = detailed.status ?: AccountStatus.UNKNOWN,
        )
    }

    val loanList = mutableListOf<DetailedPocket>()
    val savingsList = mutableListOf<DetailedPocket>()
    val shareList = mutableListOf<DetailedPocket>()
    for (account in this) {
        when (account.pocket.accountType) {
            AccountType.LOAN -> loanList.add(mapToUiModel(account))
            AccountType.SAVINGS -> savingsList.add(mapToUiModel(account))
            AccountType.SHARE -> shareList.add(mapToUiModel(account))
        }
    }

    // Multi-currency per-code sum, each rendered `"$code $displaySymbol$sum"` and
    // joined by newline (the total-balance widget renders multi-line text).
    val balancesByCurrency = this
        .filter { it.status == AccountStatus.ACTIVE && it.balance != null && it.currencyCode != null }
        .groupBy { it.currencyCode!! }
        .map { (currencyCode, accounts) ->
            val sum = accounts.sumOf { it.balance ?: 0.0 }
            val decimalPlaces = accounts.first().decimalPlaces
            val displaySymbol = accounts.first().currencyDisplaySymbol.orEmpty()
            val formattedNum = CurrencyFormatter.format(sum, decimalPlaces)
            if (currencyCode.isNotEmpty()) "$currencyCode $displaySymbol$formattedNum" else "$displaySymbol$formattedNum"
        }

    val formattedTotal = if (balancesByCurrency.isNotEmpty()) {
        balancesByCurrency.joinToString("\n")
    } else {
        "0.00"
    }

    return PocketBuckets(
        totalBalance = formattedTotal,
        savingsAccounts = savingsList,
        loanAccounts = loanList,
        shareAccounts = shareList,
    )
}

@Composable
internal fun PocketSectionHeader(
    title: StringResource,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(title),
        modifier = modifier.fillMaxWidth(),
        style = KptTheme.typography.titleSmall,
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
            .heightIn(min = 112.dp)
            .fillMaxWidth()
            .background(KptTheme.colorScheme.primary),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(KptTheme.spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
            ) {
                Text(
                    text = stringResource(Res.string.feature_pocket_dashboard_total_balance),
                    style = KptTheme.typography.bodySmall,
                    color = KptTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                )

                Text(
                    text = totalBalance,
                    style = KptTheme.typography.headlineSmall,
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
            .padding(KptTheme.spacing.lg),
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

        Spacer(modifier = Modifier.height(KptTheme.spacing.xl))

        Text(
            text = stringResource(Res.string.feature_pocket_empty_title),
            style = KptTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = KptTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(KptTheme.spacing.md))

        Text(
            text = stringResource(Res.string.feature_pocket_empty_description),
            style = KptTheme.typography.bodyLarge,
            color = KptTheme.colorScheme.secondary,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(KptTheme.spacing.xl))

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
        state = ScreenState.Content(samplePocketAccounts),
        freshness = FreshnessSignal.initial(),
        onAction = {},
        onRetry = {},
    )
}

@Preview
@Composable
private fun EmptyPocketContentPreview() {
    EmptyPocketContent(
        onLinkFirstAccount = {},
    )
}

private val samplePocketAccounts: List<DetailedPocketAccount> = listOf(
    DetailedPocketAccount(
        pocket = PocketAccount(
            pocketId = 1L,
            id = 1L,
            accountId = 1L,
            accountType = AccountType.SAVINGS,
            accountNumber = "1004859238",
        ),
        productName = "Emergency Fund",
        balance = 5_000.0,
        currencyCode = "USD",
        decimalPlaces = 2,
        status = AccountStatus.ACTIVE,
        currencyDisplaySymbol = "$",
    ),
    DetailedPocketAccount(
        pocket = PocketAccount(
            pocketId = 2L,
            id = 2L,
            accountId = 3L,
            accountType = AccountType.LOAN,
            accountNumber = "3009284756",
        ),
        productName = "Personal Loan",
        balance = 10_000.0,
        currencyCode = "MXN",
        decimalPlaces = 2,
        status = AccountStatus.ACTIVE,
        currencyDisplaySymbol = "MX$",
    ),
    DetailedPocketAccount(
        pocket = PocketAccount(
            pocketId = 3L,
            id = 3L,
            accountId = 5L,
            accountType = AccountType.SHARE,
            accountNumber = "5001129384",
        ),
        productName = "Company Shares",
        balance = 2_500.0,
        currencyCode = "USD",
        decimalPlaces = 2,
        status = AccountStatus.ACTIVE,
        currencyDisplaySymbol = "$",
    ),
)
