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

import kotlin.math.pow

/**
 * Exponential-backoff-with-jitter retry policy for outbox-style retries.
 *
 * Pure data — no scheduler, no side effects. Consumers compute the next
 * delay via [delayFor] and apply it themselves (e.g. `delay(policy.delayFor(attempt))`).
 *
 * **Schedule:** for attempt `n` (1-indexed), the base delay is
 * `initialDelayMs * multiplier^(n-1)`, capped at [maxDelayMs]. Jitter is applied
 * as `±jitterFraction * base` so collocated clients don't thundering-herd a
 * recovering server. The final delay is clamped to ≥ 0ms.
 *
 * **Defaults:**
 * - [maxAttempts] = 3 — after 3 failures, the caller should mark the entry
 *   FAILED and stop retrying.
 * - [initialDelayMs] = 1s — first retry fires ~1s after the failure.
 * - [multiplier] = 2.0 — doubles each attempt (1s, 2s, 4s, 8s…).
 * - [jitterFraction] = 0.25 — ±25% randomisation.
 * - [maxDelayMs] = 60s — hard ceiling to keep retries snappy.
 *
 * **Wire-in note:** [OfflineSubmitSyncer] holds this policy as a constructor
 * param but the per-attempt backoff is not yet applied — see the syncer's KDoc
 * for the deferred follow-up (needs Room schema migration to store
 * `attemptCount` + `nextRetryAt` per outbox entry).
 *
 * @property maxAttempts Total attempts including the first one. Must be ≥ 1.
 * @property initialDelayMs Delay before attempt 2 (i.e. the first retry). Must be > 0.
 * @property multiplier Exponential growth factor per attempt. Must be ≥ 1.0
 *   (1.0 = constant backoff).
 * @property jitterFraction Symmetric jitter as a fraction of the base delay.
 *   Must be in [0.0, 1.0]. 0.0 = no jitter; 1.0 = ±100%.
 * @property maxDelayMs Maximum single-attempt delay regardless of attempt
 *   number. Caps the exponential growth.
 */
data class RetryPolicy(
    val maxAttempts: Int = 3,
    val initialDelayMs: Long = 1_000L,
    val multiplier: Double = 2.0,
    val jitterFraction: Double = 0.25,
    val maxDelayMs: Long = 60_000L,
) {
    init {
        require(maxAttempts >= 1) { "maxAttempts must be >= 1 (got $maxAttempts)" }
        require(initialDelayMs > 0L) { "initialDelayMs must be > 0 (got $initialDelayMs)" }
        require(multiplier >= 1.0) { "multiplier must be >= 1.0 (got $multiplier)" }
        require(jitterFraction in 0.0..1.0) {
            "jitterFraction must be in [0.0, 1.0] (got $jitterFraction)"
        }
    }

    /**
     * Compute the delay for [attempt] (1-indexed, where attempt 1 is the
     * first retry — attempt 0 returns 0 since the original call doesn't wait).
     *
     * @param attempt Retry attempt number (1, 2, 3…). 0 returns 0L.
     * @param random Seam for the jitter source — tests pass a deterministic
     *   sequence to verify boundary behavior. Defaults to
     *   `kotlin.random.Random.nextDouble()` which is KMP-safe.
     * @return Delay in milliseconds, ≥ 0 and ≤ ([maxDelayMs] * (1 + [jitterFraction])).
     */
    fun delayFor(
        attempt: Int,
        random: () -> Double = { kotlin.random.Random.nextDouble() },
    ): Long {
        if (attempt < 1) return 0L
        val baseMs = (initialDelayMs * multiplier.pow(attempt - 1.0))
            .coerceAtMost(maxDelayMs.toDouble())
        // ±jitterFraction: random() returns [0.0, 1.0); (random()*2 - 1) maps to [-1.0, 1.0).
        val jitter = baseMs * jitterFraction * (random() * 2.0 - 1.0)
        return (baseMs + jitter).toLong().coerceAtLeast(0L)
    }
}
