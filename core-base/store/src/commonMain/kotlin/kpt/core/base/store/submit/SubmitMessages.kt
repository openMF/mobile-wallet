/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.store.submit

/**
 * Per-mutation copy overrides. Pass to [SubmitHandler.submit] to surface action-specific
 * messages instead of the generic "Saving…" / "Saved" / "Failed" defaults wired at the
 * theme level.
 *
 * The fields are carried through to [SubmitState.Submitting.message],
 * [SubmitState.Submitted.message], and [SubmitState.Failed.message] respectively, and
 * surface in `SubmitProgressOverlay` / `SubmitResultHandler` UI.
 *
 * Example — a loan-creation form:
 * ```kotlin
 * submitHandler.submit(
 *     messages = SubmitMessages(
 *         submitting = "Creating loan…",
 *         submitted = "Loan created",
 *         failed = "Couldn't create loan",
 *     ),
 * ) { repository.createLoan(form) }
 * ```
 *
 * Any field left `null` falls back to the screen-level / theme-level default copy.
 */
data class SubmitMessages(
    val submitting: String? = null,
    val submitted: String? = null,
    val failed: String? = null,
)
