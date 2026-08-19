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

/**
 * Semantics for [BatchSubmitHandler.submitBatch] when one or more payloads fail.
 *
 * - [AllOrNothing] — fail-fast: the first failure aborts the remaining submissions and
 *   the resulting [BatchResult] records only the payloads attempted so far. Pair this
 *   mode with a server-side transaction (or a compensating-action protocol) so callers
 *   can treat a partial success as "nothing happened" from the user's perspective.
 * - [Independent] — best-effort: every payload is attempted regardless of prior
 *   failures; the [BatchResult] records every per-payload exception by index so callers
 *   can retry only the failed subset.
 *
 * No mode performs any retries — pair with [RetryPolicy] / [DraftSubmitHandler] /
 * [OfflineSubmitSyncer] for retry semantics.
 */
sealed interface BatchSubmitMode {

    /** Fail-fast: stop on the first error. */
    data object AllOrNothing : BatchSubmitMode

    /** Best-effort: attempt every payload regardless of prior failures. */
    data object Independent : BatchSubmitMode
}
