package org.mifospay.core.data.domain.usecase.client

import org.mifospay.core.data.base.UseCase
import com.mifospay.core.model.entity.client.Client
import org.mifospay.core.data.fineract.repository.FineractRepository
import org.mifospay.core.data.util.Constants
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by ankur on 24/July/2018
 */
class FetchClientDetails @Inject constructor(private val mFineractRepository: FineractRepository) :
    UseCase<FetchClientDetails.RequestValues, FetchClientDetails.ResponseValue>() {

    data class RequestValues(val clientId: Long) : UseCase.RequestValues
    data class ResponseValue(val client: Client) : UseCase.ResponseValue

    override fun executeUseCase(requestValues: RequestValues) {
        mFineractRepository.getClientDetails(requestValues.clientId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<Client>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    useCaseCallback.onError(Constants.ERROR_FETCHING_CLIENT_DATA)
                }

                override fun onNext(client: Client) {
                    useCaseCallback.onSuccess(ResponseValue(client))
                }
            })
    }
}
