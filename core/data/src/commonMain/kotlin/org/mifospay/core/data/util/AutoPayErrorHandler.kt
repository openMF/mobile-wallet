/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.util

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import org.mifospay.core.common.DataState
import org.mifospay.core.common.ScreenState

object AutoPayErrorHandler {

    sealed class AutoPayError(
        open val message: String,
        open val code: String? = null,
    ) {
        data class NetworkError(
            override val message: String,
            override val code: String? = null,
        ) : AutoPayError(message, code)

        data class ValidationError(
            override val message: String,
            override val code: String? = null,
        ) : AutoPayError(message, code)

        data class ServerError(
            override val message: String,
            override val code: String? = null,
        ) : AutoPayError(message, code)

        data class AuthenticationError(
            override val message: String,
            override val code: String? = null,
        ) : AutoPayError(message, code)

        data class AuthorizationError(
            override val message: String,
            override val code: String? = null,
        ) : AutoPayError(message, code)

        data class NotFoundError(
            override val message: String,
            override val code: String? = null,
        ) : AutoPayError(message, code)

        data class ConflictError(
            override val message: String,
            override val code: String? = null,
        ) : AutoPayError(message, code)

        data class RateLimitError(
            override val message: String,
            override val code: String? = null,
        ) : AutoPayError(message, code)

        data class UnknownError(
            override val message: String,
            override val code: String? = null,
        ) : AutoPayError(message, code)
    }

    fun handleException(exception: Exception): AutoPayError {
        return when (exception) {
            is ClientRequestException -> handleClientRequestException(exception)
            is ServerResponseException -> handleServerResponseException(exception)
            is IllegalArgumentException -> AutoPayError.ValidationError(
                message = exception.message ?: "Invalid input provided",
                code = "VALIDATION_ERROR",
            )
            is IllegalStateException -> AutoPayError.ValidationError(
                message = exception.message ?: "Invalid state",
                code = "STATE_ERROR",
            )
            else -> AutoPayError.UnknownError(
                message = exception.message ?: "An unexpected error occurred",
                code = "UNKNOWN_ERROR",
            )
        }
    }

    private fun handleClientRequestException(exception: ClientRequestException): AutoPayError {
        return when (exception.response.status) {
            HttpStatusCode.Unauthorized -> AutoPayError.AuthenticationError(
                message = "Authentication required. Please log in again.",
                code = "UNAUTHORIZED",
            )
            HttpStatusCode.Forbidden -> AutoPayError.AuthorizationError(
                message = "You don't have permission to perform this action.",
                code = "FORBIDDEN",
            )
            HttpStatusCode.NotFound -> AutoPayError.NotFoundError(
                message = "The requested AutoPay schedule was not found.",
                code = "NOT_FOUND",
            )
            HttpStatusCode.Conflict -> AutoPayError.ConflictError(
                message = "The AutoPay schedule already exists or conflicts with existing data.",
                code = "CONFLICT",
            )
            HttpStatusCode.TooManyRequests -> AutoPayError.RateLimitError(
                message = "Too many requests. Please try again later.",
                code = "RATE_LIMIT",
            )
            HttpStatusCode.BadRequest -> AutoPayError.ValidationError(
                message = "Invalid request data. Please check your input.",
                code = "BAD_REQUEST",
            )
            else -> AutoPayError.NetworkError(
                message = "Network error occurred. Please check your connection.",
                code = "NETWORK_ERROR",
            )
        }
    }

    private fun handleServerResponseException(exception: ServerResponseException): AutoPayError {
        return when (exception.response.status) {
            HttpStatusCode.InternalServerError -> AutoPayError.ServerError(
                message = "Server error occurred. Please try again later.",
                code = "INTERNAL_SERVER_ERROR",
            )
            HttpStatusCode.ServiceUnavailable -> AutoPayError.ServerError(
                message = "Service temporarily unavailable. Please try again later.",
                code = "SERVICE_UNAVAILABLE",
            )
            HttpStatusCode.GatewayTimeout -> AutoPayError.NetworkError(
                message = "Request timeout. Please try again.",
                code = "TIMEOUT",
            )
            else -> AutoPayError.ServerError(
                message = "Server error occurred. Please try again later.",
                code = "SERVER_ERROR",
            )
        }
    }

    fun <T> createErrorDataState(error: AutoPayError): DataState<T> {
        return DataState.Error(
            exception = Exception(error.message),
            data = null,
        )
    }

    /**
     * Phase-3 cutover — sibling of [createErrorDataState] emitting the
     * template's [ScreenState] error model. Routes authentication/authorization
     * classes to [ScreenState.Unauthenticated] and network classes to
     * [ScreenState.NoNetwork]; everything else lands as [ScreenState.Error].
     * Callers now on [ScreenState] should prefer this over the DataState variant.
     */
    fun <T> createErrorScreenState(error: AutoPayError): ScreenState<T> = when (error) {
        is AutoPayError.AuthenticationError,
        is AutoPayError.AuthorizationError,
        -> ScreenState.Unauthenticated

        is AutoPayError.NetworkError -> ScreenState.NoNetwork(isCaptivePortal = false)

        else -> ScreenState.Error(
            error = Exception(error.message),
            isNetworkError = false,
        )
    }

    fun getErrorMessage(error: AutoPayError): String {
        return when (error) {
            is AutoPayError.NetworkError -> "Network Error: ${error.message}"
            is AutoPayError.ValidationError -> "Validation Error: ${error.message}"
            is AutoPayError.ServerError -> "Server Error: ${error.message}"
            is AutoPayError.AuthenticationError -> "Authentication Error: ${error.message}"
            is AutoPayError.AuthorizationError -> "Authorization Error: ${error.message}"
            is AutoPayError.NotFoundError -> "Not Found: ${error.message}"
            is AutoPayError.ConflictError -> "Conflict: ${error.message}"
            is AutoPayError.RateLimitError -> "Rate Limit: ${error.message}"
            is AutoPayError.UnknownError -> "Error: ${error.message}"
        }
    }

    fun isRetryableError(error: AutoPayError): Boolean {
        return when (error) {
            is AutoPayError.NetworkError -> true
            is AutoPayError.ServerError -> true
            is AutoPayError.RateLimitError -> true
            else -> false
        }
    }
}
