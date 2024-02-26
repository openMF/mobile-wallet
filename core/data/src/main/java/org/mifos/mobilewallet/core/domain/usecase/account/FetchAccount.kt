package org.mifos.mobilewallet.core.domain.usecase.account


import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.fineract.entity.mapper.AccountMapper
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import org.mifos.mobilewallet.core.utils.Constants
import com.mifos.mobilewallet.model.domain.Account
import com.mifos.mobilewallet.model.entity.client.ClientAccounts
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

import javax.inject.Inject

class FetchAccount @Inject constructor(private val fineractRepository: FineractRepository) :
    UseCase<FetchAccount.RequestValues, FetchAccount.ResponseValue>() {

    @Inject
    lateinit var accountMapper: AccountMapper

    override fun executeUseCase(requestValues: RequestValues) {
        fineractRepository.getSelfAccounts(requestValues.clientId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<ClientAccounts>() {
                override fun onCompleted() {}

                override fun onError(e: Throwable) {
                    useCaseCallback.onError(Constants.ERROR_FETCHING_ACCOUNTS)
                }

                override fun onNext(clientAccounts: ClientAccounts) {
                    val accounts = accountMapper.transform(clientAccounts)
                    if (!accounts.isNullOrEmpty()) {
                        var walletAccount: Account? = null
                        for (account in accounts) {
                            if (account.productId.toInt() == Constants.WALLET_ACCOUNT_SAVINGS_PRODUCT_ID) {
                                walletAccount = account
                                break
                            }
                        }
                        if (walletAccount != null) {
                            useCaseCallback.onSuccess(ResponseValue(walletAccount))
                        } else {
                            useCaseCallback.onError(Constants.NO_ACCOUNT_FOUND)
                        }
                    } else {
                        useCaseCallback.onError(Constants.NO_ACCOUNTS_FOUND)
                    }
                }
            })
    }

   data  class RequestValues(val clientId: Long) : UseCase.RequestValues

    data class ResponseValue(val account: Account) : UseCase.ResponseValue
}
