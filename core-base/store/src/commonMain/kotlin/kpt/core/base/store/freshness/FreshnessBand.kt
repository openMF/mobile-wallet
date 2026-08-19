/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.store.freshness

/**
 * Pure time-relative staleness band for cached data — independent of network state.
 *
 * Computed by [FreshnessBands.bandFor] from `(now, lastSyncedAt, ttl, lastError)` alone.
 * Network connectivity is intentionally NOT an input: UI surfaces the band via
 * `FreshnessIndicator`, while `ConnectivityBanner` handles the orthogonal "is the
 * device online" question.
 */
enum class FreshnessBand {
    /** No successful sync yet AND no error — never been fetched. UI hides the indicator. */
    Initial,

    /** `age <= ttl` AND no last error. Neutral tint, "Updated X ago". */
    Fresh,

    /** `ttl < age <= 3*ttl` AND no last error. Tertiary tint + "Refresh now". */
    Stale,

    /** `age > 3*ttl` OR `lastError != null`. Error tint + categorized error message + "Retry". */
    VeryStale,
}
