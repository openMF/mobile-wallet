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

import com.mifospay.core.model.entity.payload.StandingInstructionPayload
import com.mifospay.core.model.entity.standinginstruction.StandingInstruction
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.fineract.repository.FineractRepository
import org.mifospay.core.network.GenericResponse
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class UpdateStandingInstruction @Inject constructor(
    private val apiRepository: FineractRepository,
) : UseCase<UpdateStandingInstruction.RequestValues, UpdateStandingInstruction.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {
        val validTillString = "${requestValues.standingInstruction.validTill?.get(2)} " +
            "${requestValues.standingInstruction.validTill?.get(1)} " +
            "${requestValues.standingInstruction.validTill?.get(0)}"
        val validFromString = "${requestValues.standingInstruction.validFrom[2]} " +
            "${requestValues.standingInstruction.validFrom[1]} " +
            "${requestValues.standingInstruction.validFrom[0]}"
        val recurrenceOnMonthDayString = "${requestValues.standingInstruction.validFrom[2]} " +
            "${requestValues.standingInstruction.validFrom[1]}"

        val standingInstructionPayload = StandingInstructionPayload(
            requestValues.standingInstruction.fromClient.officeId,
            requestValues.standingInstruction.fromClient.id,
            2,
            "wallet standing transaction",
            1,
            2,
            1,
            requestValues.standingInstruction.fromAccount.id,
            requestValues.standingInstruction.toClient.officeId,
            requestValues.standingInstruction.toClient.id,
            2,
            requestValues.standingInstruction.toAccount.id,
            1,
            requestValues.standingInstruction.amount,
            validFromString,
            1,
            1,
            2,
            "en",
            "dd MM yyyy",
            validTillString,
            recurrenceOnMonthDayString,
            "dd MM",
        )

        apiRepository.updateStandingInstruction(
            requestValues.standingInstructionId,
            standingInstructionPayload,
        )
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

    class RequestValues(
        val standingInstructionId: Long,
        val standingInstruction: StandingInstruction,
    ) : UseCase.RequestValues

    class ResponseValue : UseCase.ResponseValue
}
