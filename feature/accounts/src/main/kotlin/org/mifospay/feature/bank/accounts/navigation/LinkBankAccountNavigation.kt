/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.bank.accounts.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import org.mifospay.feature.bank.accounts.link.LinkBankAccountRoute

const val LINK_BANK_ACCOUNT_ROUTE = "link_bank_account_route"

fun NavController.navigateToLinkBankAccount(navOptions: NavOptions? = null) =
    navigate(LINK_BANK_ACCOUNT_ROUTE, navOptions)

fun NavGraphBuilder.linkBankAccountScreen(
    onBackClick: () -> Unit,
) {
    composable(route = LINK_BANK_ACCOUNT_ROUTE) {
        LinkBankAccountRoute(
            onBackClick = onBackClick,
        )
    }
}
