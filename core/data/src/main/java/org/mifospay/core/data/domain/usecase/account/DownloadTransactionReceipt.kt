package org.mifospay.core.data.domain.usecase.account

import okhttp3.ResponseBody
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.fineract.repository.FineractRepository
import org.mifospay.core.data.util.Constants
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by ankur on 06/June/2018
 */
class DownloadTransactionReceipt @Inject constructor(private val mFineractRepository: FineractRepository) :
    UseCase<DownloadTransactionReceipt.RequestValues, DownloadTransactionReceipt.ResponseValue>() {
     override fun executeUseCase(requestValues: RequestValues) {
         requestValues.transactionId?.let {
             mFineractRepository.getTransactionReceipt(Constants.PDF, it)
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribeOn(Schedulers.io())
                 .subscribe(object : Subscriber<ResponseBody>() {
                     override fun onCompleted() {}
                     override fun onError(e: Throwable) {
                         useCaseCallback.onError(e.toString())
                     }

                     override fun onNext(t: ResponseBody) {
                         val responseBody = t
                         useCaseCallback.onSuccess(ResponseValue(responseBody))
                     }
                 })
         }
    }

    data class RequestValues( val transactionId: String?) : UseCase.RequestValues
    data class ResponseValue(val responseBody: ResponseBody) : UseCase.ResponseValue
}
