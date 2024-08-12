package org.mifospay.core.data.domain.usecase.standinginstruction

import org.mifospay.core.data.base.UseCase
import com.mifospay.core.model.entity.standinginstruction.StandingInstruction
import org.mifospay.core.data.fineract.repository.FineractRepository
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Devansh on 09/06/2020
 */

class FetchStandingInstruction @Inject constructor(
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