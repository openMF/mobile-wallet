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

import com.mifospay.core.model.domain.twofactor.DeliveryMethod
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.fineract.repository.FineractRepository
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class FetchDeliveryMethods(
    private val mFineractRepository: FineractRepository,
) : UseCase<FetchDeliveryMethods.RequestValues, FetchDeliveryMethods.ResponseValue>() {

    class RequestValues : UseCase.RequestValues
    class ResponseValue(
        val deliveryMethodList: List<DeliveryMethod?>,
    ) : UseCase.ResponseValue

    override fun executeUseCase(requestValues: RequestValues) {
        mFineractRepository.deliveryMethods
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                object : Subscriber<List<DeliveryMethod?>>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        useCaseCallback.onError(e.toString())
                    }

                    override fun onNext(deliveryMethods: List<DeliveryMethod?>) {
                        useCaseCallback.onSuccess(ResponseValue(deliveryMethods))
                    }
                },
            )
    }
}
