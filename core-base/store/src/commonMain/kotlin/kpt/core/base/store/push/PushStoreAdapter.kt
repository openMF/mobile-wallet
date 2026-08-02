/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.store.push

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.mobilenativefoundation.store.core5.ExperimentalStoreApi
import org.mobilenativefoundation.store.store5.MutableStore
import org.mobilenativefoundation.store.store5.StoreWriteRequest

/**
 * Bridges a transport-side [Flow] of [PushEvent]s into a Store5 [MutableStore], applying the
 * configured [PushMergeStrategy] per `Update` event.
 *
 * The adapter is transport-agnostic: callers wrap their WebSocket / SSE / FCM-data
 * channel as a `Flow<PushEvent<K, V>>` and hand it to the adapter at construction. Call
 * [start] to begin collecting; the returned [Job] cancels the collection.
 *
 * **Event handling**
 *
 * | Event   | Behaviour                                                                   |
 * |---------|-----------------------------------------------------------------------------|
 * | Insert  | `store.write(StoreWriteRequest.of(key, value))`                             |
 * | Update  | Per [PushMergeStrategy] — see below                                         |
 * | Delete  | `store.clear(key)`                                                          |
 *
 * **Merge strategies**
 *
 * - [PushMergeStrategy.Replace] (default) — `Update` is treated identically to `Insert`:
 *   a single write of the incoming value. No prior-value read needed.
 * - [PushMergeStrategy.Merge] — the adapter cannot synchronously read the current value
 *   from a Store5 [MutableStore] (the read API is a Flow). To keep the adapter pure,
 *   merge resolution is performed by the caller-supplied [readCurrent] lambda. If
 *   `readCurrent` is null (the default) **and** the strategy is `Merge`, the adapter
 *   falls back to a `Replace`-style write, because there is no safe prior value to merge
 *   into. Consumers wanting true merge semantics provide a `readCurrent` block that
 *   collects one item from `store.stream(...)` or reads from a SoT-side projection.
 *
 * Errors thrown during collection or store writes propagate to the adapter's [scope] —
 * pair the adapter scope with a `SupervisorJob` if you want transport hiccups to be
 * non-fatal to the rest of the parent.
 *
 * @param K Store key type.
 * @param V Store value type.
 * @param store Target Store5 mutable store that owns the local cache + source-of-truth.
 * @param events Hot or cold flow of [PushEvent]s from the transport layer.
 * @param mergeStrategy How `Update` events should combine with the local value.
 * @param scope Coroutine scope that drives event collection; cancel to stop the adapter.
 * @param readCurrent Optional suspending lookup of the current local value; required if
 *   [mergeStrategy] is [PushMergeStrategy.Merge].
 */
@OptIn(ExperimentalStoreApi::class)
class PushStoreAdapter<K : Any, V : Any>(
    private val store: MutableStore<K, V>,
    private val events: Flow<PushEvent<K, V>>,
    private val mergeStrategy: PushMergeStrategy<V> = PushMergeStrategy.Replace,
    private val scope: CoroutineScope,
    private val readCurrent: (suspend (K) -> V?)? = null,
) {

    /**
     * Begin collecting [events] and dispatching them to [store]. Returns the [Job] so
     * callers can cancel the adapter independently of the [scope] lifecycle.
     */
    fun start(): Job = scope.launch {
        events.collect { event -> dispatch(event) }
    }

    /**
     * Apply a single [event] to the store. Exposed for fine-grained testing and for
     * callers that want to plug their own collection loop in front of the adapter.
     */
    suspend fun dispatch(event: PushEvent<K, V>) {
        when (event) {
            is PushEvent.Insert -> writeValue(event.key, event.value)
            is PushEvent.Update -> applyUpdate(event.key, event.value)
            is PushEvent.Delete -> store.clear(event.key)
        }
    }

    private suspend fun applyUpdate(key: K, incoming: V) {
        val strategy = mergeStrategy
        if (strategy is PushMergeStrategy.Merge<V>) {
            val current = readCurrent?.invoke(key)
            val merged = if (current != null) strategy.merger(current, incoming) else incoming
            writeValue(key, merged)
        } else {
            // PushMergeStrategy.Replace (or fallback when no readCurrent was provided).
            writeValue(key, incoming)
        }
    }

    private suspend fun writeValue(key: K, value: V) {
        store.write(StoreWriteRequest.of<K, V, Any>(key = key, value = value))
    }
}
