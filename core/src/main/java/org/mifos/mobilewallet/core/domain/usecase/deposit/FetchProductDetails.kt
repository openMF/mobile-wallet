package org.mifos.mobilewallet.core.domain.usecase.deposit

import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.common.FineractRepository
import org.mifos.mobilewallet.core.data.fineractcn.entity.deposit.DepositAccount
import org.mifos.mobilewallet.core.data.fineractcn.entity.deposit.Product
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Devansh on 23/06/2020
 */
class FetchProductDetails @Inject constructor(private val apiRepository: FineractRepository) :
        UseCase<FetchProductDetails.RequestValues, FetchProductDetails.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {
        apiRepository.fetchProductDetails(requestValues.productIdentifier)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<Product>() {

                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable)
                            = useCaseCallback.onError(e.message)

                    override fun onNext(product: Product) =
                        useCaseCallback.onSuccess(ResponseValue(product))
                })
    }

    class RequestValues(val productIdentifier: String) : UseCase.RequestValues

    class ResponseValue(val product: Product) : UseCase.ResponseValue

}