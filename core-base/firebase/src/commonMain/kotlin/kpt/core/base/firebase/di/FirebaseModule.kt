/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.firebase.di

import io.github.mobilebytelabs.kmptoolkit.firebase.analytics.AnalyticsConfig
import io.github.mobilebytelabs.kmptoolkit.firebase.analytics.AnalyticsHelper
import io.github.mobilebytelabs.kmptoolkit.firebase.analytics.di.AnalyticsModule
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Base Firebase integration (the "main implementation", template-owned in core-base/firebase).
 *
 * Binds the published `cmp-firebase` engine's **real Firebase-backed** [AnalyticsHelper] (GitLive on
 * supported targets, Measurement-Protocol/NoOp fallback elsewhere) + its [PerformanceTracker]. This
 * is the production wiring: a fork includes [firebaseModule] to turn analytics ON.
 *
 * The project layer (`core/firebase`) supplies the toolkit **default** (NoOp — privacy-respecting
 * demo build) plus the app-owned domain event catalog; last-binding-wins lets a fork swap in a
 * different provider. Crashlytics wiring will join this module as core-base/firebase grows into the
 * single host for all Firebase services.
 */
val firebaseModule: Module = module {
    single<AnalyticsHelper> {
        AnalyticsModule.analyticsHelper(
            mode = AnalyticsModule.Mode.Firebase,
            config = AnalyticsConfig(),
        )
    }
    single { AnalyticsModule.performanceTracker(get()) }
}
