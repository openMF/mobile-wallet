package org.mifos.mobilewallet.core.domain.usecase.savedcards

import org.mifos.mobilewallet.core.base.UseCase
import com.mifos.mobilewallet.model.entity.savedcards.Card
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import org.mifos.mobilewallet.mifospay.network.GenericResponse
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by ankur on 21/May/2018
 */
class AddCard @Inject constructor(private val mFineractRepository: FineractRepository) :
    UseCase<AddCard.RequestValues, AddCard.ResponseValue>() {
    class RequestValues(val clientId: Long, val card: Card) : UseCase.RequestValues
    class ResponseValue : UseCase.ResponseValue

    override fun executeUseCase(requestValues: RequestValues) {
        mFineractRepository.addSavedCards(requestValues.clientId, requestValues.card)
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