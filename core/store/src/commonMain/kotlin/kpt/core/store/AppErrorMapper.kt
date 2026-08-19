/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.store

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kpt.core.base.store.error.ErrorCategory
import kpt.core.base.store.error.categorize
import kpt.core.store.generated.resources.Res
import kpt.core.store.generated.resources.error_category_auth
import kpt.core.store.generated.resources.error_category_generic
import kpt.core.store.generated.resources.error_category_network
import kpt.core.store.generated.resources.error_category_ratelimit
import kpt.core.store.generated.resources.error_category_server
import org.jetbrains.compose.resources.stringResource

/**
 * Application-level error → user-facing message mapper.
 *
 * Two flavours:
 *  - [mapErrorToUserMessage] — non-Composable, hardcoded English fallback. Used when the
 *    consumer is outside a composition (e.g. logging, analytics, deep-stack categorisation).
 *  - [rememberAppErrorMessageFor] — Composable factory returning a `(Throwable) -> String`
 *    that resolves copy through `stringResource(...)` for full i18n. Wired into
 *    [appScreenStateDefaults] for the [ScreenStateError.messageFor] hook.
 *
 * Forks should extend the `when (categorize(...))` in [mapErrorToUserMessage] (and the
 * matching `categoryCopyFor` in [rememberAppErrorMessageFor]) with their own domain-error
 * branches before falling back to [categorize].
 */
fun mapErrorToUserMessage(error: Throwable): String = when (val cat = categorize(error)) {
    ErrorCategory.Network -> "Can't reach the server. Check your connection and try again."
    ErrorCategory.Timeout.Connect -> "Connection timed out. Check your connection and try again."
    ErrorCategory.Timeout.Read -> "Server took too long to respond. Please try again shortly."
    ErrorCategory.Auth -> "Your session expired. Please sign in again."
    ErrorCategory.RateLimit -> "Too many requests. Please wait a moment and try again."
    ErrorCategory.QuotaExceeded -> "You've reached your quota. Upgrade or wait to continue."
    is ErrorCategory.Server -> "Our servers are having a moment. Please try again shortly."
    is ErrorCategory.ClientError -> error.message ?: "Request failed (${cat.httpCode})."
    ErrorCategory.Generic -> error.message ?: "Something went wrong."
    // TODO(fork): add branches above for app-specific exception types — e.g.
    //   error is InsufficientFundsException -> "Not enough balance for this transfer."
}

/**
 * Composable factory that resolves per-category copy via `stringResource(...)`, returning
 * a pure-Kotlin `(Throwable) -> String` lambda safe to pass through to
 * [kpt.core.base.ui.screen.ScreenStateError.messageFor] (which is invoked from inside
 * composition where these strings are already memoised).
 *
 * Reuses [categorize] to bucket the error, then picks the localized string.
 */
@Composable
fun rememberAppErrorMessageFor(): (Throwable) -> String {
    val networkCopy = stringResource(Res.string.error_category_network)
    val authCopy = stringResource(Res.string.error_category_auth)
    val rateLimitCopy = stringResource(Res.string.error_category_ratelimit)
    val serverCopy = stringResource(Res.string.error_category_server)
    val genericCopy = stringResource(Res.string.error_category_generic)
    return remember(networkCopy, authCopy, rateLimitCopy, serverCopy, genericCopy) {
        { error: Throwable ->
            when (categorize(error)) {
                ErrorCategory.Network,
                ErrorCategory.Timeout.Connect,
                ErrorCategory.Timeout.Read,
                -> networkCopy
                ErrorCategory.Auth -> authCopy
                ErrorCategory.RateLimit, ErrorCategory.QuotaExceeded -> rateLimitCopy
                is ErrorCategory.Server -> serverCopy
                is ErrorCategory.ClientError -> error.message ?: genericCopy
                ErrorCategory.Generic -> error.message ?: genericCopy
            }
        }
    }
}
