/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.store.combine.internal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kpt.core.base.store.combine.CombinedState
import kpt.core.base.store.combine.ScreenWithMutationStream
import kpt.core.base.store.screen.ScreenState
import kpt.core.base.store.submit.SubmitHandler
import kpt.core.base.store.submit.SubmitState

/**
 * Default [ScreenWithMutationStream] implementation — fuses a read-side
 * `Flow<ScreenState<R>>` with a [SubmitHandler] write pipeline plus optional
 * outbox-count / sync-status flows into a single hot [StateFlow] of
 * [CombinedState].
 *
 * Internal — consumers build instances via `StoreFactory.createScreenWithMutation`,
 * not by referencing this class directly.
 *
 * @param readStream the read-side state flow, typically `screenDataStream.state`.
 * @param submitHandler the write-side handler typed at `<W>` so [CombinedState.mutation]
 *   propagates the same payload type the screen submits.
 * @param submitBlock the actual API call wrapped by [submit] — `suspend (W) -> W`.
 *   Returning the payload keeps types consistent with `SubmitHandler<W>`; callers
 *   that don't need to surface the saved value can simply `return payload` (the
 *   screen typically observes the refreshed value via the read side anyway). Throwing
 *   transitions [submitHandler]'s state to `Failed`.
 * @param pendingCountFlow optional flow of pending-outbox count (defaults to a
 *   constant `0` when the consumer has no outbox wired). Callers can map
 *   `outbox.observeAllByFormKey(formKey).map { it.size }` for a live count.
 * @param syncingFlow optional flow indicating background-sync activity (defaults
 *   to a constant `false`).
 * @param scope coroutine scope hosting the combine + stateIn pipeline.
 * @param onRefresh invoked from [refresh] — wired by the factory to the
 *   ScreenDataStream's own refresh trigger.
 */
internal class ScreenWithMutationStreamImpl<R, W>(
    readStream: Flow<ScreenState<R>>,
    private val submitHandler: SubmitHandler<W>,
    private val submitBlock: suspend (W) -> W,
    pendingCountFlow: Flow<Int>,
    syncingFlow: Flow<Boolean>,
    scope: CoroutineScope,
    private val onRefresh: () -> Unit,
) : ScreenWithMutationStream<R, W> {

    override val state: StateFlow<CombinedState<R, W>> = combine(
        readStream,
        submitHandler.state,
        pendingCountFlow,
        syncingFlow,
    ) { read, mutation, pending, syncing ->
        CombinedState(
            read = read,
            mutation = mutation,
            outboxPending = pending,
            isSyncing = syncing,
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        initialValue = CombinedState(
            read = ScreenState.Loading,
            mutation = SubmitState.Idle,
            outboxPending = 0,
            isSyncing = false,
        ),
    )

    override fun refresh() {
        onRefresh()
    }

    override fun submit(payload: W) {
        // Bridge SubmitHandler's no-arg block API to ScreenWithMutationStream's
        // payload-receiving API by capturing the payload into the closure.
        submitHandler.submit { submitBlock(payload) }
    }

    override fun cancelPendingSubmit() {
        submitHandler.reset()
    }

    private companion object {
        // Match the WhileSubscribed window used by ScreenDataStream consumers
        // (5s grace period for configuration-change-driven re-subscription).
        private const val STOP_TIMEOUT_MS = 5_000L
    }
}
