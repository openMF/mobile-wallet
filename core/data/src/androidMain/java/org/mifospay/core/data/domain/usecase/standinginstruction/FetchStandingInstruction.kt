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

import com.mifospay.core.model.entity.standinginstruction.StandingInstruction
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.fineract.repository.FineractRepository
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class FetchStandingInstruction(
    private val apiRepository: FineractRepository,
) : UseCase<FetchStandingInstruction.RequestValues, FetchStandingInstruction.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {
        apiRepository.getStandingInstruction(requestValues.standingInstructionId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                object : Subscriber<StandingInstruction>() {

                    override fun onCompleted() {
                    }

                    override fun onError(e: Throwable) {
                        e.message?.let { useCaseCallback.onError(it) }
                    }

                    override fun onNext(standingInstruction: StandingInstruction) =
                        useCaseCallback.onSuccess(ResponseValue(standingInstruction))
                },
            )
    }

    class RequestValues(val standingInstructionId: Long) : UseCase.RequestValues

    class ResponseValue(val standingInstruction: StandingInstruction) : UseCase.ResponseValue
}
