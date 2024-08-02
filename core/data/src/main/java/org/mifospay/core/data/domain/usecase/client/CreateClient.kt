package org.mifospay.core.data.domain.usecase.client

import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.fineract.repository.FineractRepository
import org.mifospay.core.data.util.ErrorJsonMessageHelper.getUserMessage
import retrofit2.HttpException
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by naman on 20/8/17.
 */
class CreateClient @Inject constructor(private val apiRepository: FineractRepository) :
    UseCase<CreateClient.RequestValues, CreateClient.ResponseValue>() {

    data class RequestValues(val client: com.mifospay.core.model.domain.client.NewClient) : UseCase.RequestValues
    data class ResponseValue(val clientId: Int) : UseCase.ResponseValue

    override fun executeUseCase(requestValues: RequestValues) {
        apiRepository.createClient(requestValues.client)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<ResponseValue>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    var message: String
                    try {
                        message = (e as HttpException).response()?.errorBody()?.string().toString()
                        message = getUserMessage(message)
                    } catch (e1: Exception) {
                        message = e1.message.toString()
                    }
                    useCaseCallback.onError(message)
                }

                override fun onNext(genericResponse: ResponseValue) {
                    useCaseCallback.onSuccess(genericResponse)
                }
            })
    }
}
