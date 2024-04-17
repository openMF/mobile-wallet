package org.mifospay.core.data.base

import android.os.Handler
import org.mifospay.core.data.base.UseCase.UseCaseCallback
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Executes asynchronous tasks using a [ThreadPoolExecutor].
 *
 *
 * See also [Executors] for a list of factory methods to create common
 * [java.util.concurrent.ExecutorService]s for different scenarios.
 */
class UseCaseThreadPoolScheduler : UseCaseScheduler {

    private val mHandler = Handler()
    var mThreadPoolExecutor: ThreadPoolExecutor

    init {
        mThreadPoolExecutor = ThreadPoolExecutor(
            POOL_SIZE, MAX_POOL_SIZE, TIMEOUT.toLong(),
            TimeUnit.SECONDS, ThreadPoolQueue(MAX_POOL_SIZE)
        )
    }

    override fun execute(runnable: Runnable?) {
        mThreadPoolExecutor.execute(runnable)
    }

    override fun <V : UseCase.ResponseValue?> notifyResponse(
        response: V,
        useCaseCallback: UseCaseCallback<V>?
    ) {
        mHandler.post { useCaseCallback!!.onSuccess(response) }
    }

    override fun <V : UseCase.ResponseValue?> onError(
        message: String?,
        useCaseCallback: UseCaseCallback<V>?
    ) {
        mHandler.post { useCaseCallback!!.onError(message!!) }
    }

    companion object {
        const val POOL_SIZE = 2
        const val MAX_POOL_SIZE = 40
        const val TIMEOUT = 60
    }
}
