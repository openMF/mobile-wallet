package org.mifospay.core.data.domain.usecase.standinginstruction

import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.fineract.repository.FineractRepository
import org.mifospay.core.network.GenericResponse
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Devansh on 09/06/2020
 */
class DeleteStandingInstruction @Inject constructor(
        private val apiRepository: FineractRepository) :
        UseCase<DeleteStandingInstruction.RequestValues,
                DeleteStandingInstruction.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {
        apiRepository.deleteStandingInstruction(requestValues.standingInstructionId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<GenericResponse>() {

                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable) {
                        e.message?.let { useCaseCallback.onError(it) }
                    }

                    override fun onNext(genericResponse: GenericResponse)
                            = useCaseCallback.onSuccess(ResponseValue())
                })
    }

    class RequestValues(val standingInstructionId: Long) :
            UseCase.RequestValues

    class ResponseValue : UseCase.ResponseValue

}