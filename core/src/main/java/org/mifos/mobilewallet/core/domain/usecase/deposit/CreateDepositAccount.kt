package org.mifos.mobilewallet.core.domain.usecase.deposit

import okhttp3.ResponseBody
import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.common.FineractRepository
import org.mifos.mobilewallet.core.data.fineractcn.entity.deposit.DepositAccountPayload
import org.mifos.mobilewallet.core.utils.Constants
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Devansh on 29/06/2020
 */
class CreateDepositAccount @Inject constructor(
        private val apiRepository: FineractRepository) :
        UseCase<CreateDepositAccount.RequestValues,
                CreateDepositAccount.ResponseValue>()  {

    override fun executeUseCase(requestValues: RequestValues) {
        /**
         * Used to identify Product Instance for wallet users.
         * Note: This instance should already exist in the hosted FineractCN instance
         */
        val walletProductInstanceIdentifier = Constants.WALLET_DEPOSIT_PRODUCT_INSTANCE_IDENTIFIER;
        val newDepositAccount = DepositAccountPayload(
                requestValues.customerIdentifier, walletProductInstanceIdentifier)

        apiRepository.createDepositAccount(newDepositAccount)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<ResponseBody>() {

                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable)
                            = useCaseCallback.onError(e.message)

                    override fun onNext(responseBody: ResponseBody) {

                    }
                })
    }

    class RequestValues(val customerIdentifier: String) : UseCase.RequestValues

    class ResponseValue : UseCase.ResponseValue

}