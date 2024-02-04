package org.mifos.mobilewallet.core.domain.usecase.user

import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import org.mifos.mobilewallet.core.domain.model.user.NewUser
import org.mifos.mobilewallet.core.utils.ErrorJsonMessageHelper.getUserMessage
import retrofit2.HttpException
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by naman on 17/6/17.
 */
class CreateUser @Inject constructor(private val apiRepository: FineractRepository) :
    UseCase<CreateUser.RequestValues, CreateUser.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {
        apiRepository.createUser(requestValues.user)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<ResponseValue>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    getUserMessage(e)
                    var message: String? = "Error"
                    try {
                        message = (e as HttpException).response().errorBody().string()
                        message = getUserMessage(message)
                    } catch (e1: Exception) {
                        message = "Error"
                    }
                    useCaseCallback.onError(message)
                }

                override fun onNext(genericResponse: ResponseValue) {
                    useCaseCallback.onSuccess(genericResponse)
                }
            })
    }

    class RequestValues(val user: NewUser) : UseCase.RequestValues
    class ResponseValue(val userId: Int) : UseCase.ResponseValue
}
