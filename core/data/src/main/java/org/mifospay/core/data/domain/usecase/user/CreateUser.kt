package org.mifospay.core.data.domain.usecase.user

import com.mifospay.core.model.domain.user.NewUser
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.fineract.repository.FineractRepository
import org.mifospay.core.data.util.ErrorJsonMessageHelper.getUserMessage
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
                    var message = "Error"
                    try {
                        message = (e as HttpException).response()!!.errorBody()!!.string()
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
