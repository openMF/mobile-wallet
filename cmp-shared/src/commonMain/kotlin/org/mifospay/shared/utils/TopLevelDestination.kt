/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifospay.shared.utils

import androidx.compose.ui.graphics.vector.ImageVector
import cmp.shared.generated.resources.Res
import cmp.shared.generated.resources.app_name
import cmp.shared.generated.resources.finance
import cmp.shared.generated.resources.history
import cmp.shared.generated.resources.home
import cmp.shared.generated.resources.payments
import cmp.shared.generated.resources.transaction_history
import org.jetbrains.compose.resources.StringResource
import org.mifospay.core.designsystem.icon.MifosIcons

/**
 * Type for the top level destinations in the application. Each of these destinations
 * can contain one or more screens (based on the window size). Navigation from one screen to the
 * next within a single destination will be handled directly in composables.
 */
// TODO Profile is not using self api
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
    HISTORY(
        selectedIcon = MifosIcons.History,
        unselectedIcon = MifosIcons.HistoryBoarder,
        iconText = Res.string.history,
        titleText = Res.string.transaction_history,
    ),
}
