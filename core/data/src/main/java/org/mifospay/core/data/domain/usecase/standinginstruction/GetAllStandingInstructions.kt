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

import com.mifospay.core.model.entity.Page
import com.mifospay.core.model.entity.standinginstruction.StandingInstruction
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.fineract.repository.FineractRepository
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class GetAllStandingInstructions (
    private val apiRepository: FineractRepository,
) : UseCase<GetAllStandingInstructions.RequestValues, GetAllStandingInstructions.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {
        apiRepository.getAllStandingInstructions(requestValues.clientId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                object : Subscriber<Page<StandingInstruction>>() {

                    override fun onCompleted() {
                    }

                    override fun onError(e: Throwable) {
                        e.message?.let { useCaseCallback.onError(it) }
                    }

                    override fun onNext(standingInstructionPage: Page<StandingInstruction>) {
                        return useCaseCallback.onSuccess(
                            ResponseValue(standingInstructionPage.pageItems),
                        )
                    }
                },
            )
    }

    class RequestValues(val clientId: Long) : UseCase.RequestValues

    class ResponseValue(val standingInstructionsList: List<StandingInstruction>) :
        UseCase.ResponseValue
}
