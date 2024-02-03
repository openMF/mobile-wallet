package org.mifos.mobilewallet.core.domain.usecase.twofactor

import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by ankur on 01/June/2018
 */
class RequestOTP @Inject constructor(private val mFineractRepository: FineractRepository) :
    UseCase<RequestOTP.RequestValues?, RequestOTP.ResponseValue?>() {

    class RequestValues(val deliveryMethod: String) : UseCase.RequestValues
    class ResponseValue(val response: String) : UseCase.ResponseValue

    override fun executeUseCase(requestValues: RequestValues?) {
        requestValues?.let {
            mFineractRepository.requestOTP(it.deliveryMethod)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<String?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        useCaseCallback.onError(e.toString())
                    }

                    override fun onNext(t: String?) {
                        useCaseCallback.onSuccess(t?.let { response -> ResponseValue(response) })
                    }
                })
        }
    }
}
