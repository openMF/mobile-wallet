package org.mifospay.core.data.domain.usecase.twofactor

import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.fineract.repository.FineractRepository
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by ankur on 01/June/2018
 */
class RequestOTP @Inject constructor(private val mFineractRepository: FineractRepository) :
    UseCase<RequestOTP.RequestValues, RequestOTP.ResponseValue>() {

    class RequestValues(val deliveryMethod: String) : UseCase.RequestValues
    class ResponseValue(val response: String) : UseCase.ResponseValue

    override fun executeUseCase(requestValues: RequestValues) {
        mFineractRepository.requestOTP(requestValues.deliveryMethod)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<String>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    useCaseCallback.onError(e.toString())
                }

                override fun onNext(response: String) {
                    useCaseCallback.onSuccess(ResponseValue(response))
                }
            })

    }
}
