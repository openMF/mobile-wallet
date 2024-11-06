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

import com.mifospay.core.model.entity.Page
import com.mifospay.core.model.entity.client.Client
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.fineract.entity.mapper.ClientDetailsMapper
import org.mifospay.core.data.fineract.repository.FineractRepository
import org.mifospay.core.data.util.Constants
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class FetchClientData(
    private val fineractRepository: FineractRepository,
    private val clientDetailsMapper: ClientDetailsMapper,
) : UseCase<FetchClientData.RequestValues, FetchClientData.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {
        requestValues.clientId?.let { clientId ->
            fineractRepository.getSelfClientDetails(clientId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    object : Subscriber<Client>() {
                        override fun onCompleted() {}
                        override fun onError(e: Throwable) {
                            useCaseCallback.onError(Constants.ERROR_FETCHING_CLIENT_DATA)
                        }

                        override fun onNext(client: Client) {
                            useCaseCallback.onSuccess(
                                ResponseValue(clientDetailsMapper.transform(client)),
                            )
                        }
                    },
                )
        } ?: run {
            fineractRepository.selfClientDetails
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    object : Subscriber<Page<Client>>() {
                        override fun onCompleted() {}
                        override fun onError(e: Throwable) {
                            useCaseCallback.onError(Constants.ERROR_FETCHING_CLIENT_DATA)
                        }

                        override fun onNext(client: Page<Client>) {
                            if (client.pageItems.size != 0) {
                                useCaseCallback.onSuccess(
                                    ResponseValue(clientDetailsMapper.transform(client.pageItems[0])),
                                )
                            } else {
                                useCaseCallback.onError(Constants.NO_CLIENT_FOUND)
                            }
                        }
                    },
                )
        }
    }

    data class RequestValues(val clientId: Long?) : UseCase.RequestValues
    data class ResponseValue(
        val clientDetails: com.mifospay.core.model.domain.client.Client,
    ) : UseCase.ResponseValue
}
