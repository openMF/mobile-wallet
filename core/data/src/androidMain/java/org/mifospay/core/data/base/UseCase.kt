/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.base

/**
 * Use cases are the entry points to the domain layer.
 *
 * @param <Q> the request type
 * @param <P> the response type
</P></Q> */
abstract class UseCase<Q : UseCase.RequestValues, P : UseCase.ResponseValue?> {
    lateinit var walletRequestValues: Q
    lateinit var useCaseCallback: UseCaseCallback<P>

    fun setRequestValues(requestValues: Q) {
        this.walletRequestValues = requestValues
    }

    fun run() {
        executeUseCase(walletRequestValues)
    }

    protected abstract fun executeUseCase(requestValues: Q)

    /**
     * Data passed to a request.
     */
    interface RequestValues

    /**
     * Data received from a request.
     */
    interface ResponseValue
    interface UseCaseCallback<R> {
        fun onSuccess(response: R)
        fun onError(message: String)
    }
}
