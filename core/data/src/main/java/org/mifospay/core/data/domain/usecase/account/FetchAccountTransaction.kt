package org.mifospay.core.data.domain.usecase.account

import com.mifospay.core.model.domain.Transaction
import com.mifospay.core.model.entity.accounts.savings.Transactions
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.fineract.entity.mapper.TransactionMapper
import org.mifospay.core.data.fineract.repository.FineractRepository
import org.mifospay.core.data.util.Constants
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Shivansh on 15/6/19.
 */
class FetchAccountTransaction @Inject constructor(
    val fineractRepository: FineractRepository,
    val transactionMapper: TransactionMapper
) :
    UseCase<FetchAccountTransaction.RequestValues, FetchAccountTransaction.ResponseValue>() {
    override fun executeUseCase(requestValues: RequestValues) {
        fineractRepository.getSelfAccountTransactionFromId(
            requestValues.accountId,
            requestValues.transactionId
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<Transactions?> {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    if (e.message == "HTTP 401 Unauthorized") {
                        useCaseCallback.onError(Constants.UNAUTHORIZED_ERROR)
                    } else {
                        useCaseCallback.onError(
                            Constants.ERROR_FETCHING_REMOTE_ACCOUNT_TRANSACTIONS
                        )
                    }
                }

                override fun onNext(transaction: Transactions?) {
                    useCaseCallback.onSuccess(
                        ResponseValue(
                            transactionMapper
                                .transformInvoice(transaction)
                        )
                    )
                }
            })
    }

    data class RequestValues(
        val accountId: Long,
        val transactionId: Long
    ) : UseCase.RequestValues


    data class ResponseValue(val transaction: Transaction) : UseCase.ResponseValue
}
