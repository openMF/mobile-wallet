/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.domain.usecase.standinginstruction

import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.fineract.repository.FineractRepository
import org.mifospay.core.network.GenericResponse
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class DeleteStandingInstruction @Inject constructor(
    private val apiRepository: FineractRepository,
) : UseCase<DeleteStandingInstruction.RequestValues, DeleteStandingInstruction.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {
        apiRepository.deleteStandingInstruction(requestValues.standingInstructionId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                object : Subscriber<GenericResponse>() {

                    override fun onCompleted() {
                    }

                    override fun onError(e: Throwable) {
                        e.message?.let { useCaseCallback.onError(it) }
                    }

                    override fun onNext(genericResponse: GenericResponse) =
                        useCaseCallback.onSuccess(ResponseValue())
                },
            )
    }

    class RequestValues(val standingInstructionId: Long) :
        UseCase.RequestValues

    class ResponseValue : UseCase.ResponseValue
}
