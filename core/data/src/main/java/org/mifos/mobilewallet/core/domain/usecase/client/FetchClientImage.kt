package org.mifos.mobilewallet.core.domain.usecase.client

import okhttp3.ResponseBody
import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import org.mifos.mobilewallet.core.util.ErrorJsonMessageHelper.getUserMessage
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by ankur on 02/August/2018
 */
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
