/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.store.wallet.office

/**
 * Store5 key for the LEDGER `offices` store — the WHOLE Fineract office list.
 *
 * ## Singleton-key deviation (documented)
 *
 * Unlike every other Phase-5 read store (which keys per-client), the Fineract
 * office list is global reference data — one list per Fineract instance, not
 * per authenticated client — so this key is a `data object` (a value-less
 * Kotlin singleton). Store5's [Store] uses the key for `equals` / `hashCode`
 * routing into its in-memory memory cache; a `data object` satisfies both
 * (Kotlin generates them). The DAO ignores the key entirely (its queries
 * operate over the whole table); the SoT's `reader = { _ -> dao.observeAll() }`
 * lambda receives [OfficesKey] and drops it.
 *
 * Two callers may be reading the offices list concurrently (e.g. FastMpayProcessor
 * mid-QR-scan + a hypothetical office picker); both share the SAME cache slot
 * because the key `equals` itself. This is the intended behavior — global
 * reference data has exactly one cache row, not N.
 *
 * Cache-key string used at the repository layer's `asScreenStream(cacheKey = …)`
 * call is the constant `"wallet_offices"`, so `FetchedAtRepository`'s freshness
 * band lookups stay collision-free with the future addition of a
 * `wallet_offices-<tenantId>` variant (if / when tenant switching lands in this
 * store surface).
 */
data object OfficesKey
