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

import android.os.Handler
import android.os.Looper
import org.mifospay.core.data.base.UseCase.UseCaseCallback
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Executes asynchronous tasks using a [ThreadPoolExecutor].
 *
 *
 * See also [ThreadPoolExecutor] for a list of factory methods to create common
 * [java.util.concurrent.ExecutorService]s for different scenarios.
 */
class UseCaseThreadPoolScheduler : UseCaseScheduler {

    private val mHandler = Handler(Looper.getMainLooper())
    private var mThreadPoolExecutor: ThreadPoolExecutor = ThreadPoolExecutor(
        POOL_SIZE,
        MAX_POOL_SIZE,
        TIMEOUT.toLong(),
        TimeUnit.SECONDS,
        ThreadPoolQueue(MAX_POOL_SIZE),
    )

    override fun execute(runnable: Runnable?) {
        mThreadPoolExecutor.execute(runnable)
    }

    override fun <V : UseCase.ResponseValue?> notifyResponse(
        response: V,
        useCaseCallback: UseCaseCallback<V>?,
    ) {
        mHandler.post { useCaseCallback!!.onSuccess(response) }
    }

    override fun <V : UseCase.ResponseValue?> onError(
        message: String?,
        useCaseCallback: UseCaseCallback<V>?,
    ) {
        mHandler.post { useCaseCallback!!.onError(message!!) }
    }

    companion object {
        const val POOL_SIZE = 2
        const val MAX_POOL_SIZE = 40
        const val TIMEOUT = 60
    }
}
