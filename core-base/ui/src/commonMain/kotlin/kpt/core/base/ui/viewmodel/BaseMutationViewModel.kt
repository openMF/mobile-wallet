/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.ui.viewmodel

/**
 * Deprecated alias for [BaseSubmitMutationViewModel].
 *
 * Renamed for clarity when [BaseDraftMutationViewModel] was introduced as its offline-resilient
 * sibling — both extend the same MVI base, but each wraps a different submit handler:
 *
 * | Class | Wraps | When to use |
 * |---|---|---|
 * | [BaseSubmitMutationViewModel] | `SubmitHandler<R>` | Fire-and-forget submit. No outbox. |
 * | [BaseDraftMutationViewModel]  | `DraftSubmitHandler<P, R>` | Offline-resilient. Persists drafts; auto-retries. |
 *
 * Existing call sites keep working — [typealias][typealias] is a fully transparent rename.
 * Update at your own pace; CI does not break.
 */
@Deprecated(
    message = "Renamed to BaseSubmitMutationViewModel for clarity. " +
        "Use BaseDraftMutationViewModel for offline-resilient submits.",
    replaceWith = ReplaceWith(
        "BaseSubmitMutationViewModel<T, R>",
        "kpt.core.base.ui.viewmodel.BaseSubmitMutationViewModel",
    ),
    level = DeprecationLevel.WARNING,
)
typealias BaseMutationViewModel<T, R> = BaseSubmitMutationViewModel<T, R>
