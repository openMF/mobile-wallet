/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifos.lib.loan.ui.selectLoanProvider

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mobile_wallet.libs.mifos_loans.generated.resources.Res
import mobile_wallet.libs.mifos_loans.generated.resources.feature_select_loan_provider_choose_provider
import mobile_wallet.libs.mifos_loans.generated.resources.feature_select_loan_provider_top_bar_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifospay.core.designsystem.component.MifosCard
import org.mifospay.core.designsystem.component.MifosScaffold
import org.mifospay.core.ui.utils.EventsEffect
import template.core.base.designsystem.theme.KptTheme

/**
 * Entry point for the Select Loan Provider screen — the new first step of the loan-application
 * wizard, shown when the user taps "Apply for Loan".
 *
 * Shows a short list of (currently dummy/placeholder) loan providers; tapping one proceeds into
 * the existing wizard starting at Select Loan Type.
 *
 * @param navigateBack Callback to return to the previous screen.
 * @param navigateToSelectLoanType Callback to proceed into the existing loan-type selection step.
 * @param viewModel The state holder responsible for the (static) provider list.
 */
@Composable
internal fun SelectLoanProviderScreen(
    navigateBack: () -> Unit,
    navigateToSelectLoanType: (clientId: Long, providerId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SelectLoanProviderViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            SelectLoanProviderEvent.NavigateBack -> navigateBack()

            is SelectLoanProviderEvent.NavigateToSelectLoanType -> {
                navigateToSelectLoanType(event.clientId, event.providerId)
            }
        }
    }

    SelectLoanProviderContent(
        state = state,
        modifier = modifier,
        onAction = remember(viewModel) {
            { viewModel.trySendAction(it) }
        },
    )
}

@Composable
private fun SelectLoanProviderContent(
    state: SelectLoanProviderState,
    onAction: (SelectLoanProviderAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosScaffold(
        modifier = modifier,
        backPress = { onAction(SelectLoanProviderAction.NavigateBack) },
        topBarTitle = stringResource(Res.string.feature_select_loan_provider_top_bar_title),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(top = KptTheme.spacing.md),
        ) {
            Text(
                text = stringResource(Res.string.feature_select_loan_provider_choose_provider),
                style = KptTheme.typography.titleSmall,
                modifier = Modifier.padding(horizontal = KptTheme.spacing.md),
            )

            LazyColumn(
                contentPadding = PaddingValues(KptTheme.spacing.md),
                verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
            ) {
                items(state.providers, key = { it.id }) { provider ->
                    LoanProviderCard(
                        provider = provider,
                        onClick = {
                            onAction(SelectLoanProviderAction.ProviderClicked(provider.id))
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun LoanProviderCard(
    provider: LoanProviderOption,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MifosCard(
        modifier = modifier,
        shape = KptTheme.shapes.medium,
        elevation = KptTheme.elevation.level2,
        onClick = onClick,
    ) {
        Text(
            text = provider.name,
            style = KptTheme.typography.titleMedium,
            modifier = Modifier.padding(KptTheme.spacing.lg),
        )
    }
}
