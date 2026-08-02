/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
@file:Suppress("unused")

package kpt.core.analytics

import androidx.compose.runtime.Composable

@Deprecated(
    "Renamed to KptAnalyticsTracker",
    ReplaceWith("KptAnalyticsTracker"),
    level = DeprecationLevel.WARNING,
)
typealias MifosAnalyticsTracker = KptAnalyticsTracker

@Deprecated(
    "Renamed to rememberKptAnalyticsTracker",
    ReplaceWith("rememberKptAnalyticsTracker()"),
    level = DeprecationLevel.WARNING,
)
@Composable
fun rememberMifosAnalyticsTracker(): KptAnalyticsTracker = rememberKptAnalyticsTracker()
