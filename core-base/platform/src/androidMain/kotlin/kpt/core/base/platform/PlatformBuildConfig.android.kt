/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-pay/blob/master/LICENSE.md
 */
package kpt.core.base.platform

/**
 * Android implementation of PlatformBuildConfig.
 *
 * NOTE (foundation-compile Phase 2 restore, 2026-08): the fork's previous
 * implementation read `BuildConfig.DEBUG`, but AGP 9.2's unified KMP library
 * plugin (`com.android.kotlin.multiplatform.library`) does not generate
 * `BuildConfig` for library modules — the experimental property
 * `android.experimental.kmp.enableAndroidBuildConfig=true` (set by
 * KMPCoreBaseLibraryConventionPlugin) does NOT wire up the generator in this
 * AGP version, and `KotlinMultiplatformAndroidLibraryExtension` exposes no
 * `buildFeatures.buildConfig` DSL. `BuildConfig.DEBUG` therefore fails to
 * resolve.
 *
 * TODO(follow-up): restore proper debug detection via one of:
 *   (a) an `init(app: Application)` on PlatformBuildConfig called from
 *       `MifosPayApplication.onCreate`, using
 *       `context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE`
 *       (this is the pattern kpt.core.base.platform.review.AppReviewManagerImpl
 *       uses in the template), OR
 *   (b) inject `mobile.wallet.kmpflavors.KmpFlavorsRuntime.isDebug` — the
 *       kmp-product-flavors plugin generates this in :cmp-navigation and it
 *       exposes `isDebug` as a compile-time constant — but that requires
 *       :core-base:platform to depend on the module that owns the generator or
 *       to trigger runtime generation for itself.
 *
 * Hardcoded to `false` (production-safe default) so `:core-base:platform`
 * compiles cleanly. Sole visible impact: the `ServerInstanceInfo` debug
 * widget on the login screen (feature/auth/LoginScreen.kt) no longer shows
 * in debug builds until the TODO is resolved.
 */
actual object PlatformBuildConfig {
    actual val isDebug: Boolean = false
}
