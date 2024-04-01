package org.mifos.mobilewallet.core.domain.usecase.account

import com.mifos.mobilewallet.model.domain.Account
import com.mifos.mobilewallet.model.entity.client.ClientAccounts
import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.fineract.entity.mapper.AccountMapper
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import org.mifos.mobilewallet.core.util.Constants
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by naman on 11/7/17.
 */
class FetchAccounts @Inject constructor(
    private val fineractRepository: FineractRepository,
    val accountMapper: AccountMapper) :
    UseCase<FetchAccounts.RequestValues, FetchAccounts.ResponseValue>() {
   
    override fun executeUseCase(requestValues: RequestValues) {
        fineractRepository.getAccounts(requestValues.clientId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<ClientAccounts?>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    useCaseCallback.onError(Constants.ERROR_FETCHING_ACCOUNTS)
                }

                override fun onNext(t: ClientAccounts?) {
                    val accounts = t
                    if (accounts != null) {
                        useCaseCallback.onSuccess(
                            ResponseValue(
                                accountMapper!!.transform(accounts)
                            )
                        )
                    } else {
                        useCaseCallback.onError(Constants.NO_ACCOUNTS_FOUND)
                    }
                }
            })
    }

    data class RequestValues(val clientId: Long) : UseCase.RequestValues
    data class ResponseValue(val accountList: List<Account>) : UseCase.ResponseValue
}
