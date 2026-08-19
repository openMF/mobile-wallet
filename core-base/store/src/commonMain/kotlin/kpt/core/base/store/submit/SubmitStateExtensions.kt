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

import kpt.core.base.store.error.ErrorCategory

/**
 * Extension properties for [SubmitState]. Mirror the style of [ScreenStateExtensions].
 *
 * Prefer these over `is` checks so UI code reads declaratively:
 * ```kotlin
 * Button(enabled = !submitState.isSubmitting) { … }
 * ```
 */

/** True while the API call is in-flight. Use to disable the submit button. */
val <R> SubmitState<R>.isSubmitting: Boolean
    get() = this is SubmitState.Submitting

/** True when in the initial or post-reset state. */
val <R> SubmitState<R>.isIdle: Boolean
    get() = this is SubmitState.Idle

/** True when the last submission completed successfully. */
val <R> SubmitState<R>.isSubmitted: Boolean
    get() = this is SubmitState.Submitted

/** True when the last submission failed. [SubmitHandler.retry] is available. */
val <R> SubmitState<R>.isFailed: Boolean
    get() = this is SubmitState.Failed

/** Returns the result payload for [SubmitState.Submitted], or null for all other states. */
val <R> SubmitState<R>.resultOrNull: R?
    get() = (this as? SubmitState.Submitted)?.result

/** Returns the error for [SubmitState.Failed], or null for all other states. */
val <R> SubmitState<R>.errorOrNull: Throwable?
    get() = (this as? SubmitState.Failed)?.error

/** Returns the [ErrorCategory] for [SubmitState.Failed], or null for all other states. */
val <R> SubmitState<R>.categoryOrNull: ErrorCategory?
    get() = (this as? SubmitState.Failed)?.category
