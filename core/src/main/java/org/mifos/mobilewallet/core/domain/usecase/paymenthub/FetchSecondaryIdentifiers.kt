package org.mifos.mobilewallet.core.domain.usecase.paymenthub

import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import org.mifos.mobilewallet.core.data.paymenthub.entity.Identifier
import org.mifos.mobilewallet.core.data.paymenthub.entity.PartyIdentifiers
import org.mifos.mobilewallet.core.utils.Constants
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Devansh on 10/07/2020
 */
class FetchSecondaryIdentifiers @Inject constructor(
        private val fineractRepository: FineractRepository) :
        UseCase<FetchSecondaryIdentifiers.RequestValues, FetchSecondaryIdentifiers.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {

        fineractRepository.getSecondaryIdentifiers(requestValues.accountExternalId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<PartyIdentifiers>() {
                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable) = useCaseCallback.onError(e.message)

                    override fun onNext(response: PartyIdentifiers) {
                        response.identifierList?.let {
                            useCaseCallback.onSuccess(ResponseValue(it))
                        } ?: useCaseCallback.onError(Constants.NO_IDENTIFIERS_FOUND)
                    }

                })
    }

    class RequestValues(val accountExternalId: String) : UseCase.RequestValues

    class ResponseValue(val identifierList: List<Identifier>) : UseCase.ResponseValue
}
