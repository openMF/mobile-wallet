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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * The UI state emitted by a draft-resume stream.
 *
 * Screens collect this to show a "Resume previous submission?" banner when a PENDING
 * draft exists, or to silently prefill a form field set from a saved draft.
 */
sealed interface DraftResumeState<out P> {

    /** No pending draft — form starts fresh. */
    data object None : DraftResumeState<Nothing>

    /**
     * A PENDING draft is available for resumption.
     *
     * @param entry The full outbox entry, including the deserialized [payload].
     */
    data class HasDraft<out P>(val entry: SubmitOutboxEntry<P>) : DraftResumeState<P>
}

/**
 * Converts a [SubmitOutbox.observePending] flow into a typed [DraftResumeState] flow.
 *
 * Usage in a ViewModel:
 * ```kotlin
 * val draftState: StateFlow<DraftResumeState<LoanPayload>> =
 *     outbox.resumeStateFor("loan_application")
 *         .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DraftResumeState.None)
 * ```
 */
fun <P> SubmitOutbox<P>.resumeStateFor(formKey: String): Flow<DraftResumeState<P>> =
    observePending(formKey).map { entry ->
        if (entry != null) DraftResumeState.HasDraft(entry) else DraftResumeState.None
    }

/**
 * Multi-pending-aware overload. When [uniqueKey] is non-null, observes the PENDING draft for that
 * exact `(formKey, uniqueKey)` pair (one of N concurrent drafts); when null, behaves exactly like
 * the singleton [resumeStateFor] above. Used by `BaseMutationViewModel` (in `MutationMode.Draft`)
 * so every draft form — singleton or multi-pending — exposes a uniform resume stream.
 */
fun <P> SubmitOutbox<P>.resumeStateFor(formKey: String, uniqueKey: String?): Flow<DraftResumeState<P>> =
    (if (uniqueKey == null) observePending(formKey) else observePendingByUniqueKey(formKey, uniqueKey))
        .map { entry -> if (entry != null) DraftResumeState.HasDraft(entry) else DraftResumeState.None }
