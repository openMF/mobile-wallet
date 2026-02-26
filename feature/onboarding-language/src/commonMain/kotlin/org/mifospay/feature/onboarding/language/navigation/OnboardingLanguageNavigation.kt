/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.feature.onboarding.language.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import org.mifospay.core.ui.composableWithSlideTransitions
import org.mifospay.feature.onboarding.language.OnboardingLanguageScreenRoute

const val ONBOARDING_LANGUAGE_ROUTE = "onboarding_language_route"

fun NavController.navigateToOnboardingLanguage(navOptions: NavOptions? = null) {
    this.navigate(ONBOARDING_LANGUAGE_ROUTE, navOptions)
}

fun NavGraphBuilder.onboardingLanguageScreen(
    onNavigateToNext: () -> Unit,
) {
    composableWithSlideTransitions(route = ONBOARDING_LANGUAGE_ROUTE) {
        OnboardingLanguageScreenRoute(
            onNavigateToNext = onNavigateToNext,
        )
    }
}
