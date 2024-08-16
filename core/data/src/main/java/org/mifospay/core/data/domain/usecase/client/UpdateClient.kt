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

import okhttp3.ResponseBody
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.fineract.repository.FineractRepository
import org.mifospay.core.data.util.ErrorJsonMessageHelper.getUserMessage
import retrofit2.HttpException
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class UpdateClient @Inject constructor(
    private val fineractRepository: FineractRepository,
) : UseCase<UpdateClient.RequestValues, UpdateClient.ResponseValue>() {

    data class RequestValues(val updateClientEntity: Any, val clientId: Long) :
        UseCase.RequestValues

    data class ResponseValue(val responseBody: ResponseBody) : UseCase.ResponseValue

    override fun executeUseCase(requestValues: RequestValues) {
        fineractRepository.updateClient(requestValues.clientId, requestValues.updateClientEntity)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                object : Subscriber<ResponseBody>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        var message: String
                        try {
                            message =
                                (e as HttpException).response()?.errorBody()?.string().toString()
                            message = getUserMessage(message)
                        } catch (e1: Exception) {
                            message = e1.message.toString()
                        }
                        useCaseCallback.onError(message)
                    }

                    override fun onNext(responseBody: ResponseBody) {
                        useCaseCallback.onSuccess(ResponseValue(responseBody))
                    }
                },
            )
    }
}
