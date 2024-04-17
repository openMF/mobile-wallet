package org.mifospay.core.data.domain.usecase.twofactor

import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.fineract.repository.FineractRepository
import com.mifospay.core.model.domain.twofactor.DeliveryMethod
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by ankur on 01/June/2018
 */
class FetchDeliveryMethods @Inject constructor(private val mFineractRepository: FineractRepository) :
    UseCase<FetchDeliveryMethods.RequestValues, FetchDeliveryMethods.ResponseValue>() {

    class RequestValues : UseCase.RequestValues
    class ResponseValue(
        val deliveryMethodList: List<DeliveryMethod?>
    ) : UseCase.ResponseValue

    override fun executeUseCase(requestValues: RequestValues) {
        mFineractRepository.deliveryMethods
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<DeliveryMethod?>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    useCaseCallback.onError(e.toString())
                }

                override fun onNext(deliveryMethods: List<DeliveryMethod?>) {
                    useCaseCallback.onSuccess(ResponseValue(deliveryMethods))
                }

            })
    }
}
