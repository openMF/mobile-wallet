/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */

// feature-deps.gradle.kts — the FORK-OWNED feature-dependency seam (white-label, S7/F4).
//
// `cmp-navigation/build.gradle.kts` (template-owned) applies this script via `apply(from = ...)`. A fork
// adds/removes a feature module dependency by editing ONLY this file — it never touches the template build
// file, so a template sync full-copies `cmp-navigation/build.gradle.kts` while these deps survive. This is
// the build-graph twin of the `cmp-navigation/registry` runtime seams (FeatureRegistry etc.); adding a
// feature is: include it in `settings.gradle.kts` (or `settings.local.gradle.kts`), add its dep here, and
// register its module + routes in `FeatureRegistry`.
//
// String `project(":feature:x")` notation (not the type-safe `projects.feature.x` accessors) is used
// deliberately: type-safe project accessors are NOT generated for `apply(from = ...)` script plugins. The
// `commonMainImplementation` configuration is the KMP-created implementation configuration for the
// commonMain source set; it exists by the time this script is applied (after the `kotlin { }` block).
//
// Mifos Pay's existing ~28 feature-module deps stay inline in `cmp-navigation/build.gradle.kts`'s own
// `commonMain.dependencies { }` block (pre-dates this seam's adoption; not moved to avoid an unnecessary
// bulk-move of working config). Any NEW feature dependency should go here instead, so it survives the
// next template sync.

dependencies {
    // Fork feature dependencies go below this line, e.g.:
    // "commonMainImplementation"(project(":feature:my-feature"))
}
