/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.base.ui.paging

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember

/**
 * Returns a [State] that flips to `true` when the user has scrolled within
 * [threshold] items of the end of the list AND more pages are available AND
 * a load is not already in flight.
 *
 * Reads `listState.layoutInfo.totalItemsCount` directly. This avoids the closure-capture
 * footgun of writing the trigger inline with a stale `coins.size` parameter — a
 * `derivedStateOf` block created in `remember{}` captures the FIRST emission's list
 * reference and never sees grown sizes from subsequent recompositions, causing
 * load-more to fire repeatedly until pagination exhausts. (See PLAN-fw-260504-crypto
 * G1 for the bug this replaces.)
 *
 * Typical wiring:
 * ```
 * val shouldLoadMore by rememberLoadMoreTrigger(listState, hasMore, isLoadingMore)
 * LaunchedEffect(shouldLoadMore) { if (shouldLoadMore) viewModel.onLoadMore() }
 * ```
 *
 * For the canonical case use [PagingScreenContent] with the `lazyContent` slot — it
 * wires this trigger automatically.
 *
 * @param listState The list whose scroll position drives the trigger.
 * @param hasMore Whether more pages are available (typically from `PagingScreenStream.hasMore`).
 * @param isLoadingMore Whether a load is currently in flight (typically from `PagingScreenStream.isLoadingMore`).
 * @param threshold How many items before the end to fire the trigger. Default 5.
 */
@Composable
fun rememberLoadMoreTrigger(
    listState: LazyListState,
    hasMore: Boolean,
    isLoadingMore: Boolean,
    threshold: Int = 5,
): State<Boolean> = remember(listState, threshold) {
    derivedStateOf {
        val info = listState.layoutInfo
        val lastVisible = info.visibleItemsInfo.lastOrNull()?.index ?: 0
        // hasMore + isLoadingMore are observed from the enclosing composition each
        // time this lambda runs, so the trigger reacts to their changes.
        lastVisible >= info.totalItemsCount - threshold && hasMore && !isLoadingMore
    }
}
