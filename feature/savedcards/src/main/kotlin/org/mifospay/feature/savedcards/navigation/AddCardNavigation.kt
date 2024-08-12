/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.savedcards.navigation

import androidx.compose.material.navigation.bottomSheet
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.mifospay.core.model.entity.savedcards.Card
import org.mifospay.feature.savedcards.AddCardDialogSheet

const val ADD_CARD_ROUTE = "add_card_route"

fun NavController.navigateToAddCard(navOptions: NavOptions? = null) {
    navigate(ADD_CARD_ROUTE, navOptions)
}

fun NavGraphBuilder.addCardScreen(
    onDismiss: () -> Unit,
    onAddCard: (Card) -> Unit,
) {
    bottomSheet(
        route = ADD_CARD_ROUTE,
    ) {
        AddCardDialogSheet(
            cancelClicked = onDismiss,
            addClicked = onAddCard,
            onDismiss = onDismiss,
        )
    }
}
