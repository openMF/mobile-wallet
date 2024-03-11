package org.mifos.mobilewallet.core.domain.usecase.client

import org.mifos.mobilewallet.core.base.UseCase
import com.mifos.mobilewallet.model.entity.Page
import com.mifos.mobilewallet.model.entity.client.Client
import org.mifos.mobilewallet.core.data.fineract.entity.mapper.ClientDetailsMapper
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import org.mifos.mobilewallet.core.utils.Constants
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by naman on 17/6/17.
 */
class FetchClientData @Inject constructor(private val fineractRepository: FineractRepository) :
    UseCase<FetchClientData.RequestValues, FetchClientData.ResponseValue>() {

    @Inject
    lateinit var clientDetailsMapper: ClientDetailsMapper

    override fun executeUseCase(requestValues: RequestValues) {
        if (requestValues != null) {
            fineractRepository.getSelfClientDetails(requestValues.clientid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<Client>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        useCaseCallback.onError(Constants.ERROR_FETCHING_CLIENT_DATA)
                    }

                    override fun onNext(client: Client) {
                        useCaseCallback.onSuccess(
                            ResponseValue(
                                clientDetailsMapper.transform(client)
                            )
                        )
                    }
                })
        } else {
            fineractRepository.selfClientDetails
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<Page<Client>>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        useCaseCallback.onError(Constants.ERROR_FETCHING_CLIENT_DATA)
                    }

                    override fun onNext(client: Page<Client>) {
                        if (client.pageItems != null && client.pageItems.size != 0) {
                            useCaseCallback.onSuccess(
                                ResponseValue(
                                    clientDetailsMapper
                                        .transform(client.pageItems[0])
                                )
                            )
                        } else {
                            useCaseCallback.onError(Constants.NO_CLIENT_FOUND)
                        }
                    }
                })
        }
    }

    data class RequestValues(val clientid: Long) : UseCase.RequestValues
    data class ResponseValue(val userDetails: com.mifos.mobilewallet.model.domain.client.Client) :
        UseCase.ResponseValue
}
