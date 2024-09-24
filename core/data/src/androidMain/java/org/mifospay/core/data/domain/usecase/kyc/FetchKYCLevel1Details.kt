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
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class FetchKYCLevel1Details(
    private val mFineractRepository: FineractRepository,
) : UseCase<FetchKYCLevel1Details.RequestValues, FetchKYCLevel1Details.ResponseValue>() {

    class RequestValues(val clientId: Int) : UseCase.RequestValues
    class ResponseValue(
        val kycLevel1DetailsList: List<KYCLevel1Details?>,
    ) : UseCase.ResponseValue

    override fun executeUseCase(requestValues: RequestValues) {
        mFineractRepository.fetchKYCLevel1Details(requestValues.clientId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                object : Subscriber<List<KYCLevel1Details?>>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        useCaseCallback.onError(e.toString())
                    }

                    override fun onNext(kycLevel1Details: List<KYCLevel1Details?>) {
                        useCaseCallback.onSuccess(
                            ResponseValue(kycLevel1Details),
                        )
                    }
                },
            )
    }
}
