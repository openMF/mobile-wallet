package org.mifos.mobilewallet.core.domain.usecase.paymenthub

import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.paymenthub.entity.TransactionResponse
import org.mifos.mobilewallet.core.data.paymenthub.repository.PaymentHubRepository
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
                .subscribe(object : Subscriber<TransactionResponse>() {
                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable) {
                        useCaseCallback.onError("Error")
                    }

                    override fun onNext(t: TransactionResponse) {
                        useCaseCallback.onSuccess(ResponseValue(t))
                    }
                })

    }

    class RequestValues(val transactionId: String) : UseCase.RequestValues

    class ResponseValue(val transactionResponse: TransactionResponse) : UseCase.ResponseValue
}
