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
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class FetchClientImage(
    private val mFineractRepository: FineractRepository,
) :
    UseCase<FetchClientImage.RequestValues, FetchClientImage.ResponseValue>() {

    data class RequestValues(val clientId: Long) : UseCase.RequestValues
    data class ResponseValue(val responseBody: ResponseBody) : UseCase.ResponseValue

    override fun executeUseCase(requestValues: RequestValues) {
        mFineractRepository.getClientImage(requestValues.clientId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                object : Subscriber<ResponseBody>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        getUserMessage(e)?.let { useCaseCallback.onError(it) }
                    }

                    override fun onNext(responseBody: ResponseBody) {
                        useCaseCallback.onSuccess(ResponseValue(responseBody))
                    }
                },
            )
    }
}
