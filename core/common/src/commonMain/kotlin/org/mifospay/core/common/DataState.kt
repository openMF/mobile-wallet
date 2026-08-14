/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifospay.core.common

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.io.IOException

sealed class DataState<out T> {
    abstract val data: T?

    data object Loading : DataState<Nothing>() {
        override val data: Nothing? get() = null
    }

    data class Success<T>(
        override val data: T,
    ) : DataState<T>()

    data class Error<T>(
        val exception: Throwable,
        override val data: T? = null,
    ) : DataState<T>() {
        val message = exception.message.toString()
    }
}

/**
 * Converts a Flow<T> to Flow<DataState<T>> with Loading, Success, and Error states.
 * Basic version without HTTP error parsing.
 */
fun <T> Flow<T>.asDataStateFlow(): Flow<DataState<T>> =
    map<T, DataState<T>> { DataState.Success(it) }
        .onStart { emit(DataState.Loading) }
        .catch { emit(DataState.Error(it, null)) }

/**
 * Converts a Flow<T> to Flow<DataState<T>> with HTTP error parsing support.
 *
 * This version parses HTTP error response bodies using the provided [errorBodyParser]
 * to extract user-friendly error messages from server responses.
 *
 * @param errorBodyParser Function to parse error response body into user message
 * @return Flow<DataState<T>> with typed exceptions for HTTP errors
 *
 * Usage:
 * ```kotlin
 * return apiManager.api.makeTransfer(payload)
 *     .map { it.toModel() }
 *     .asDataStateFlow(parseMifosError)
 *     .flowOn(dispatcher)
 * ```
 */
fun <T> Flow<T>.asDataStateFlow(
    errorBodyParser: ErrorBodyParser,
): Flow<DataState<T>> =
    map<T, DataState<T>> { DataState.Success(it) }
        .onStart { emit(DataState.Loading) }
        .catch { e -> emit(DataState.Error(e.toTypedException(errorBodyParser), null)) }

/**
 * Converts an exception to a typed exception with parsed error message.
 */
private suspend fun Throwable.toTypedException(
    errorBodyParser: ErrorBodyParser,
): Throwable = when (this) {
    is ClientRequestException -> {
        val status = response.status.value
        val responseBody = try {
            response.bodyAsText()
        } catch (_: Exception) {
            ""
        }
        val userMessage = errorBodyParser(responseBody, status)
        HttpStatusException(
            statusCode = status,
            userMessage = userMessage,
            developerMessage = message,
        )
    }
    is ServerResponseException -> {
        val status = response.status.value
        val responseBody = try {
            response.bodyAsText()
        } catch (_: Exception) {
            ""
        }
        val userMessage = errorBodyParser(responseBody, status)
        HttpStatusException(
            statusCode = status,
            userMessage = userMessage,
            developerMessage = message,
        )
    }
    is IOException -> NetworkException("Network unavailable. Please check your connection.")
    else -> this
}
