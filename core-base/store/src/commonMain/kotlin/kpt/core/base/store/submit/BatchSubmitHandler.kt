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

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope

/**
 * Orchestrator for submitting a list of payloads via a single per-payload [submitBlock].
 *
 * Modes:
 * - [BatchSubmitMode.AllOrNothing] — abort on first failure; remaining payloads are not
 *   attempted. Use when the consumer treats the batch as a logical transaction.
 * - [BatchSubmitMode.Independent] — attempt every payload regardless of prior failures;
 *   record per-payload outcome by index. Use for "send N independent notes" batches.
 *
 * `submitBatch` is sequential — it does not parallelise per-payload work. Callers that
 * need concurrent dispatch should wrap each call in its own coroutine and join the
 * results manually. Sequential semantics keep AllOrNothing's "stop on first failure"
 * contract simple and predictable.
 *
 * [CancellationException] is **never** caught — cooperative cancellation always
 * propagates so that scope teardown tears down the batch as expected.
 *
 * Idempotency: callers may attach an [idempotencyKey] (defaults to a freshly-generated
 * UUIDv4 via [IdempotencyKey.generate]) and reference it inside [submitBlock] to attach
 * an `Idempotency-Key` header (or equivalent). The handler itself does **not** persist
 * the key — see [IdempotencyKey] for the storage caveat.
 *
 * @param P Per-payload type.
 * @param scope Held for future use by retry/parallelism extensions; unused in the
 *   sequential implementation. Kept on the constructor to avoid an API break later.
 * @param mode Default failure-handling mode; overridable per [submitBatch] call.
 */
class BatchSubmitHandler<P>(
    @Suppress("unused") private val scope: CoroutineScope,
    private val mode: BatchSubmitMode = BatchSubmitMode.AllOrNothing,
) {

    /**
     * Submit each payload in [payloads] via [submitBlock], applying the configured [mode].
     *
     * @param payloads Ordered payloads to submit; the [BatchResult.failures] index refers
     *   to this list's order.
     * @param idempotencyKey Optional key for client-side de-duplication of retries.
     *   Defaults to a freshly-generated UUID — passed to [submitBlock] so the consumer
     *   can attach it to the wire request. Reuse the same key when retrying the same
     *   batch to enable server-side dedup.
     * @param submitBlock Per-payload submission lambda. Receives `(index, payload, key)`.
     *   Throwing from this lambda is the failure signal; the handler categorises it but
     *   never wraps it (the original throwable is preserved in [BatchResult.failures]).
     * @return Aggregate per-batch result; never throws (except [CancellationException]).
     */
    suspend fun submitBatch(
        payloads: List<P>,
        idempotencyKey: String = IdempotencyKey.generate(),
        submitBlock: suspend (index: Int, payload: P, key: String) -> Unit,
    ): BatchResult = when (mode) {
        BatchSubmitMode.AllOrNothing -> submitAllOrNothing(payloads, idempotencyKey, submitBlock)
        BatchSubmitMode.Independent -> submitIndependent(payloads, idempotencyKey, submitBlock)
    }

    private suspend fun submitAllOrNothing(
        payloads: List<P>,
        key: String,
        submitBlock: suspend (Int, P, String) -> Unit,
    ): BatchResult {
        var succeeded = 0
        val failures = mutableListOf<Pair<Int, Throwable>>()
        payloads.forEachIndexed { index, payload ->
            try {
                submitBlock(index, payload, key)
                succeeded++
            } catch (t: CancellationException) {
                throw t
            } catch (t: Throwable) {
                failures += index to t
                return BatchResult(
                    total = payloads.size,
                    succeeded = succeeded,
                    failed = 1,
                    failures = failures.toList(),
                    aborted = true,
                )
            }
        }
        return BatchResult(
            total = payloads.size,
            succeeded = succeeded,
            failed = 0,
            failures = emptyList(),
            aborted = false,
        )
    }

    private suspend fun submitIndependent(
        payloads: List<P>,
        key: String,
        submitBlock: suspend (Int, P, String) -> Unit,
    ): BatchResult {
        var succeeded = 0
        val failures = mutableListOf<Pair<Int, Throwable>>()
        payloads.forEachIndexed { index, payload ->
            try {
                submitBlock(index, payload, key)
                succeeded++
            } catch (t: CancellationException) {
                throw t
            } catch (t: Throwable) {
                failures += index to t
            }
        }
        return BatchResult(
            total = payloads.size,
            succeeded = succeeded,
            failed = failures.size,
            failures = failures.toList(),
            aborted = false,
        )
    }
}

/**
 * Aggregate outcome of [BatchSubmitHandler.submitBatch].
 *
 * @param total Number of payloads passed in.
 * @param succeeded Number of payloads where [BatchSubmitHandler.submitBatch]'s
 *   `submitBlock` returned without throwing.
 * @param failed Number of payloads that threw. Always `0` for the success path of
 *   AllOrNothing, `1` for the abort path, and `[0..total]` for Independent.
 * @param failures Indexed list of `(payloadIndex, throwable)` for each failed payload.
 *   Indices refer to the original `payloads` list passed to `submitBatch`.
 * @param aborted `true` when AllOrNothing returned early on the first failure;
 *   remaining payloads were not attempted.
 */
data class BatchResult(
    val total: Int,
    val succeeded: Int,
    val failed: Int,
    val failures: List<Pair<Int, Throwable>>,
    val aborted: Boolean = false,
) {
    /** True when every payload succeeded. */
    val isFullSuccess: Boolean get() = total == succeeded && failures.isEmpty()

    /** True when zero payloads succeeded (only meaningful for non-empty batches). */
    val isFullFailure: Boolean get() = total > 0 && succeeded == 0
}
