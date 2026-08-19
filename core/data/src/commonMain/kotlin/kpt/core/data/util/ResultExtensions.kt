/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
@file:Suppress("MatchingDeclarationName")

package kpt.core.data.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kpt.core.base.common.DataState
import kpt.core.base.network.NetworkError
import kpt.core.base.network.NetworkResult

/** Custom exception class that wraps a RemoteError */
class RemoteException(
    val networkError: NetworkError,
    message: String = networkError.name,
) : Exception(message)

fun NetworkError.toThrowable(): Throwable = when (this) {
    NetworkError.BAD_REQUEST -> RemoteException(
        networkError = this,
        message = "Something went wrong with your request. Please try again.",
    )

    NetworkError.NOT_FOUND -> RemoteException(
        networkError = this,
        message = "The information you're looking for couldn't be found.",
    )

    NetworkError.UNAUTHORIZED -> RemoteException(
        networkError = this,
        message = "You need to sign in to access this content.",
    )

    NetworkError.REQUEST_TIMEOUT -> RemoteException(
        networkError = this,
        message = "The request is taking too long. Please check your connection and try again.",
    )

    NetworkError.TOO_MANY_REQUESTS -> RemoteException(
        networkError = this,
        message = "You're doing that too often. Please wait a moment and try again.",
    )

    NetworkError.SERVER -> RemoteException(
        networkError = this,
        message = "We're experiencing technical difficulties. Please try again later.",
    )

    NetworkError.SERIALIZATION -> RemoteException(
        networkError = this,
        message = "We received unexpected data. Please try refreshing the app.",
    )

    NetworkError.UNKNOWN -> RemoteException(
        networkError = this,
        message = "Something unexpected happened. Please try again.",
    )
}

/**
 * Simple conversion from Result to DataState. Success -> Loaded, Error ->
 * Error
 */
fun <D> NetworkResult<D, *>.toDataState(): DataState<D> = when (this) {
    is NetworkResult.Success -> DataState.Success(data)
    is NetworkResult.Error -> DataState.Error(error.toThrowable(), null)
}

/**
 * Converts kpt.corebase.network.NetworkResult to DataState with
 * existing data preserved in error state. Useful when you want to keep
 * showing previous data even when an error occurs.
 */
fun <D> NetworkResult<D, *>.toDataState(existingData: D?): DataState<D> = when (this) {
    is NetworkResult.Success -> DataState.Success(data)
    is NetworkResult.Error -> DataState.Error(error.toThrowable(), existingData)
}

/**
 * Smart conversion from kpt.corebase.network.NetworkResult to
 * DataState that maps certain RemoteErrors to more specific DataState
 * types.
 */
fun <D> NetworkResult<D, *>.toDataStateWithMapping(): DataState<D> = when (this) {
    is NetworkResult.Success -> DataState.Success(data)
    is NetworkResult.Error -> when (error) {
        NetworkError.REQUEST_TIMEOUT -> DataState.NoNetwork(null)
        else -> DataState.Error(error.toThrowable(), null)
    }
}

/** Smart conversion with existing data preserved. */
fun <D> NetworkResult<D, *>.toDataStateWithMapping(existingData: D?): DataState<D> = when (this) {
    is NetworkResult.Success -> DataState.Success(data)
    is NetworkResult.Error -> when (error) {
        NetworkError.REQUEST_TIMEOUT -> DataState.NoNetwork(existingData)
        else -> DataState.Error(error.toThrowable(), existingData)
    }
}

/**
 * Extension for Flow<kpt.corebase.network.NetworkResult> to convert
 * to Flow<DataState>
 */
fun <D> Flow<NetworkResult<D, *>>.asDataStateFlow(): Flow<DataState<D>> = map { it.toDataState() }
    .onStart { emit(DataState.Loading) }

/**
 * Extension for Flow<kpt.corebase.network.NetworkResult> with
 * existing data preservation
 */
fun <D> Flow<NetworkResult<D, *>>.asDataStateFlow(
    preserveDataOnError: Boolean = false,
    getCurrentData: () -> D? = { null },
): Flow<DataState<D>> = flow {
    emit(DataState.Loading)

    collect { result ->
        val dataState = if (preserveDataOnError) {
            result.toDataState(getCurrentData())
        } else {
            result.toDataState()
        }
        emit(dataState)
    }
}

/** Utility extension to check if DataState represents a successful state */
val <T> DataState<T>.isSuccess: Boolean
    get() = this is DataState.Success

/** Utility extension to check if DataState represents an error state */
val <T> DataState<T>.isError: Boolean
    get() = this is DataState.Error

/** Utility extension to get the error if DataState is in error state */
val <T> DataState<T>.errorOrNull: Throwable?
    get() = (this as? DataState.Error)?.error

/** Utility extension to get RemoteError if the error is RemoteException */
val <T> DataState<T>.networkErrorOrNull: NetworkError?
    get() = (this as? DataState.Error)?.error?.let { error ->
        (error as? RemoteException)?.networkError
    }
