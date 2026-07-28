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

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.runComposeUiTest
import mobile_wallet.feature.pocket.generated.resources.Res
import mobile_wallet.feature.pocket.generated.resources.feature_pocket_error_load_accounts
import org.mifospay.core.model.pocket.AccountStatus
import org.mifospay.feature.pocket.viewmodels.DetailedPocket
import org.mifospay.feature.pocket.viewmodels.PocketDashboardAction
import org.mifospay.feature.pocket.viewmodels.PocketDashboardState
import org.mifospay.feature.pocket.viewmodels.PocketDashboardUiState
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class PocketDashboardScreenTest {

    @Test
    fun givenSuccessState_whenRendering_thenDashboardShowsBalanceAndAllAccountCategories() = runComposeUiTest {
        val state = PocketDashboardState(
            totalBalance = "MX$ 10,000.00",
            savingsAccounts = listOf(
                DetailedPocket(
                    accountId = 1L,
                    name = "Emergency Fund",
                    accountNumber = "1004859238",
                    balanceOrStatus = "$ 5,000.00",
                    status = AccountStatus.ACTIVE,
                ),
            ),
            loanAccounts = listOf(
                DetailedPocket(
                    accountId = 2L,
                    name = "Personal Loan",
                    accountNumber = "3009284756",
                    balanceOrStatus = "ACTIVE",
                    status = AccountStatus.ACTIVE,
                ),
            ),
            shareAccounts = listOf(
                DetailedPocket(
                    accountId = 3L,
                    name = "Company Shares",
                    accountNumber = "5001129384",
                    balanceOrStatus = "PENDING",
                    status = AccountStatus.PENDING,
                ),
            ),
            uiState = PocketDashboardUiState.Success,
        )

        setContent { PocketDashboardContent(state = state, onAction = {}) }

        onNodeWithText("MX$ 10,000.00").performScrollTo().assertIsDisplayed()
        onNodeWithText("Emergency Fund").performScrollTo().assertIsDisplayed()
        onNodeWithText("1004859238").performScrollTo().assertIsDisplayed()
        onNodeWithText("Personal Loan").performScrollTo().assertIsDisplayed()
        onNodeWithText("3009284756").performScrollTo().assertIsDisplayed()
        onNodeWithText("Company Shares").performScrollTo().assertIsDisplayed()
        onNodeWithText("5001129384").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun givenSuccessState_whenManageClicked_thenManageActionIsEmitted() = runComposeUiTest {
        var emittedAction: PocketDashboardAction? = null
        val state = PocketDashboardState(
            totalBalance = "MX$ 10,000.00",
            uiState = PocketDashboardUiState.Success,
        )

        setContent { PocketDashboardContent(state = state, onAction = { emittedAction = it }) }

        onNodeWithText("Manage").performClick()

        assertEquals(PocketDashboardAction.ManagePocket, emittedAction)
    }

    @Test
    fun givenEmptyState_whenLinkFirstAccountClicked_thenLinkActionIsEmitted() = runComposeUiTest {
        var emittedAction: PocketDashboardAction? = null

        setContent {
            PocketDashboardContent(
                state = PocketDashboardState(uiState = PocketDashboardUiState.Empty),
                onAction = { emittedAction = it },
            )
        }

        onNodeWithText("Link Your First Account").performClick()

        assertEquals(PocketDashboardAction.LinkFirstAccount, emittedAction)
    }

    @Test
    fun givenErrorState_whenRendering_thenErrorMessageIsDisplayed() = runComposeUiTest {
        setContent {
            PocketDashboardContent(
                state = PocketDashboardState(
                    uiState = PocketDashboardUiState.Error(Res.string.feature_pocket_error_load_accounts),
                ),
                onAction = {},
            )
        }

        onNodeWithText("Failed to load pocket accounts").assertIsDisplayed()
    }
}
