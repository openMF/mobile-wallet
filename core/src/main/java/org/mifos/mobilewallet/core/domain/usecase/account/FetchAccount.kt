package org.mifos.mobilewallet.core.domain.usecase.account

import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.fineract.entity.client.ClientAccounts
import org.mifos.mobilewallet.core.data.fineract.entity.mapper.AccountMapper
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import org.mifos.mobilewallet.core.domain.model.Account
import org.mifos.mobilewallet.core.utils.Constants
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Devansh on 11/07/2020
 */
class FetchAccount @Inject constructor(
        private val fineractRepository: FineractRepository,
        private val accountMapper: AccountMapper) :
        UseCase<FetchAccount.RequestValues, FetchAccount.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {

        fineractRepository.getAccounts(requestValues.clientId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<ClientAccounts>() {
                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable) {
                        useCaseCallback.onError(Constants.ERROR_FETCHING_ACCOUNTS)
                    }

                    override fun onNext(clientAccounts: ClientAccounts) {
                        val accounts: List<Account> = accountMapper.transform(clientAccounts)
                        if (accounts.isNotEmpty()) {
                            var walletAccount: Account? = null
                            for (account in accounts) {
                                if (account.productId ==
                                        Constants.WALLET_ACCOUNT_SAVINGS_PRODUCT_ID.toLong()) {
                                    walletAccount = account
                                    break
                                }
                            }

                            walletAccount?.let {
                               useCaseCallback.onSuccess(ResponseValue(it))

                            } ?: useCaseCallback.onError(Constants.NO_ACCOUNTS_FOUND)

                        } else {
                            useCaseCallback.onError(Constants.ERROR_FETCHING_ACCOUNT)
                        }
                    }
                })
    }

    class RequestValues(val clientId: Long): UseCase.RequestValues

    class ResponseValue(val account: Account) : UseCase.ResponseValue

}