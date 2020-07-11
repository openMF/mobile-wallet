package org.mifos.mobilewallet.core.domain.usecase.paymenthub

import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.paymenthub.entity.Amount
import org.mifos.mobilewallet.core.data.paymenthub.entity.PartyIdInfo
import org.mifos.mobilewallet.core.data.paymenthub.entity.TransactingEntity
import org.mifos.mobilewallet.core.data.paymenthub.entity.Transaction
import org.mifos.mobilewallet.core.data.paymenthub.entity.TransactionInfo
import org.mifos.mobilewallet.core.data.paymenthub.repository.PaymentHubRepository
import org.mifos.mobilewallet.core.utils.Constants
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class CreateTransaction @Inject constructor(private val paymentHubRepository: PaymentHubRepository) :
        UseCase<CreateTransaction.RequestValues, CreateTransaction.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {
        val payee = TransactingEntity(requestValues.payeeParty);
        val payer = TransactingEntity(requestValues.payerParty);
        val transaction = Transaction(payee, payer, requestValues.amount);

        paymentHubRepository.createTransaction(transaction)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<TransactionInfo>() {
                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable) {
                        useCaseCallback.onError(Constants.ERROR_FETCHING_ACCOUNTS)
                    }

                    override fun onNext(t: TransactionInfo) {
                        useCaseCallback.onSuccess(ResponseValue(t))
                    }
                })

    }

    class RequestValues(
            val payeeParty: PartyIdInfo,
            val payerParty: PartyIdInfo,
            val amount: Amount) : UseCase.RequestValues

    class ResponseValue(val transactionInfo: TransactionInfo) : UseCase.ResponseValue
}
