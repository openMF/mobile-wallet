package org.mifos.mobilewallet.core.domain.usecase.savedcards

import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.fineract.api.GenericResponse
import org.mifos.mobilewallet.core.data.fineract.entity.savedcards.Card
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by ankur on 22/May/2018
 */
class EditCard @Inject constructor(private val mFineractRepository: FineractRepository) :
    UseCase<EditCard.RequestValues, EditCard.ResponseValue>() {

    class RequestValues(val clientId: Int, val card: Card) : UseCase.RequestValues
    class ResponseValue : UseCase.ResponseValue

    override fun executeUseCase(requestValues: RequestValues) {
        mFineractRepository.editSavedCard(requestValues.clientId, requestValues.card)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GenericResponse?>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    useCaseCallback.onError(e.toString())
                }

                override fun onNext(t: GenericResponse?) {
                    useCaseCallback.onSuccess(ResponseValue())
                }
            })

    }
}
