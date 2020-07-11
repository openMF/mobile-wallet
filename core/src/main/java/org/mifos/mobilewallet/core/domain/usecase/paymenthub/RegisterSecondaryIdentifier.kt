package org.mifos.mobilewallet.core.domain.usecase.paymenthub

import okhttp3.ResponseBody
import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.paymenthub.entity.RegistrationEntity
import org.mifos.mobilewallet.core.data.paymenthub.repository.PaymentHubRepository
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Devansh on 09/07/2020
 */
class RegisterSecondaryIdentifier @Inject constructor(
        private val paymentHubRepository: PaymentHubRepository) :
        UseCase<RegisterSecondaryIdentifier.RequestValues, RegisterSecondaryIdentifier.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {

        paymentHubRepository.registerSecondaryIdentifier(requestValues.entity)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<ResponseBody>() {
                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable) = useCaseCallback.onError(e.message)


                    override fun onNext(response: ResponseBody) =
                            useCaseCallback.onSuccess(ResponseValue())

                })
    }

    class RequestValues(val entity: RegistrationEntity) : UseCase.RequestValues

    class ResponseValue : UseCase.ResponseValue
}
