package org.mifos.mobilewallet.core.domain.usecase.standinginstruction

import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.fineract.entity.standinginstruction.StandingInstruction
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Devansh on 09/06/2020
 */

class FetchStandingInstruction @Inject constructor(private val apiRepository: FineractRepository)
    : UseCase<FetchStandingInstruction.RequestValues, FetchStandingInstruction.ResponseValue>(){


    override fun executeUseCase(requestValues: RequestValues) {
        apiRepository.getStandingInstruction(requestValues.standingInstructionId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<StandingInstruction>() {

                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable)
                            = useCaseCallback.onError(e.message)


                    override fun onNext(standingInstruction: StandingInstruction)
                            = useCaseCallback.onSuccess(ResponseValue(standingInstruction))
                })
    }

    class RequestValues(val standingInstructionId: Long) : UseCase.RequestValues

    class ResponseValue(val standingInstruction: StandingInstruction)
        : UseCase.ResponseValue

}