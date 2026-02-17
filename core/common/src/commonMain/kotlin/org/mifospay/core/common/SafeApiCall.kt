/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.common

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.bodyAsText
import kotlinx.io.IOException

/**
 * Function type for parsing error response body into a user-friendly message.
 * This allows repositories to provide custom error parsing logic (e.g., parsing MifosError).
 */
typealias ErrorBodyParser = suspend (responseBody: String, statusCode: Int) -> String

/**
 * Default error body parser that returns the response body as-is or a generic message.
 */
val defaultErrorBodyParser: ErrorBodyParser = { body, _ ->
    body.ifBlank { "An error occurred" }
}

/**
 * Safely executes an API call and wraps the result in [DataState].
 *
 * This function provides consistent error handling for network operations:
 * - 4xx client errors (ClientRequestException) → HttpStatusException with parsed message
 * - 5xx server errors (ServerResponseException) → HttpStatusException with parsed message
 * - Network connectivity issues (IOException) → NetworkException
 * - Other exceptions → preserved as-is or wrapped with HTTP status extraction
 *
 * @param T The type of data expected from the API call
 * @param errorBodyParser Optional function to parse error response body into user message
 * @param apiCall The suspend function that makes the actual API call
 * @return [DataState.Success] with the data on success, or [DataState.Error] with a typed exception on failure
 *
 * Usage:
 * ```kotlin
 * suspend fun makeTransfer(payload: TransferPayload): DataState<TPTResponse> {
 *     return safeApiCall(errorBodyParser = ::parseMifosError) {
 *         apiManager.thirdPartyTransferApi.makeTransfer(payload)
 *     }
 * }
 * ```
 */
suspend fun <T> safeApiCall(
    errorBodyParser: ErrorBodyParser = defaultErrorBodyParser,
    apiCall: suspend () -> T,
): DataState<T> {
    return try {
        val result = apiCall()
        DataState.Success(result)
    } catch (e: ClientRequestException) {
        // 4xx client errors (Bad Request, Unauthorized, Forbidden, Not Found, etc.)
        val status = e.response.status.value
        val responseBody = try {
            e.response.bodyAsText()
        } catch (_: Exception) {
            ""
        }
        val userMessage = errorBodyParser(responseBody, status)
        DataState.Error(
            HttpStatusException(
                statusCode = status,
                userMessage = userMessage,
                developerMessage = e.message,
            ),
        )
    } catch (e: ServerResponseException) {
        // 5xx server errors (Internal Server Error, Bad Gateway, Service Unavailable, etc.)
        val status = e.response.status.value
        val responseBody = try {
            e.response.bodyAsText()
        } catch (_: Exception) {
            ""
        }
        val userMessage = errorBodyParser(responseBody, status)
        DataState.Error(
            HttpStatusException(
                statusCode = status,
                userMessage = userMessage,
                developerMessage = e.message,
            ),
        )
    } catch (e: IOException) {
        // Network connectivity errors (no internet, timeout, connection reset, etc.)
        DataState.Error(NetworkException("Network unavailable. Please check your connection."))
    } catch (e: Exception) {
        // Fallback for other exceptions - try to extract HTTP status if available
        val wrappedException = parseExceptionForHttpStatus(e)
        DataState.Error(wrappedException)
    }
}

/**
 * Safely executes an API call that returns a value directly (not wrapped in DataState).
 * Returns the result or throws a typed exception.
 *
 * Use this when you need to throw exceptions rather than return DataState.
 *
 * @param T The type of data expected from the API call
 * @param errorBodyParser Optional function to parse error response body into user message
 * @param apiCall The suspend function that makes the actual API call
 * @return The result of the API call
 * @throws HttpStatusException for HTTP errors
 * @throws NetworkException for network connectivity issues
 */
suspend fun <T> safeApiCallOrThrow(
    errorBodyParser: ErrorBodyParser = defaultErrorBodyParser,
    apiCall: suspend () -> T,
): T {
    return try {
        apiCall()
    } catch (e: ClientRequestException) {
        val status = e.response.status.value
        val responseBody = try {
            e.response.bodyAsText()
        } catch (_: Exception) {
            ""
        }
        val userMessage = errorBodyParser(responseBody, status)
        throw HttpStatusException(
            statusCode = status,
            userMessage = userMessage,
            developerMessage = e.message,
        )
    } catch (e: ServerResponseException) {
        val status = e.response.status.value
        val responseBody = try {
            e.response.bodyAsText()
        } catch (_: Exception) {
            ""
        }
        val userMessage = errorBodyParser(responseBody, status)
        throw HttpStatusException(
            statusCode = status,
            userMessage = userMessage,
            developerMessage = e.message,
        )
    } catch (e: IOException) {
        throw NetworkException("Network unavailable. Please check your connection.")
    } catch (e: Exception) {
        throw parseExceptionForHttpStatus(e)
    }
}

/**
 * Attempts to parse exception message for HTTP status codes.
 * Useful when the original exception type is lost in the call chain.
 */
private fun parseExceptionForHttpStatus(e: Exception): Exception {
    val message = e.message ?: return e

    // Try to extract HTTP status code from common error patterns
    // Matches patterns like "400", "404", "500" in error messages
    val statusRegex = Regex("""(\d{3})""")
    val match = statusRegex.find(message)

    return if (match != null) {
        val statusCode = match.value.toIntOrNull()
        if (statusCode != null && statusCode in 400..599) {
            HttpStatusException(
                statusCode = statusCode,
                userMessage = message,
                developerMessage = null,
            )
        } else {
            e
        }
    } else {
        e
    }
}
