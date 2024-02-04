package org.mifos.mobilewallet.core.domain.usecase.client

import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.fineract.entity.client.Client
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import org.mifos.mobilewallet.core.utils.Constants
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
