package org.mifos.mobilewallet.core.domain.usecase.standinginstruction

import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.fineract.entity.Page
import org.mifos.mobilewallet.core.data.fineract.entity.standinginstruction.StandingInstruction
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Devansh 08/06/2020
 */

class GetAllStandingInstructions @Inject constructor(private val apiRepository: FineractRepository) :
        UseCase<GetAllStandingInstructions.RequestValues, GetAllStandingInstructions.ResponseValue>() {


    override fun executeUseCase(requestValues: RequestValues) {
        apiRepository.getAllStandingInstructions(requestValues.clientId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<Page<StandingInstruction>>() {

                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable) =
                            useCaseCallback.onError(e.message)


                    override fun onNext(standingInstructionPage: Page<StandingInstruction>)
                            = useCaseCallback.onSuccess(ResponseValue(standingInstructionPage.pageItems))

                })
    }

    class RequestValues(val clientId: Long) : UseCase.RequestValues

    class ResponseValue(val standingInstructionsList: List<StandingInstruction>)
        : UseCase.ResponseValue

}