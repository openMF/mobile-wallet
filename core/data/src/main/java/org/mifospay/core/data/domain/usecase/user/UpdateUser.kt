package org.mifospay.core.data.domain.usecase.user

import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.fineract.repository.FineractRepository
import org.mifospay.core.data.util.ErrorJsonMessageHelper.getUserMessage
import org.mifospay.core.network.GenericResponse
import retrofit2.HttpException
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by ankur on 25/June/2018
 */
class UpdateUser @Inject constructor(
    private val mFineractRepository: FineractRepository
) : UseCase<UpdateUser.RequestValues, UpdateUser.ResponseValue?>() {
    override fun executeUseCase(requestValues: RequestValues) {
        mFineractRepository.updateUser(requestValues.updateUserEntity, requestValues.userId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GenericResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    var message = "Error"
                    try {
                        message = (e as HttpException).response()!!.errorBody()!!.string()
                        message = getUserMessage(message)
                    } catch (e1: Exception) {
                        message = "Error"
                    }
                    useCaseCallback.onError(message)
                }

                override fun onNext(genericResponse: GenericResponse?) {
                    useCaseCallback.onSuccess(ResponseValue())
                }
            })
    }

    class RequestValues(val updateUserEntity: Any, val userId: Int) : UseCase.RequestValues

    class ResponseValue : UseCase.ResponseValue
}
