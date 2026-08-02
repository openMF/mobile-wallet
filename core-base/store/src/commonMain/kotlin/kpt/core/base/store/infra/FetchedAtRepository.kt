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

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Persists "when was this Store's data last successfully fetched from network",
 * keyed by [storeKey]. Used by [PagingScreenStream] and [ScreenDataStream] so the
 * staleness banner can render real "Updated 5m ago" timestamps that survive
 * ViewModel destruction, navigation, and app process restart.
 *
 * **No production implementation ships in this module.** Consumer apps must
 * provide a concrete impl (typically Room-backed) and inject it via DI. This
 * keeps the framework's persistence story unambiguous: there is one supported
 * answer, and consumers wire it explicitly.
 *
 * Tests use [FakeFetchedAtRepository] (in commonTest), which is **never on the
 * production classpath**.
 *
 * Recommended consumer impl: a Room-backed implementation against a small
 * framework-owned table (`framework_fetched_at`) — see `core/data/.../
 * RoomFetchedAtRepository.kt` in the project's data module.
 *
 * Convention for [storeKey]:
 * - Single-key streams: `"<feature>:<storeName>:<key>"` (e.g., `"crypto:coinDetail:bitcoin"`)
 * - Paginated streams: `"<feature>:<storeName>"` (e.g., `"crypto:coinMarkets"`)
 *   — one timestamp per store, latest-page-fetch wins. Granularity at the store
 *   level matches user intent ("how stale is my data") and keeps the framework
 *   table small.
 */
@OptIn(ExperimentalTime::class)
interface FetchedAtRepository {
    /** Returns the last persisted timestamp for [storeKey], or null if none. */
    suspend fun read(storeKey: String): Instant?

    /** Persists [instant] as the latest fetch time for [storeKey]. */
    suspend fun write(storeKey: String, instant: Instant)
}
