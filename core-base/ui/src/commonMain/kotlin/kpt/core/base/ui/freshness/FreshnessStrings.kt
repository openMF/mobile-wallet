/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.ui.freshness

import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * Humanises a duration into a "X ago" tooltip phrase for `FreshnessIndicator`.
 *
 * Boundaries (closed on the lower side):
 *  - `[0, 60s)`     → "just now"
 *  - `[60s, 60m)`   → "Nm ago"
 *  - `[1h, 24h)`    → "Nh ago"
 *  - `[24h, 48h)`   → "yesterday"
 *  - `[48h, ∞)`     → "N days ago"
 *
 * Pure function — no allocations beyond the resulting String. Pluralisation is
 * intentionally simple ("1 days ago" never occurs because the 24-48h slot already
 * fires "yesterday").
 */
fun humanizeDuration(d: Duration): String = when {
    d < 60.seconds -> "just now"
    d < 60.minutes -> "${d.inWholeMinutes}m ago"
    d < 24.hours -> "${d.inWholeHours}h ago"
    d < 48.hours -> "yesterday"
    else -> "${d.inWholeDays} days ago"
}
