package org.mifos.mobilewallet.core.domain.usecase.account

import com.mifos.mobilewallet.model.domain.Transaction
import com.mifos.mobilewallet.model.entity.accounts.savings.SavingsWithAssociations
import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.fineract.entity.mapper.TransactionMapper
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import org.mifos.mobilewallet.core.util.Constants
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by naman on 11/7/17.
 */
class FetchAccountTransactions @Inject constructor(
    private val fineractRepository: FineractRepository,
    private val transactionMapper: TransactionMapper
) : UseCase<FetchAccountTransactions.RequestValues, FetchAccountTransactions.ResponseValue?>() {

    protected override fun executeUseCase(requestValues: RequestValues) {
        fineractRepository.getSelfAccountTransactions(requestValues.accountId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<SavingsWithAssociations>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    useCaseCallback.onError(
                        Constants.ERROR_FETCHING_REMOTE_ACCOUNT_TRANSACTIONS
                    )
                }

                override fun onNext(transactions: SavingsWithAssociations) {
                    useCaseCallback.onSuccess(
                        ResponseValue(
                            transactionMapper.transformTransactionList(transactions)
                        )
                    )
                }
            })
    }

    data class RequestValues(var accountId: Long) : UseCase.RequestValues
    data class ResponseValue(val transactions: List<Transaction>) : UseCase.ResponseValue
}
