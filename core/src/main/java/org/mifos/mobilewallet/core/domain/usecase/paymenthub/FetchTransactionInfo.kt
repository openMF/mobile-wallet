package org.mifos.mobilewallet.core.domain.usecase.paymenthub

import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.paymenthub.entity.TransactionInfo
import org.mifos.mobilewallet.core.data.paymenthub.entity.TransactionStatus
import org.mifos.mobilewallet.core.data.paymenthub.repository.PaymentHubRepository
import org.mifos.mobilewallet.core.utils.Constants
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class FetchTransactionInfo @Inject constructor(val paymentHubRepository: PaymentHubRepository) :
        UseCase<FetchTransactionInfo.RequestValues, FetchTransactionInfo.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {

        paymentHubRepository.fetchTransactionInfo(requestValues.transactionId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<TransactionStatus>() {
                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable) {
                        useCaseCallback.onError("Error")
                    }

                    override fun onNext(t: TransactionStatus) {
                        useCaseCallback.onSuccess(ResponseValue(t))
                    }
                })

    }

    class RequestValues(val transactionId: String) : UseCase.RequestValues

    class ResponseValue(val transactionStatus: TransactionStatus) : UseCase.ResponseValue
}
