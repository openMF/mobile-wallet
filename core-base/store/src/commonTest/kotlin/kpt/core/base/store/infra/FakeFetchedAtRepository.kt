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

import kotlinx.atomicfu.atomic
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Test-only [FetchedAtRepository] backed by an in-memory map.
 *
 * **Never on the production classpath.** Lives in `commonTest` so the framework
 * cannot accidentally fall back to it in production — that would defeat the
 * "Room-only persistence" architectural decision (see PLAN-fw-260504-store-policy-
 * and-fetched-at-persistence). Production consumers must wire a real impl
 * (typically Room-backed in `core/data`).
 *
 * Thread-safe via atomicfu CAS on an immutable map snapshot.
 */
@OptIn(ExperimentalTime::class)
internal class FakeFetchedAtRepository : FetchedAtRepository {
    private val state = atomic<Map<String, Instant>>(emptyMap())

    override suspend fun read(storeKey: String): Instant? = state.value[storeKey]

    override suspend fun write(storeKey: String, instant: Instant) {
        while (true) {
            val current = state.value
            val updated = current + (storeKey to instant)
            if (state.compareAndSet(current, updated)) return
        }
    }
}
