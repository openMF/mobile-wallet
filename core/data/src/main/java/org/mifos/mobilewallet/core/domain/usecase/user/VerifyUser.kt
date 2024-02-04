package org.mifos.mobilewallet.core.domain.usecase.user

import okhttp3.ResponseBody
import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.fineract.entity.register.UserVerify
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import org.mifos.mobilewallet.core.utils.Constants
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by naman on 17/8/17.
 */
class VerifyUser @Inject constructor(private val apiRepository: FineractRepository) :
    UseCase<VerifyUser.RequestValues, VerifyUser.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {
        apiRepository.verifyUser(requestValues.userVerify)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<ResponseBody>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    useCaseCallback.onError(Constants.ERROR_VERIFYING_USER)
                }

                override fun onNext(responseBody: ResponseBody) {
                    useCaseCallback.onSuccess(ResponseValue())
                }
            })
    }

    class RequestValues(val userVerify: UserVerify) : UseCase.RequestValues
    class ResponseValue : UseCase.ResponseValue
}
