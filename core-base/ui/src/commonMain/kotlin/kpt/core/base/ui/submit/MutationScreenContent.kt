/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.ui.submit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kpt.core.base.store.freshness.FreshnessSignal
import kpt.core.base.store.screen.ScreenDataStream
import kpt.core.base.store.screen.ScreenState
import kpt.core.base.store.submit.DraftResumeState
import kpt.core.base.store.submit.MutationUiState
import kpt.core.base.store.submit.SubmitState
import kpt.core.base.ui.screen.ScreenContent

/**
 * Convenience composable for edit/mutation screens.
 *
 * Combines [ScreenContent] + [SubmitProgressOverlay] + [SubmitResultHandler] into a single
 * call. The overlay appears automatically while submitting; result callbacks fire on terminal
 * [SubmitState] transitions.
 *
 * ```kotlin
 * MutationScreenContent(
 *     screenState = uiState.screen,
 *     submitState = uiState.submit,
 *     onRetry = viewModel::onRetry,
 *     onSubmitted = { navigateBack() },
 *     onFailed = { _, category -> showError(category) },
 * ) { data, _ ->
 *     EditForm(data = data, enabled = uiState.canInteract, onSave = viewModel::onSave)
 * }
 * ```
 */
@Composable
fun <T, R> MutationScreenContent(
    screenState: ScreenState<T>,
    submitState: SubmitState<R>,
    onRetry: () -> Unit,
    onSubmitted: (result: R) -> Unit,
    modifier: Modifier = Modifier,
    refreshingIndicator: (@Composable () -> Unit)? = null,
    onFailed: ((error: Throwable, category: kpt.core.base.store.error.ErrorCategory) -> Unit)? = null,
    submitStatus: (@Composable (submitState: SubmitState<R>) -> Unit)? = null,
    content: @Composable (data: T, freshnessSignal: FreshnessSignal) -> Unit,
) {
    SubmitResultHandler(
        state = submitState,
        onSubmitted = onSubmitted,
        onFailed = onFailed,
    )
    Box(modifier = modifier.fillMaxSize()) {
        if (submitStatus != null) {
            // Form-friendly layout: the read/edit content fills the space, a PERSISTENT inline
            // submit-status strip (Saving / Saved / Failed-with-retry) is pinned below it. Use
            // this for input screens that surface submit feedback in-place rather than as a
            // transient snackbar. The scrim overlay still layers on top while Submitting.
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f)) {
                    ScreenContent(
                        state = screenState,
                        onRetry = onRetry,
                        modifier = Modifier.fillMaxSize(),
                        refreshingIndicator = refreshingIndicator,
                        content = content,
                    )
                }
                submitStatus(submitState)
            }
        } else {
            ScreenContent(
                state = screenState,
                onRetry = onRetry,
                modifier = Modifier.fillMaxSize(),
                refreshingIndicator = refreshingIndicator,
                content = content,
            )
        }
        SubmitProgressOverlay(state = submitState)
    }
}

// STORE5-COMPLETENESS: mutation-stream — stream overload
/**
 * Direct [ScreenDataStream] (Store5 read) overload of [MutationScreenContent]. Mirrors
 * [ScreenContent]'s stream overload: the ViewModel exposes the read stream (built in the
 * repository) and Compose collects `stream.state` lifecycle-aware here, wiring retry to
 * `stream.retry()`. The write side is still driven by the [submitState] the ViewModel owns.
 *
 * This collapses the read-side ViewModel boilerplate — no `StateFlow` conversion, no manual
 * `ScreenState` bookkeeping — while keeping the SAME `(data, freshness)` content lambda, so a
 * mutation screen can bind a live Store5 stream OR a plain [ScreenState] value (the base
 * `screenState:` overload) with zero call-site divergence (the D2 dual-input contract).
 *
 * ```kotlin
 * MutationScreenContent(
 *     stream = viewModel.editStream,          // repository-built ScreenDataStream
 *     submitState = uiState.submit,
 *     onSubmitted = { navigateBack() },
 * ) { data, _ ->
 *     EditForm(data = data, onSave = viewModel::onSave)
 * }
 * ```
 */
@Composable
fun <T, R> MutationScreenContent(
    stream: ScreenDataStream<T>,
    submitState: SubmitState<R>,
    onSubmitted: (result: R) -> Unit,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit = stream::retry,
    refreshingIndicator: (@Composable () -> Unit)? = null,
    onFailed: ((error: Throwable, category: kpt.core.base.store.error.ErrorCategory) -> Unit)? = null,
    submitStatus: (@Composable (submitState: SubmitState<R>) -> Unit)? = null,
    content: @Composable (data: T, freshnessSignal: FreshnessSignal) -> Unit,
) {
    val screenState by stream.state.collectAsStateWithLifecycle(ScreenState.Loading)
    MutationScreenContent(
        screenState = screenState,
        submitState = submitState,
        onRetry = onRetry,
        onSubmitted = onSubmitted,
        modifier = modifier,
        refreshingIndicator = refreshingIndicator,
        onFailed = onFailed,
        submitStatus = submitStatus,
        content = content,
    )
}

/**
 * [MutationUiState]-typed overload. Reduces boilerplate when the ViewModel exposes a
 * single [MutationUiState] property.
 *
 * ```kotlin
 * MutationScreenContent(
 *     state = uiState.mutation,
 *     onRetry = viewModel::onRetry,
 *     onSubmitted = { navigateBack() },
 * ) { data, _ ->
 *     EditForm(data = data, enabled = uiState.mutation.canInteract, onSave = viewModel::onSave)
 * }
 * ```
 */
@Composable
fun <T, R> MutationScreenContent(
    state: MutationUiState<T, R>,
    onRetry: () -> Unit,
    onSubmitted: (result: R) -> Unit,
    modifier: Modifier = Modifier,
    refreshingIndicator: (@Composable () -> Unit)? = null,
    onFailed: ((error: Throwable, category: kpt.core.base.store.error.ErrorCategory) -> Unit)? = null,
    submitStatus: (@Composable (submitState: SubmitState<R>) -> Unit)? = null,
    content: @Composable (data: T, freshnessSignal: FreshnessSignal) -> Unit,
) {
    MutationScreenContent(
        screenState = state.screen,
        submitState = state.submit,
        onRetry = onRetry,
        onSubmitted = onSubmitted,
        modifier = modifier,
        refreshingIndicator = refreshingIndicator,
        onFailed = onFailed,
        submitStatus = submitStatus,
        content = content,
    )
}

/**
 * Draft-aware overload of [MutationScreenContent].
 *
 * Shows a [DraftResumeBanner] above the form content when [draftResumeState] is
 * [DraftResumeState.HasDraft]. The banner is omitted when [draftResumeState] is
 * [DraftResumeState.None] (default).
 *
 * ```kotlin
 * MutationScreenContent(
 *     screenState = uiState.screen,
 *     submitState = uiState.submit,
 *     draftResumeState = draftState,
 *     onResumeClick = viewModel::onResumeDraft,
 *     onDiscardClick = viewModel::onDiscardDraft,
 *     onRetry = viewModel::onRetry,
 *     onSubmitted = { navigateBack() },
 * ) { data, _ ->
 *     EditForm(data = data, enabled = uiState.canInteract)
 * }
 * ```
 */
@Composable
fun <T, R> MutationScreenContent(
    screenState: ScreenState<T>,
    submitState: SubmitState<R>,
    onRetry: () -> Unit,
    onSubmitted: (result: R) -> Unit,
    draftResumeState: DraftResumeState<*>,
    onResumeClick: () -> Unit,
    onDiscardClick: () -> Unit,
    modifier: Modifier = Modifier,
    refreshingIndicator: (@Composable () -> Unit)? = null,
    onFailed: ((error: Throwable, category: kpt.core.base.store.error.ErrorCategory) -> Unit)? = null,
    content: @Composable (data: T, freshnessSignal: FreshnessSignal) -> Unit,
) {
    SubmitResultHandler(
        state = submitState,
        onSubmitted = onSubmitted,
        onFailed = onFailed,
    )
    Column(modifier = modifier.fillMaxSize()) {
        DraftResumeBanner(
            state = draftResumeState,
            onResume = onResumeClick,
            onDiscard = onDiscardClick,
        )
        Box(modifier = Modifier.weight(1f)) {
            ScreenContent(
                state = screenState,
                onRetry = onRetry,
                modifier = Modifier.fillMaxSize(),
                refreshingIndicator = refreshingIndicator,
                content = content,
            )
            SubmitProgressOverlay(state = submitState)
        }
    }
}

/**
 * [MutationUiState]-typed draft-aware overload. Combines read state, write state, and
 * draft resume into a single call.
 */
@Composable
fun <T, R> MutationScreenContent(
    state: MutationUiState<T, R>,
    onRetry: () -> Unit,
    onSubmitted: (result: R) -> Unit,
    draftResumeState: DraftResumeState<*>,
    onResumeClick: () -> Unit,
    onDiscardClick: () -> Unit,
    modifier: Modifier = Modifier,
    refreshingIndicator: (@Composable () -> Unit)? = null,
    onFailed: ((error: Throwable, category: kpt.core.base.store.error.ErrorCategory) -> Unit)? = null,
    content: @Composable (data: T, freshnessSignal: FreshnessSignal) -> Unit,
) {
    MutationScreenContent(
        screenState = state.screen,
        submitState = state.submit,
        onRetry = onRetry,
        onSubmitted = onSubmitted,
        draftResumeState = draftResumeState,
        onResumeClick = onResumeClick,
        onDiscardClick = onDiscardClick,
        modifier = modifier,
        refreshingIndicator = refreshingIndicator,
        onFailed = onFailed,
        content = content,
    )
}
