/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.shared.utils

import androidx.compose.ui.graphics.vector.ImageVector
import mobile_wallet.mifospay_shared.generated.resources.Res
import mobile_wallet.mifospay_shared.generated.resources.app_name
import mobile_wallet.mifospay_shared.generated.resources.finance
import mobile_wallet.mifospay_shared.generated.resources.home
import mobile_wallet.mifospay_shared.generated.resources.payments
import mobile_wallet.mifospay_shared.generated.resources.profile
import org.jetbrains.compose.resources.StringResource
import org.mifospay.core.designsystem.icon.MifosIcons

/**
 * Type for the top level destinations in the application. Each of these destinations
 * can contain one or more screens (based on the window size). Navigation from one screen to the
 * next within a single destination will be handled directly in composables.
 */
internal enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val iconText: StringResource,
    val titleText: StringResource,
) {
    HOME(
        selectedIcon = MifosIcons.Home,
        unselectedIcon = MifosIcons.HomeBoarder,
        iconText = Res.string.home,
        titleText = Res.string.app_name,
    ),
    PAYMENTS(
        selectedIcon = MifosIcons.Payment,
        unselectedIcon = MifosIcons.Payment,
        iconText = Res.string.payments,
        titleText = Res.string.payments,
    ),
    FINANCE(
        selectedIcon = MifosIcons.Finance,
        unselectedIcon = MifosIcons.FinanceBoarder,
        iconText = Res.string.finance,
        titleText = Res.string.finance,
    ),
    PROFILE(
        selectedIcon = MifosIcons.Profile,
        unselectedIcon = MifosIcons.ProfileBoarder,
        iconText = Res.string.profile,
        titleText = Res.string.profile,
    ),
}
