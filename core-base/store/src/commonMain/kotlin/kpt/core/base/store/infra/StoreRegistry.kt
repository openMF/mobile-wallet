/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.store.infra

import org.koin.core.qualifier.StringQualifier
import org.koin.core.qualifier.named

/**
 * Base registry for Store DI qualifiers (Koin).
 *
 * Projects extend this in their `core/store` module to define
 * app-specific store qualifiers that prevent type erasure collisions
 * when multiple [org.mobilenativefoundation.store.store5.Store] instances
 * share the same erased type signature.
 *
 * Example:
 * ```
 * object AppStoreRegistry : StoreRegistry() {
 *     val ExchangeRates = store("exchangeRates")
 *     val CoinMarkets = store("coinMarkets")
 * }
 * ```
 */
abstract class StoreRegistry {
    protected fun store(name: String): StringQualifier = named(name)
}
