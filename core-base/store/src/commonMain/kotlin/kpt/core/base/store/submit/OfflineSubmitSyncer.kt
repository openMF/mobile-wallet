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

import io.github.mobilebytelabs.kmptoolkit.networkmonitor.NetworkStatus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kpt.core.base.observability.CrashReporter
import kpt.core.base.observability.CrashSeverity

/**
 * Retries all PENDING outbox entries whenever the device regains connectivity.
 *
 * Wire this once in the app's DI scope (e.g. `AppViewModel`) to ensure that drafts saved
 * during offline submissions are automatically retried when the network comes back.
 *
 * ```kotlin
 * val syncer = coroutineScope.offlineSubmitSyncer(
 *     outbox             = roomSubmitOutbox,
 *     networkStatusFlow  = networkMonitor.networkStatus,
 *     submitBlock        = { payload -> repository.submitLoanApplication(payload) },
 *     retryOnStatus      = RetryOnNetworkStatus.OnlineOnly,   // default
 * )
 * syncer.start()
 * ```
 *
 * Pass `retryOnStatus = RetryOnNetworkStatus.OnlineOrCaptivePortal` for apps whose API is
 * reachable behind a captive portal (e.g. corporate WiFi after sign-in) — retries will
 * resume immediately after portal auth instead of waiting for the portal to clear.
 *
 * @param P Serializable payload type that the outbox holds.
 * @param R Result type returned by the server on success (ignored — fire-and-forget).
 *
 * **Optional crash reporter:** Pass a [CrashReporter] (typically Koin-injected) and the syncer
 * will record retry failures as non-fatal exceptions — useful in production where a single
 * persistent retry failure can indicate a server-side regression. When null (the default),
 * failures are silently logged to the outbox only. The optional shape preserves backwards
 * compatibility — existing call-sites that don't pass a reporter remain unchanged.
 */
class OfflineSubmitSyncer<P, R>(
    private val scope: CoroutineScope,
    private val outbox: SubmitOutbox<P>,
    private val networkStatusFlow: Flow<NetworkStatus>,
    private val submitBlock: suspend (P) -> R,
    private val retryOnStatus: RetryOnNetworkStatus = RetryOnNetworkStatus.OnlineOnly,
    private val crashReporter: CrashReporter? = null,
    /**
     * Per-attempt retry-backoff policy. **Held but not yet applied** — the per-entry
     * `attemptCount` and `nextRetryAt` columns required to schedule backoff between
     * the connectivity-triggered retry cycles are pending a Room schema migration
     * (see `docs/claude/retry-policy.md` for the deferred work). Until then,
     * `retryAll()` continues to fire every PENDING entry once per reconnect with
     * no inter-entry delay — same behavior as before this parameter existed.
     * Defaults to [RetryPolicy] defaults (3 attempts, 1s/2s/4s backoff, ±25% jitter).
     */
    @Suppress("unused")
    private val retryPolicy: RetryPolicy = RetryPolicy(),
) {

    /**
     * Begin watching [networkStatusFlow]. The status is mapped through [retryOnStatus] to
     * a `Boolean` "should retry now?" signal, then edge-triggered so retries fire only when
     * transitioning into a retry-eligible state.
     *
     * Individual entry failures are logged to the outbox as FAILED; they do not abort the
     * batch — remaining entries continue to be retried.
     */
    fun start(): Job = scope.launch {
        networkStatusFlow
            .map { status -> retryOnStatus.shouldRetry(status) }
            .distinctUntilChanged()
            .filter { shouldRetry -> shouldRetry }
            .collect { retryAll() }
    }

    private suspend fun retryAll() {
        val pending = outbox.getAllPending()
        for (entry in pending) {
            outbox.markRetrying(entry.id)
            try {
                submitBlock(entry.payload)
                outbox.markSubmitted(entry.id)
            } catch (e: CancellationException) {
                outbox.markFailed(entry.id, "cancelled")
                throw e
            } catch (e: Exception) {
                outbox.markFailed(entry.id, e.message)
                // Surface retry-failure to the fork's crash-reporter (if wired) so production
                // installs can spot recurring failures without polling the outbox table.
                crashReporter?.recordException(
                    throwable = e,
                    message = "OfflineSubmitSyncer.retryAll: entry=${entry.id} " +
                        "failed during connectivity-triggered retry",
                )
            }
        }
    }
}

/**
 * Creates an [OfflineSubmitSyncer] bound to this [CoroutineScope].
 *
 * ```kotlin
 * val syncer = viewModelScope.offlineSubmitSyncer(
 *     outbox             = roomSubmitOutbox,
 *     networkStatusFlow  = networkMonitor.networkStatus,
 *     submitBlock        = { payload -> api.submit(payload) },
 * )
 * syncer.start()
 * ```
 */
fun <P, R> CoroutineScope.offlineSubmitSyncer(
    outbox: SubmitOutbox<P>,
    networkStatusFlow: Flow<NetworkStatus>,
    submitBlock: suspend (P) -> R,
    retryOnStatus: RetryOnNetworkStatus = RetryOnNetworkStatus.OnlineOnly,
    crashReporter: CrashReporter? = null,
    retryPolicy: RetryPolicy = RetryPolicy(),
): OfflineSubmitSyncer<P, R> = OfflineSubmitSyncer(
    scope = this,
    outbox = outbox,
    networkStatusFlow = networkStatusFlow,
    submitBlock = submitBlock,
    retryOnStatus = retryOnStatus,
    crashReporter = crashReporter,
    retryPolicy = retryPolicy,
)

/**
 * Re-exported for callers that want to set retry-classification severity in their own code.
 * Currently informational only — [OfflineSubmitSyncer] records exceptions at the default
 * severity (no [CrashSeverity] argument needed). Re-exposing the type avoids forcing every
 * call-site to depend on `core-base:observability` directly.
 */
typealias OfflineSubmitSyncerCrashSeverity = CrashSeverity
