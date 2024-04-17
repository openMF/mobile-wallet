package org.mifospay.core.data.domain.usecase.client

import org.mifospay.core.data.base.UseCase
import com.mifospay.core.model.entity.Page
import com.mifospay.core.model.entity.client.Client
import org.mifospay.core.data.fineract.entity.mapper.ClientDetailsMapper
import org.mifospay.core.data.fineract.repository.FineractRepository
import org.mifospay.core.data.util.Constants
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class FetchClientData @Inject constructor(private val fineractRepository: FineractRepository) :
    UseCase<FetchClientData.RequestValues, FetchClientData.ResponseValue>() {

    @Inject
    lateinit var clientDetailsMapper: ClientDetailsMapper

    override fun executeUseCase(requestValues: RequestValues) {
        requestValues.clientId?.let { clientId ->
            fineractRepository.getSelfClientDetails(clientId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<Client>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        useCaseCallback.onError(Constants.ERROR_FETCHING_CLIENT_DATA)
                    }

                    override fun onNext(client: Client) {
                        useCaseCallback.onSuccess(
                            ResponseValue(clientDetailsMapper.transform(client))
                        )
                    }
                })
        } ?: run {
            fineractRepository.selfClientDetails
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<Page<Client>>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        useCaseCallback.onError(Constants.ERROR_FETCHING_CLIENT_DATA)
                    }

                    override fun onNext(client: Page<Client>) {
                        if (client.pageItems.size != 0) {
                            useCaseCallback.onSuccess(
                                ResponseValue(clientDetailsMapper.transform(client.pageItems[0]))
                            )
                        } else {
                            useCaseCallback.onError(Constants.NO_CLIENT_FOUND)
                        }
                    }
                })
        }
    }

    data class RequestValues(val clientId: Long?) : UseCase.RequestValues
    data class ResponseValue(
        val clientDetails: com.mifospay.core.model.domain.client.Client
    ) : UseCase.ResponseValue
}
