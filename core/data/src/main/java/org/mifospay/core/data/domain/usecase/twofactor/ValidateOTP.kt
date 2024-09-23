/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.domain.usecase.twofactor

import com.mifospay.core.model.domain.twofactor.AccessToken
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.fineract.repository.FineractRepository
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class ValidateOTP(
    private val mFineractRepository: FineractRepository,
) : UseCase<ValidateOTP.RequestValues, ValidateOTP.ResponseValue>() {

    class RequestValues(val token: String) : UseCase.RequestValues
    class ResponseValue(val accessToken: AccessToken) : UseCase.ResponseValue

    override fun executeUseCase(requestValues: RequestValues) {
        mFineractRepository.validateToken(requestValues.token)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                object : Subscriber<AccessToken>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        useCaseCallback.onError(e.toString())
                    }

                    override fun onNext(accessToken: AccessToken) {
                        useCaseCallback.onSuccess(ResponseValue(accessToken))
                    }
                },
            )
    }
}
