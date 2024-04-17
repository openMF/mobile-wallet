package org.mifospay.core.data.domain.usecase.savedcards

import org.mifospay.core.data.base.UseCase
import com.mifospay.core.model.entity.savedcards.Card
import org.mifospay.core.data.fineract.repository.FineractRepository
import org.mifospay.core.network.GenericResponse
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
