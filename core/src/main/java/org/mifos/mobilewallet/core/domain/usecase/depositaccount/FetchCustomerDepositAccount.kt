package org.mifos.mobilewallet.core.domain.usecase.depositaccount

import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.common.FineractRepository
import org.mifos.mobilewallet.core.data.fineractcn.entity.depositaccount.DepositAccount
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Devansh on 17/06/2020
 */
class FetchCustomerDepositAccount @Inject constructor(
        private val apiRepository: FineractRepository) :
        UseCase<FetchCustomerDepositAccount.RequestValues,
                FetchCustomerDepositAccount.ResponseValue>()  {

    override fun executeUseCase(requestValues: RequestValues) {
        apiRepository.fetchCustomerDepositAccounts(requestValues.customerIdentifier)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<DepositAccount>>() {

                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable)
                            = useCaseCallback.onError(e.message)

                    override fun onNext(depositAccounts: List<DepositAccount>) {
                        /**
                         * For now, just selecting first account as wallet account
                         */
                        val walletAccount = depositAccounts[0]
                        useCaseCallback.onSuccess(ResponseValue(walletAccount))
                    }
                })
    }

    class RequestValues(val customerIdentifier: String) : UseCase.RequestValues

    class ResponseValue(val depositAccount: DepositAccount) : UseCase.ResponseValue

}