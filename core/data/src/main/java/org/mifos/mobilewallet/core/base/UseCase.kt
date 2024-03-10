package org.mifos.mobilewallet.core.base

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
