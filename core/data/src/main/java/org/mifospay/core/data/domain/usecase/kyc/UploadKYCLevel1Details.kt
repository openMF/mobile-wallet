/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.domain.usecase.kyc

import com.mifospay.core.model.entity.kyc.KYCLevel1Details
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.fineract.repository.FineractRepository
import org.mifospay.core.network.GenericResponse
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class UploadKYCLevel1Details(
    var mFineractRepository: FineractRepository,
) : UseCase<UploadKYCLevel1Details.RequestValues, UploadKYCLevel1Details.ResponseValue>() {
    class RequestValues(
        val clientId: Int,
        val mKYCLevel1Details: KYCLevel1Details,
    ) : UseCase.RequestValues

    class ResponseValue : UseCase.ResponseValue

    override fun executeUseCase(requestValues: RequestValues) {
        mFineractRepository.uploadKYCLevel1Details(
            requestValues.clientId,
            requestValues.mKYCLevel1Details,
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                object : Subscriber<GenericResponse>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        useCaseCallback.onError(e.toString())
                    }

                    override fun onNext(t: GenericResponse) {
                        useCaseCallback.onSuccess(ResponseValue())
                    }
                },
            )
    }
}
