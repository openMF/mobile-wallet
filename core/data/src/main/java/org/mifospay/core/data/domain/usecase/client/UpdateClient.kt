package org.mifospay.core.data.domain.usecase.client

import okhttp3.ResponseBody
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.fineract.repository.FineractRepository
import org.mifospay.core.data.util.ErrorJsonMessageHelper.getUserMessage
import retrofit2.HttpException
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by naman on 19/8/17.
 */
class UpdateClient @Inject constructor(private val fineractRepository: FineractRepository) :
    UseCase<UpdateClient.RequestValues, UpdateClient.ResponseValue>() {

    data class RequestValues(val updateClientEntity: Any, val clientId: Long) :
        UseCase.RequestValues

    data class ResponseValue(val responseBody: ResponseBody) : UseCase.ResponseValue

    override fun executeUseCase(requestValues: RequestValues) {
        fineractRepository.updateClient(requestValues.clientId, requestValues.updateClientEntity)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<ResponseBody>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    var message: String
                    try {
                        message = (e as HttpException).response()?.errorBody()?.string().toString()
                        message = getUserMessage(message)
                    } catch (e1: Exception) {
                        message = "Error"
                    }
                    useCaseCallback.onError(message)
                }

                override fun onNext(responseBody: ResponseBody) {
                    useCaseCallback.onSuccess(ResponseValue(responseBody))
                }
            })
    }
}