package org.mifos.mobilewallet.core.base

import org.mifos.mobilewallet.core.base.UseCase.UseCaseCallback

/**
 * Runs [UseCase]s using a [UseCaseScheduler].
 */
class UseCaseHandler(private val mUseCaseScheduler: UseCaseScheduler) {
    fun <T : UseCase.RequestValues, R : UseCase.ResponseValue?> execute(
        useCase: UseCase<T, R>, values: T?, callback: UseCaseCallback<R>
    ) {
        values?.let {  useCase.walletRequestValues = values }
        useCase.useCaseCallback = UiCallbackWrapper(callback, this)
        mUseCaseScheduler.execute { useCase.run() }
    }

    fun <V : UseCase.ResponseValue?> notifyResponse(
        response: V,
        useCaseCallback: UseCaseCallback<V>?
    ) {
        mUseCaseScheduler.notifyResponse(response, useCaseCallback)
    }

    private fun <V : UseCase.ResponseValue?> notifyError(
        message: String?,
        useCaseCallback: UseCaseCallback<V>?
    ) {
        mUseCaseScheduler.onError(message, useCaseCallback)
    }

    private class UiCallbackWrapper<V : UseCase.ResponseValue?>(
        private val mCallback: UseCaseCallback<V>?,
        private val mUseCaseHandler: UseCaseHandler
    ) : UseCaseCallback<V> {
        override fun onSuccess(response: V) {
            mUseCaseHandler.notifyResponse(response, mCallback)
        }

        override fun onError(message: String) {
            mUseCaseHandler.notifyError(message, mCallback)
        }
    }

    companion object {
        var instance: UseCaseHandler? = null
            get() {
                if (field == null) {
                    field = UseCaseHandler(UseCaseThreadPoolScheduler())
                }
                return field
            }
            private set
    }
}
