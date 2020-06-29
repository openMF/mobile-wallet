package org.mifos.mobilewallet.core.domain.usecase.customer

import okhttp3.ResponseBody
import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.common.FineractRepository
import org.mifos.mobilewallet.core.data.fineractcn.entity.customer.Customer
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Devansh on  28/06/2020
 */
class CreateCustomer @Inject constructor(
        private val apiRepository: FineractRepository) :
        UseCase<CreateCustomer.RequestValues,
                CreateCustomer.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {
        apiRepository.createCustomer(requestValues.customerPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<ResponseBody>() {

                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable)
                            = useCaseCallback.onError(e.message)

                    override fun onNext(response: ResponseBody) {

                    }
                })
    }

    class RequestValues(val customerPayload: Customer) : UseCase.RequestValues

    class ResponseValue : UseCase.ResponseValue

}
