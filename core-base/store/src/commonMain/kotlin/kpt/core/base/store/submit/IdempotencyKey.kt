/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.store.submit

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Idempotency-key generator for client-side de-duplication of retried submissions.
 *
 * The contract: a caller generates one key per logical user intent (e.g. "tap pay"),
 * attaches it to the outgoing mutation, and reuses the **same** key on any retry of
 * that intent. Server-side de-duplication (when supported) is then able to recognise
 * "this is the same mutation as before" and return the prior result instead of
 * applying the mutation twice.
 *
 * **Scope of this helper.** As of Phase 06 the key is pure in-memory: it is **not**
 * persisted to [SubmitOutbox] / Room. Forks that need durable idempotency across
 * process restarts (e.g. retries that survive a crash) must layer their own storage
 * — the Room migration to v11 to add an `idempotency_key` column is tracked in the
 * Phase 06 follow-up section of `docs/claude/retry-policy.md`.
 *
 * The implementation backs on Kotlin 2.x [Uuid.random] (currently
 * `@OptIn(ExperimentalUuidApi::class)`), which produces RFC 4122 v4 UUIDs across all
 * KMP targets. The output string is the lowercase canonical form, e.g.
 * `"5d2f9a3b-1c4e-4a8d-9f6b-2e0c5d7a1f3e"`.
 */
object IdempotencyKey {

    /**
     * Generate a fresh, cryptographically-random idempotency key.
     *
     * @return Canonical-form UUIDv4 string suitable for an `Idempotency-Key` HTTP header
     *   or equivalent transport-level dedup channel.
     */
    @OptIn(ExperimentalUuidApi::class)
    fun generate(): String = Uuid.random().toString()
}
