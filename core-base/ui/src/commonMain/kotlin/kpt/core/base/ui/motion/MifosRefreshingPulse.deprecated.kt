/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
@file:Suppress("unused")

package kpt.core.base.ui.motion

import androidx.compose.ui.Modifier

@Deprecated(
    "Renamed to kptRefreshingPulse",
    ReplaceWith("kptRefreshingPulse(active)"),
    level = DeprecationLevel.WARNING,
)
fun Modifier.mifosRefreshingPulse(active: Boolean): Modifier = kptRefreshingPulse(active)
