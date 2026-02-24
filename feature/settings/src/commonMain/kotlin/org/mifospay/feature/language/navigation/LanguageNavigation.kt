/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.language.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import org.mifospay.core.ui.composableWithSlideTransitions
import org.mifospay.feature.language.LanguageScreenRoute

const val LANGUAGE_ROUTE = "language_route"

fun NavController.navigateToLanguage(navOptions: NavOptions? = null) {
    this.navigate(LANGUAGE_ROUTE, navOptions)
}

fun NavGraphBuilder.languageDestination(
    onBackPress: () -> Unit,
) {
    composableWithSlideTransitions(route = LANGUAGE_ROUTE) {
        LanguageScreenRoute(
            onNavigateBack = onBackPress,
        )
    }
}
