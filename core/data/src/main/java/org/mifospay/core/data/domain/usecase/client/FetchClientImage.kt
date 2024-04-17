package org.mifospay.core.data.domain.usecase.client

import okhttp3.ResponseBody
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.fineract.repository.FineractRepository
import org.mifospay.core.data.util.ErrorJsonMessageHelper.getUserMessage
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class FetchClientImage @Inject constructor(private val mFineractRepository: FineractRepository) :
    UseCase<FetchClientImage.RequestValues, FetchClientImage.ResponseValue>() {

    data class RequestValues(val clientid: Long) : UseCase.RequestValues
    data class ResponseValue(val responseBody: ResponseBody) : UseCase.ResponseValue

    override fun executeUseCase(requestValues: RequestValues) {
        mFineractRepository.getClientImage(requestValues.clientid)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<ResponseBody>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    getUserMessage(e)?.let { useCaseCallback.onError(it) }
                }

                override fun onNext(responseBody: ResponseBody) {
                    useCaseCallback.onSuccess(ResponseValue(responseBody))
                }
            })
    }
}
