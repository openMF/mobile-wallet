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
import androidx.compose.ui.test.runComposeUiTest
import org.mifospay.core.model.enums.AccountType
import org.mifospay.feature.pocket.viewmodels.ManagePocketAccount
import org.mifospay.feature.pocket.viewmodels.ManagePocketAction
import org.mifospay.feature.pocket.viewmodels.ManagePocketState
import org.mifospay.feature.pocket.viewmodels.ManagePocketUiState
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class ManagePocketScreenTest {

    @Test
    fun givenSuccessState_withLinkedAccounts_thenAccountsAreDisplayed() = runComposeUiTest {
        val state = ManagePocketState(
            linkedAccounts = listOf(
                ManagePocketAccount(
                    accountId = 1L,
                    name = "Vacation Savings",
                    accountNumber = "9988776655",
                    accountType = AccountType.SAVINGS,
                    mappingId = 1L,
                ),
            ),
            uiState = ManagePocketUiState.Success,
        )

        setContent {
            ManagePocketContent(state = state, onAction = {})
        }

        onNodeWithText("Vacation Savings").assertIsDisplayed()
        onNodeWithText("9988776655").assertIsDisplayed()
    }

    @Test
    fun givenSuccessState_whenLinkMoreAccountsIsClicked_thenLinkActionIsEmitted() = runComposeUiTest {
        var emittedAction: ManagePocketAction? = null

        setContent {
            ManagePocketContent(
                state = ManagePocketState(uiState = ManagePocketUiState.Success),
                onAction = { emittedAction = it },
            )
        }

        onNodeWithText("Link").performClick()

        assertEquals(ManagePocketAction.OpenLinkAccounts, emittedAction)
    }

    @Test
    fun givenSuccessState_withoutLinkedAccounts_thenEmptyMessageIsDisplayed() = runComposeUiTest {
        setContent {
            ManagePocketContent(
                state = ManagePocketState(uiState = ManagePocketUiState.Success),
                onAction = {},
            )
        }

        onNodeWithText("No accounts are linked to pocket").assertIsDisplayed()
    }
}
