package org.mifos.mobilewallet.core.domain.usecase.user

import okhttp3.ResponseBody
import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.fineract.entity.register.RegisterPayload
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import org.mifos.mobilewallet.core.utils.Constants
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by naman on 17/8/17.
 */
class RegisterUser @Inject constructor(private val apiRepository: FineractRepository) :
    UseCase<RegisterUser.RequestValues, RegisterUser.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {
        apiRepository.registerUser(requestValues.registerPayload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<ResponseBody>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    useCaseCallback.onError(Constants.ERROR_REGISTERING_USER)
                }

                override fun onNext(responseBody: ResponseBody) {
                    useCaseCallback.onSuccess(ResponseValue())
                }
            })
    }

    class RequestValues(val registerPayload: RegisterPayload) : UseCase.RequestValues
    class ResponseValue : UseCase.ResponseValue
}
