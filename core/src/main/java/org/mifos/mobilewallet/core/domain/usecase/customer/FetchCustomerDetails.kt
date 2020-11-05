package org.mifos.mobilewallet.core.domain.usecase.customer

import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.common.FineractRepository
import org.mifos.mobilewallet.core.data.fineractcn.entity.customer.Customer
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Devansh on  17/06/2020
 */
class FetchCustomerDetails @Inject constructor(
        private val apiRepository: FineractRepository) :
        UseCase<FetchCustomerDetails.RequestValues,
                FetchCustomerDetails.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {
        apiRepository.fetchCustomerDetails(requestValues.customerIdentifier)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<Customer>() {

                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable)
                            = useCaseCallback.onError(e.message)

                    override fun onNext(customer: Customer)
                            = useCaseCallback.onSuccess(ResponseValue(customer))
                })
    }

    class RequestValues(val customerIdentifier: String) : UseCase.RequestValues

    class ResponseValue(val customer: Customer) : UseCase.ResponseValue

}
