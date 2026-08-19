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

import kpt.core.base.store.error.OfflineException
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Verifies the [loadMoreFooterCopy] pure function — copy + icon selection driven
 * by error category.
 *
 * PLAN-fw-260504-screen-polish-pull-refresh-and-freshness-tracking §3.
 */
class LoadMoreFooterCopyTest {

    @Test
    fun offlineException_showsNoInternet() {
        val copy = loadMoreFooterCopy(OfflineException())
        assertEquals("No internet", copy.label)
        assertEquals("Tap to retry", copy.retryText)
    }

    @Test
    fun networkShapedIOException_showsNoInternet() {
        // Throwable whose simpleName contains "IOException" → categorize as Network.
        val copy = loadMoreFooterCopy(FakeIOException("connection lost"))
        assertEquals("No internet", copy.label)
    }

    @Test
    fun authError_showsSessionExpired() {
        val copy = loadMoreFooterCopy(RuntimeException("HTTP 401: Unauthorized"))
        assertEquals("Session expired", copy.label)
        assertEquals("Sign in again", copy.retryText)
    }

    @Test
    fun serverError_showsServerUnavailable() {
        val copy = loadMoreFooterCopy(RuntimeException("HTTP 503: Service Unavailable"))
        assertEquals("Server unavailable", copy.label)
        assertEquals("Tap to retry", copy.retryText)
    }

    @Test
    fun unknownError_showsGenericFailedToLoadMore() {
        val copy = loadMoreFooterCopy(IllegalStateException("something weird"))
        assertEquals("Failed to load more", copy.label)
        assertEquals("Tap to retry", copy.retryText)
    }

    /** Throwable whose simpleName contains "IOException" — triggers categorize Network heuristic. */
    private class FakeIOException(message: String) : Exception(message)
}
