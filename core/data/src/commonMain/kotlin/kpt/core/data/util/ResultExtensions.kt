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

import kpt.core.base.network.NetworkError

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
