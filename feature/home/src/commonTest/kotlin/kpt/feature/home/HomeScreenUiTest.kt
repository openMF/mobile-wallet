/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.home

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import kpt.core.designsystem.theme.KptTheme
import kpt.feature.home.demo.HomeDashboard
import kpt.feature.home.demo.ui.EmptyBillReminderRepository
import kpt.feature.home.demo.ui.EmptyLoanRepository
import kpt.feature.home.demo.ui.FakeDashboardCurrencyRepository
import kpt.feature.home.demo.ui.HomeViewModel
import kpt.feature.home.demo.ui.LoadingEconomicRatesRepository
import kpt.feature.home.ui.TestTags
import kotlin.test.Test

/**
 * Compose Multiplatform UI test for [HomeScreen] — the top-level bottom-nav
 * shell that wraps [HomeDashboard] inside a framework-owned [Scaffold] with
 * a settings top-bar action.
 *
 * Reuses the same `internal` fake repositories from [HomeDashboardViewModelTest]
 * (all in the same module's `commonTest`). Constructs the real [HomeViewModel]
 * with no Koin and asserts [TestTags.Home.SCREEN] — the root [Scaffold] testTag
 * added to [HomeScreen] — which is always rendered regardless of dashboard state
 * (RULE-KMP-COMPOSE-UITEST-001 CU-1..CU-3).
 */
@OptIn(ExperimentalTestApi::class)
class HomeScreenUiTest {

    @Test
    fun screenScaffoldIsDisplayed() = runComposeUiTest {
        val viewModel = HomeViewModel(
            loanRepository = EmptyLoanRepository,
            billReminderRepository = EmptyBillReminderRepository,
            economicRatesRepository = LoadingEconomicRatesRepository,
            currencyRepository = FakeDashboardCurrencyRepository(),
        )
        setContent {
            KptTheme {
                HomeScreen(
                    onSettingsClick = {},
                    // The fork-owned home body — here the demo dashboard wired to fake repos, mirroring
                    // production where cmp-navigation's BackboneRegistry.homeBody supplies it.
                    homeBody = {
                        HomeDashboard(
                            onNavigateToLoans = {},
                            onNavigateToBills = {},
                            onNavigateToRates = {},
                            onNavigateToExchangeRates = {},
                            onNavigateToRateHistory = {},
                            onNavigateToMacro = {},
                            onNavigateToEmi = {},
                            onNavigateToAffordability = {},
                            onNavigateToAmortization = {},
                            onNavigateToLoanComparison = {},
                            onNavigateToLoanCalcWizard = {},
                            onNavigateToCrypto = {},
                            viewModel = viewModel,
                        )
                    },
                )
            }
        }
        onNodeWithTag(TestTags.Home.SCREEN).assertIsDisplayed()
    }
}
