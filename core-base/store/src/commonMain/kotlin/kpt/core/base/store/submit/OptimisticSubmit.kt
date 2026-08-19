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
import org.mobilenativefoundation.store.core5.ExperimentalStoreApi
import org.mobilenativefoundation.store.store5.MutableStore

/**
 * Optimistic-UI submission orchestration: mutate the local store immediately,
 * then perform the network call, and roll back if the network call fails.
 *
 * Typical use: a like-button, edit-in-place, or single-field update where the
 * UI should reflect the user's action instantly without waiting for a server
 * round-trip. Rollback restores the previous local state on any failure other
 * than [CancellationException] — which is re-thrown so cooperative cancellation
 * (e.g. ViewModel scope tear-down) still works as expected.
 *
 * ```kotlin
 * private val submit = viewModelScope.submitHandler<Item>()
 *
 * fun onLike(item: Item) = viewModelScope.launch {
 *     submit.optimisticSubmit(
 *         payload  = item,
 *         apply    = { p -> localStore.update(p.copy(liked = true)) },
 *         network  = { p -> api.like(p.id) },
 *         rollback = { p, _ -> localStore.update(p.copy(liked = false)) },
 *     )
 * }
 * ```
 *
 * The [SubmitHandler] receiver is unused at runtime — the function is attached
 * as an extension to make discovery via IDE auto-complete obvious for callers
 * already holding a [SubmitHandler]. Callers without a handler can wrap an
 * inline lambda; see [MutableStore.optimisticSubmit] for the typed [MutableStore]
 * variant.
 *
 * @param P Payload type carried through the lifecycle (read by [apply], [network],
 *   and [rollback]).
 * @param R Result type returned by the [network] call on success.
 * @param payload The payload to apply optimistically.
 * @param apply Suspend lambda that mutates the local store to reflect the
 *   optimistic outcome. Called BEFORE the network call.
 * @param network Suspend lambda that performs the actual server call. Returns
 *   the server's response on success; throws on failure.
 * @param rollback Suspend lambda that reverses [apply]. Called only on
 *   non-cancellation failures from [network] or [apply].
 * @return The result returned by [network] on success.
 * @throws CancellationException re-thrown without invoking [rollback] so
 *   scope cancellation does not appear as a user-facing failure.
 * @throws Throwable any other failure from [apply] or [network] — [rollback]
 *   is invoked before the exception is re-thrown.
 */
@Suppress("UnusedReceiverParameter")
suspend fun <P, R> SubmitHandler<R>.optimisticSubmit(
    payload: P,
    apply: suspend (P) -> Unit,
    network: suspend (P) -> R,
    rollback: suspend (P, Throwable) -> Unit,
): R = optimisticSubmitImpl(payload, apply, network, rollback)

/**
 * [MutableStore] variant of [optimisticSubmit] — identical semantics, just
 * a different receiver for IDE discoverability when the caller is holding
 * a [MutableStore] rather than a [SubmitHandler].
 *
 * The receiver is unused at runtime — concrete persistence happens inside
 * the caller-supplied [apply]/[rollback] lambdas (Store5's [MutableStore]
 * does not expose an optimistic-write API directly; conflict resolution is
 * forked-store responsibility per the [kpt.core.base.store.infra.ConflictStrategy]
 * contract).
 */
@OptIn(ExperimentalStoreApi::class)
@Suppress("UnusedReceiverParameter")
suspend fun <Key : Any, Value : Any, P, R> MutableStore<Key, Value>.optimisticSubmit(
    payload: P,
    apply: suspend (P) -> Unit,
    network: suspend (P) -> R,
    rollback: suspend (P, Throwable) -> Unit,
): R = optimisticSubmitImpl(payload, apply, network, rollback)

/**
 * Receiver-less core. Both extension forms delegate here so the contract is
 * defined once and the two receivers stay byte-identical.
 */
private suspend fun <P, R> optimisticSubmitImpl(
    payload: P,
    apply: suspend (P) -> Unit,
    network: suspend (P) -> R,
    rollback: suspend (P, Throwable) -> Unit,
): R {
    apply(payload)
    return try {
        network(payload)
    } catch (t: CancellationException) {
        // Cooperative cancellation — do NOT roll back; preserve the optimistic
        // apply so the next collector sees consistent local state.
        throw t
    } catch (t: Throwable) {
        rollback(payload, t)
        throw t
    }
}
