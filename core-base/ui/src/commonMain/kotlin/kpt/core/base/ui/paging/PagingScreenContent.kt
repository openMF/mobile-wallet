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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kpt.core.base.store.freshness.FreshnessSignal
import kpt.core.base.store.paging.PagingScreenStream
import kpt.core.base.store.screen.ScreenState
import kpt.core.base.ui.screen.DefaultEmptyContent
import kpt.core.base.ui.screen.DefaultErrorContent
import kpt.core.base.ui.screen.DefaultLoadingContent
import kpt.core.base.ui.screen.DefaultNoNetworkContent
import kpt.core.base.ui.screen.ScreenContent

/**
 * [ScreenContent] variant for paginated lists — slot-only overload for screens with
 * custom layouts (sticky headers, sectioned lists, etc.).
 *
 * Most screens should prefer the [PagingScreenContent] overload below that owns the
 * `LazyColumn` and footer wiring — it removes the load-more-trigger and
 * footer-installation footguns by construction.
 *
 * Usage:
 * ```
 * PagingScreenContent(
 *     pagingStream = viewModel.pagingStream,
 *     onRetry = viewModel::retry,
 * ) { items, freshness ->
 *     LazyColumn {
 *         items(items) { item -> ItemRow(item) }
 *         item { LoadMoreFooter(pagingStream = viewModel.pagingStream) }
 *     }
 * }
 * ```
 */
@Composable
fun <T : Any> PagingScreenContent(
    pagingStream: PagingScreenStream<T>,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    refreshingIndicator: (@Composable () -> Unit)? = null,
    loading: @Composable () -> Unit = { DefaultLoadingContent() },
    empty: @Composable () -> Unit = { DefaultEmptyContent() },
    noNetwork: @Composable (isCaptivePortal: Boolean) -> Unit = { captive ->
        DefaultNoNetworkContent(onRetry, isCaptivePortal = captive)
    },
    error: @Composable (Throwable) -> Unit = { DefaultErrorContent(it, onRetry) },
    content: @Composable (data: List<T>, freshnessSignal: FreshnessSignal) -> Unit,
) {
    val state by pagingStream.state.collectAsStateWithLifecycle(ScreenState.Loading)

    ScreenContent(
        state = state,
        onRetry = onRetry,
        modifier = modifier,
        refreshingIndicator = refreshingIndicator,
        loading = loading,
        empty = empty,
        noNetwork = noNetwork,
        error = error,
        content = content,
    )
}

/**
 * Recommended [PagingScreenContent] overload — owns the [LazyColumn], [LoadMoreFooter],
 * and load-more trigger. Screens just provide the per-item lazy content.
 *
 * Use this for "infinite scrolling list" screens. It eliminates the two most common
 * paging footguns:
 * 1. **Load-more trigger drift**: the trigger reads `listState.layoutInfo.totalItemsCount`
 *    via [rememberLoadMoreTrigger], so it can't be broken by a stale `items.size` capture.
 * 2. **Forgetting the LoadMoreFooter** or wiring it incorrectly: the footer is installed
 *    automatically as the last item in the LazyColumn.
 *
 * Usage:
 * ```
 * PagingScreenContent(
 *     pagingStream = viewModel.pagingStream,
 *     onRetry = viewModel::onRetry,
 * ) { coins ->
 *     items(coins) { coin -> CoinItem(coin = coin) }
 * }
 * ```
 *
 * @param lazyContent The per-item content rendered inside the LazyColumn.
 * @param listState The LazyListState driving the load-more trigger. Defaults to
 *   `rememberLazyListState()`. Pass your own if you need to scroll programmatically.
 * @param loadMoreThreshold How many items before the end to fire the next-page trigger.
 * @param loadingMessage Footer message during page load.
 * @param endMessage Footer message when all pages loaded.
 */
@Composable
fun <T : Any> PagingScreenContent(
    pagingStream: PagingScreenStream<T>,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    refreshingIndicator: (@Composable () -> Unit)? = null,
    listState: LazyListState = rememberLazyListState(),
    loadMoreThreshold: Int = 5,
    loadingMessage: String = "Loading more...",
    endMessage: String = "You're all caught up",
    loading: @Composable () -> Unit = { DefaultLoadingContent() },
    empty: @Composable () -> Unit = { DefaultEmptyContent() },
    noNetwork: @Composable (isCaptivePortal: Boolean) -> Unit = { captive ->
        DefaultNoNetworkContent(onRetry, isCaptivePortal = captive)
    },
    error: @Composable (Throwable) -> Unit = { DefaultErrorContent(it, onRetry) },
    lazyContent: LazyListScope.(items: List<T>) -> Unit,
) {
    val state by pagingStream.state.collectAsStateWithLifecycle(ScreenState.Loading)
    val isLoadingMore by pagingStream.isLoadingMore.collectAsStateWithLifecycle()
    val hasMore by pagingStream.hasMore.collectAsStateWithLifecycle()
    val loadMoreError by pagingStream.loadMoreError.collectAsStateWithLifecycle()

    val shouldLoadMore by rememberLoadMoreTrigger(
        listState = listState,
        hasMore = hasMore,
        isLoadingMore = isLoadingMore,
        threshold = loadMoreThreshold,
    )
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) pagingStream.loadNextPage()
    }

    // Pull-to-refresh is owned by the project's KptScaffold via KptPullToRefreshState
    // (see core/ui/.../KptPullToRefreshState.kt: rememberKptPullToRefreshState(pagingStream)).
    // PagingScreenContent intentionally does NOT wrap its own PullToRefreshBox — that
    // would conflict with KptScaffold's gesture and indicator.
    ScreenContent(
        state = state,
        onRetry = onRetry,
        modifier = modifier,
        refreshingIndicator = refreshingIndicator,
        loading = loading,
        empty = empty,
        noNetwork = noNetwork,
        error = error,
    ) { items, _ ->
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
        ) {
            lazyContent(items)
            item {
                LoadMoreFooter(
                    isLoadingMore = isLoadingMore,
                    hasMore = hasMore,
                    loadMoreError = loadMoreError,
                    onRetry = pagingStream::loadNextPage,
                    loadingMessage = loadingMessage,
                    endMessage = endMessage,
                )
            }
        }
    }
}
