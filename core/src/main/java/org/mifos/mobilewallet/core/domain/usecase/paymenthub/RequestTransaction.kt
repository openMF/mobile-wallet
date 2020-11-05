package org.mifos.mobilewallet.core.domain.usecase.paymenthub

import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.paymenthub.entity.Transaction
import org.mifos.mobilewallet.core.data.paymenthub.entity.TransactionInfo
import org.mifos.mobilewallet.core.data.paymenthub.repository.PaymentHubRepository
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Devansh on 10/07/2020
 */
class RequestTransaction @Inject constructor(private val paymentHubRepository: PaymentHubRepository) :
        UseCase<RequestTransaction.RequestValues, RequestTransaction.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {

        paymentHubRepository.requestTransaction(requestValues.transactionRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<TransactionInfo>() {
                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable) {
                        useCaseCallback.onError(e.message)
                    }

                    override fun onNext(t: TransactionInfo) {
                        useCaseCallback.onSuccess(ResponseValue(t))
                    }
                })

    }

    class RequestValues(val transactionRequest: Transaction) : UseCase.RequestValues

    class ResponseValue(val transactionInfo: TransactionInfo) : UseCase.ResponseValue
}
