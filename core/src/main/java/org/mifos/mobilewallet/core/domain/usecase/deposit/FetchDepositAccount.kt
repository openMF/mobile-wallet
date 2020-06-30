package org.mifos.mobilewallet.core.domain.usecase.deposit

import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.common.FineractRepository
import org.mifos.mobilewallet.core.data.fineractcn.entity.deposit.DepositAccount
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Devansh on 29/06/2020
 */
class FetchDepositAccount @Inject constructor(
        private val apiRepository: FineractRepository) :
        UseCase<FetchDepositAccount.RequestValues,
                FetchDepositAccount.ResponseValue>()  {

    override fun executeUseCase(requestValues: RequestValues) {
        apiRepository.fetchDepositAccountDetails(requestValues.accountIdentifier)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<DepositAccount>() {

                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable)
                            = useCaseCallback.onError(e.message)

                    override fun onNext(depositAccount: DepositAccount) {
                        useCaseCallback.onSuccess(ResponseValue(depositAccount))
                    }
                })
    }

    class RequestValues(val accountIdentifier: String) : UseCase.RequestValues

    class ResponseValue(val depositAccount: DepositAccount) : UseCase.ResponseValue

}