/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */

// settings.local.gradle.kts — the FORK-OWNED module-include seam (white-label, B1/T11).
//
// `settings.gradle.kts` (template-owned, synced) applies this file via `apply(from = ...)` if it exists,
// so a fork adds/removes its own Gradle module includes HERE without editing the template settings file —
// a template sync full-copies `settings.gradle.kts` while these includes survive. This is the
// settings-graph twin of `feature-deps.gradle.kts` (the dependency seam) and the `cmp.navigation.registry`
// runtime seams.
//
// Mifos Pay's existing ~30 feature-module includes stay inline in `settings.gradle.kts` (pre-dates
// this seam's adoption; not moved to avoid an unnecessary bulk-move of working config). Any NEW
// module a future contributor adds should go here instead, so it survives the next template sync.
//
// Example — add a consumer feature module:
//     include(":feature:my-feature")

// Fork module includes go below this line.
