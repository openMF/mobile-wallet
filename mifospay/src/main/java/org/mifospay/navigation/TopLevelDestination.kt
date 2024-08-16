/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import org.mifospay.R
import org.mifospay.core.designsystem.icon.MifosIcons

/**
 * Type for the top level destinations in the application. Each of these destinations
 * can contain one or more screens (based on the window size). Navigation from one screen to the
 * next within a single destination will be handled directly in composables.
 */
enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val iconTextId: Int,
    val titleTextId: Int,
) {
    HOME(
        selectedIcon = MifosIcons.Home,
        unselectedIcon = MifosIcons.HomeBoarder,
        iconTextId = R.string.home,
        titleTextId = R.string.app_name,
    ),
    PAYMENTS(
        selectedIcon = MifosIcons.Payment,
        unselectedIcon = MifosIcons.Payment,
        iconTextId = R.string.payments,
        titleTextId = R.string.payments,
    ),
    FINANCE(
        selectedIcon = MifosIcons.Finance,
        unselectedIcon = MifosIcons.FinanceBoarder,
        iconTextId = R.string.finance,
        titleTextId = R.string.finance,
    ),
    PROFILE(
        selectedIcon = MifosIcons.Profile,
        unselectedIcon = MifosIcons.ProfileBoarder,
        iconTextId = R.string.profile,
        titleTextId = R.string.profile,
    ),
}
