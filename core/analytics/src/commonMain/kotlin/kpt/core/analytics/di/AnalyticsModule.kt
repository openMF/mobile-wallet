/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.analytics.di

import kpt.core.base.analytics.AnalyticsHelper
import kpt.core.base.analytics.NoOpAnalyticsHelper
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Project-scoped Koin module exposing the **default** [AnalyticsHelper] binding for the toolkit.
 *
 * ## Why this exists alongside [kpt.core.base.analytics.di.analyticsModule]?
 *
 * `core-base/analytics/.../analyticsModule` is an `expect val` with platform-specific
 * actuals — those actuals bind [AnalyticsHelper] to the platform Firebase implementation
 * (Android Firebase Analytics, iOS Firebase Analytics, etc.).
 *
 * `core/analytics/.../coreAnalyticsModule` (this module) sits **on top** at the project layer
 * and binds [AnalyticsHelper] to [NoOpAnalyticsHelper]. The toolkit ships **without** analytics
 * by default — every event call becomes a no-op. Forks that want real analytics override this
 * binding (or skip including this module and rely on `core-base/analytics`'s Firebase actuals).
 *
 * ## Fork binding examples
 *
 * **(a) Keep the no-op (toolkit default — privacy-respecting demo build):**
 * ```kotlin
 * startKoin {
 *     modules(coreAnalyticsModule, ...) // NoOpAnalyticsHelper installed
 * }
 * ```
 *
 * **(b) Use the framework's Firebase binding (production Mifos build):**
 * ```kotlin
 * startKoin {
 *     // Skip coreAnalyticsModule entirely — analyticsModule (expect/actual) binds Firebase.
 *     modules(analyticsModule, ...)
 * }
 * ```
 *
 * **(c) Wire your own provider (Mixpanel / Amplitude / Segment):**
 * ```kotlin
 * val myAnalyticsModule = module {
 *     single<AnalyticsHelper> { MixpanelAnalyticsHelper(getApiKey()) }
 * }
 * startKoin {
 *     modules(myAnalyticsModule, ...) // Last-binding-wins overrides coreAnalyticsModule
 * }
 * ```
 *
 * **(d) Use the [kpt.core.base.analytics.StubAnalyticsHelper] for debug builds:**
 * ```kotlin
 * val debugAnalyticsModule = module {
 *     single<AnalyticsHelper> { StubAnalyticsHelper() } // Prints events to console
 * }
 * ```
 *
 * @see kpt.core.base.analytics.AnalyticsHelper Contract being bound
 * @see kpt.core.base.analytics.NoOpAnalyticsHelper Default toolkit implementation
 * @see kpt.core.base.analytics.di.analyticsModule Framework-level expect/actual variant
 */
val coreAnalyticsModule = module {
    singleOf(::NoOpAnalyticsHelper) bind AnalyticsHelper::class
}
