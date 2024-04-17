package org.mifospay.core.data.domain.usecase.client

import org.mifospay.core.data.base.UseCase
import com.mifospay.core.model.entity.SearchedEntity
import org.mifospay.core.data.fineract.entity.mapper.SearchedEntitiesMapper
import org.mifospay.core.data.fineract.repository.FineractRepository
import com.mifospay.core.model.domain.SearchResult
import org.mifospay.core.data.util.Constants
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by naman on 19/8/17.
 */
class SearchClient @Inject constructor(private val apiRepository: FineractRepository) :
    UseCase<SearchClient.RequestValues, SearchClient.ResponseValue>() {

    @Inject
    lateinit var searchedEntitiesMapper: SearchedEntitiesMapper

    data class RequestValues(val externalId: String) : UseCase.RequestValues
    data class ResponseValue(val results: List<SearchResult>) : UseCase.ResponseValue

    override fun executeUseCase(requestValues: RequestValues) {
        apiRepository.searchResources(requestValues.externalId, Constants.CLIENTS, false)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<SearchedEntity>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    useCaseCallback.onError(Constants.ERROR_SEARCHING_CLIENTS)
                }

                override fun onNext(results: List<SearchedEntity>) {
                    if (results.isNotEmpty()) {
                        useCaseCallback.onSuccess(
                            ResponseValue(
                                searchedEntitiesMapper.transformList(results)
                            )
                        )
                    } else {
                        useCaseCallback.onError(Constants.NO_CLIENTS_FOUND)
                    }
                }
            })
    }
}
