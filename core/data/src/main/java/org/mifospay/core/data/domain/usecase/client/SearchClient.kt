/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.domain.usecase.client

import com.mifospay.core.model.domain.SearchResult
import com.mifospay.core.model.entity.SearchedEntity
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.fineract.entity.mapper.SearchedEntitiesMapper
import org.mifospay.core.data.fineract.repository.FineractRepository
import org.mifospay.core.data.util.Constants
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class SearchClient @Inject constructor(
    private val apiRepository: FineractRepository,
) : UseCase<SearchClient.RequestValues, SearchClient.ResponseValue>() {

    @Inject
    lateinit var searchedEntitiesMapper: SearchedEntitiesMapper

    data class RequestValues(val externalId: String) : UseCase.RequestValues
    data class ResponseValue(val results: List<SearchResult>) : UseCase.ResponseValue

    override fun executeUseCase(requestValues: RequestValues) {
        apiRepository.searchResources(requestValues.externalId, Constants.CLIENTS, false)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                object : Subscriber<List<SearchedEntity>>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        useCaseCallback.onError(Constants.ERROR_SEARCHING_CLIENTS)
                    }

                    override fun onNext(results: List<SearchedEntity>) {
                        if (results.isNotEmpty()) {
                            useCaseCallback.onSuccess(
                                ResponseValue(
                                    searchedEntitiesMapper.transformList(results),
                                ),
                            )
                        } else {
                            useCaseCallback.onError(Constants.NO_CLIENTS_FOUND)
                        }
                    }
                },
            )
    }
}
