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

/**
 * How a [PushEvent.Update] should be combined with the existing local value held by the
 * [org.mobilenativefoundation.store.store5.MutableStore].
 *
 * - [Replace] — overwrite the local value with the incoming value (the default; correct
 *   when the server push is a fully-formed entity).
 * - [Merge] — invoke a caller-supplied merger to combine the existing and incoming values
 *   (correct when the server push is a partial / delta payload — e.g. only the changed
 *   columns — and the consumer needs to preserve untouched fields locally).
 *
 * `Replace` does not need access to the prior value, so [PushStoreAdapter] takes the fast
 * write path (`store.write`). `Merge` requires reading the current value first; consult
 * the adapter docs for the read-modify-write contract.
 *
 * @param V Store value type. The interface is declared `out V` so the singleton
 *   [Replace] (with `Nothing` payload) is assignable to any `PushMergeStrategy<V>`.
 */
sealed interface PushMergeStrategy<out V> {

    /** Overwrite the local value with the incoming value. */
    data object Replace : PushMergeStrategy<Nothing>

    /**
     * Combine the existing local value with the incoming push value via [merger].
     *
     * @param merger Pure function `(existing, incoming) -> merged`. Must be deterministic
     *   and side-effect free — it may be re-invoked under contention.
     */
    data class Merge<V>(val merger: (existing: V, incoming: V) -> V) : PushMergeStrategy<V>
}
