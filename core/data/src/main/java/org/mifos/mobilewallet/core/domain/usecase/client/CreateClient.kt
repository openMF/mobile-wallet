package org.mifos.mobilewallet.core.domain.usecase.client

import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import com.mifos.mobilewallet.model.domain.client.NewClient
import org.mifos.mobilewallet.core.utils.ErrorJsonMessageHelper.getUserMessage
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

    data class RequestValues(val client: com.mifos.mobilewallet.model.domain.client.NewClient) : UseCase.RequestValues
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
}
