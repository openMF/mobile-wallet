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

/**
 * Policy for reconciling a server-side value with a local-side value when both
 * have diverged — the classic "I edited offline, the server also changed"
 * conflict that surfaces during outbox sync, optimistic-UI rollback, and
 * eventually-consistent reads.
 *
 * Wired into [StoreFactory.createMutableStore] as an optional parameter so
 * forks can pick a default. The strategy itself is plain Kotlin — Store5's
 * [org.mobilenativefoundation.store.store5.Updater] interface does not directly
 * consume a `ConflictStrategy`; instead, the recommended pattern is for the
 * fork's `Updater` block to call `conflictStrategy.resolve(server, client)`
 * and write back the resolved value. The [StoreFactory] parameter is therefore
 * exposed for *future* automated wiring + as a discovery hook (and the
 * forks' Updater code documents which strategy is in use).
 *
 * Four shipping strategies cover the dominant cases — see the per-class KDoc.
 * Forks needing bespoke merge logic should implement their own
 * `ConflictStrategy<O>` directly.
 */
sealed interface ConflictStrategy<O> {

    /**
     * Resolve a conflict between [server] and [client]. The returned value is
     * what should be written to the local source-of-truth (and, for outbox
     * scenarios, what should be retransmitted).
     */
    fun resolve(server: O, client: O): O

    /**
     * Server is the authority: discard the client copy. Use when the server is
     * the canonical source of truth (e.g. balances, account state) and local
     * edits are merely speculative.
     */
    class ServerWins<O> : ConflictStrategy<O> {
        override fun resolve(server: O, client: O): O = server
    }

    /**
     * Client is the authority: discard the server copy. Use when the client
     * has the most recent intent and the server's value is assumed stale
     * (rare — typically only for write-heavy local-first apps).
     */
    class ClientWins<O> : ConflictStrategy<O> {
        override fun resolve(server: O, client: O): O = client
    }

    /**
     * Pick the side with the larger [timestampOf]. Tie goes to the server
     * (defensive — prefer the canonical store on equality so repeated sync
     * cycles converge).
     *
     * @param timestampOf Function returning a comparable timestamp (typically
     *   millis-since-epoch or a monotonic version-id) for a given value.
     */
    class LastWriteWins<O>(
        private val timestampOf: (O) -> Long,
    ) : ConflictStrategy<O> {
        override fun resolve(server: O, client: O): O =
            if (timestampOf(client) > timestampOf(server)) client else server
    }

    /**
     * Caller-supplied [merger] lambda for field-level merge (e.g. union of
     * tags + max of edited_at + server's id). The merge MUST be deterministic
     * — i.e. `merger(s, c) == merger(s, c)` for the same inputs — so repeated
     * sync cycles converge.
     */
    class Merge<O>(
        private val merger: (server: O, client: O) -> O,
    ) : ConflictStrategy<O> {
        override fun resolve(server: O, client: O): O = merger(server, client)
    }
}
