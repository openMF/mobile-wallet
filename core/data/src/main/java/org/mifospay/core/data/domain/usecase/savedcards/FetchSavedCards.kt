/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifospay.core.data.domain.usecase.savedcards

import com.mifospay.core.model.entity.savedcards.Card
import org.mifospay.core.data.base.UseCase
import org.mifospay.core.data.fineract.repository.FineractRepository
import org.mifospay.core.data.util.Constants
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class FetchSavedCards @Inject constructor(
    private val mFineractRepository: FineractRepository,
) : UseCase<FetchSavedCards.RequestValues, FetchSavedCards.ResponseValue>() {

    class RequestValues(val clientId: Long) : UseCase.RequestValues
    class ResponseValue(val cardList: List<Card>) : UseCase.ResponseValue

    override fun executeUseCase(requestValues: RequestValues) {
        mFineractRepository.fetchSavedCards(requestValues.clientId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                object : Subscriber<List<Card>>() {
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
                },
            )
    }
}
