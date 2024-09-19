/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.domain.usecase.user

import com.mifospay.core.model.entity.register.RegisterPayload
import okhttp3.ResponseBody
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.fineract.repository.FineractRepository
import org.mifospay.core.data.util.Constants
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class RegisterUser (
    private val apiRepository: FineractRepository,
) : UseCase<RegisterUser.RequestValues, RegisterUser.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {
        apiRepository.registerUser(requestValues.registerPayload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                object : Subscriber<ResponseBody?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        useCaseCallback.onError(Constants.ERROR_REGISTERING_USER)
                    }

                    override fun onNext(t: ResponseBody?) {
                        useCaseCallback.onSuccess(ResponseValue())
                    }
                },
            )
    }

    data class RequestValues(val registerPayload: RegisterPayload) : UseCase.RequestValues
    class ResponseValue : UseCase.ResponseValue
}
