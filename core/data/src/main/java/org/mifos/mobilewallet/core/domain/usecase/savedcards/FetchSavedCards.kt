package org.mifos.mobilewallet.core.domain.usecase.savedcards

import org.mifos.mobilewallet.core.base.UseCase
import com.mifos.mobilewallet.model.entity.savedcards.Card
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import org.mifos.mobilewallet.core.utils.Constants
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by ankur on 21/May/2018
 */
class FetchSavedCards @Inject constructor(private val mFineractRepository: FineractRepository) :
    UseCase<FetchSavedCards.RequestValues, FetchSavedCards.ResponseValue>() {

    class RequestValues(val clientId: Long) : UseCase.RequestValues
    class ResponseValue(val cardList: List<Card>) : UseCase.ResponseValue

    override fun executeUseCase(requestValues: RequestValues) {
        mFineractRepository.fetchSavedCards(requestValues.clientId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<Card>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    useCaseCallback.onError(e.toString())
                }

                override fun onNext(cards: List<Card>) {
                    if (cards.isNotEmpty()) {
                        useCaseCallback.onSuccess(ResponseValue(cards))
                    } else {
                        useCaseCallback.onError(Constants.NO_SAVED_CARDS)
                    }
                }
            })

    }
}
