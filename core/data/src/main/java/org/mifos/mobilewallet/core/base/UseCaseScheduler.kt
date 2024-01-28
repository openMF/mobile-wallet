package org.mifos.mobilewallet.core.base

import org.mifos.mobilewallet.core.base.UseCase.UseCaseCallback

/**
 * Interface for schedulers, see [UseCaseThreadPoolScheduler].
 */
interface UseCaseScheduler {
    fun execute(runnable: Runnable?)
    fun <V : UseCase.ResponseValue?> notifyResponse(
        response: V, useCaseCallback: UseCaseCallback<V>?
    )

    fun <V : UseCase.ResponseValue?> onError(
        message: String?, useCaseCallback: UseCaseCallback<V>?
    )
}
