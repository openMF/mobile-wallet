/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.store.combine

import kpt.core.base.store.screen.ScreenState
import kpt.core.base.store.submit.SubmitState

/**
 * Snapshot pairing the read-side [ScreenState] with the write-side [SubmitState] —
 * the single value a screen needs to render every read-write cycle (form preload,
 * submission progress, post-submit outcome, offline outbox indicator).
 *
 * @param R the read-side payload type (typically a domain DTO or list).
 * @param W the write-side payload type — what the user is submitting.
 * @param read latest read-state from the underlying [ScreenDataStream] / [asLoadOnceStream].
 * @param mutation latest write-state from the underlying [SubmitHandler] /
 *   [DraftSubmitHandler] (or a no-op stub of [SubmitState.Idle] while no submission has fired).
 * @param outboxPending number of mutations waiting to be retried by the offline outbox.
 *   `0` when no outbox is wired or the queue is empty. Drives a small "x pending" badge
 *   on the screen header (consumer renders or ignores at will).
 * @param isSyncing `true` while the offline syncer is actively draining the outbox.
 *   Independent of [SubmitState.Submitting] — the latter is for the user's current
 *   in-flight submit, the former is for the background drain.
 */
data class CombinedState<R, W>(
    val read: ScreenState<R>,
    val mutation: SubmitState<W>,
    val outboxPending: Int = 0,
    val isSyncing: Boolean = false,
)
