package org.mifos.mobilewallet.core.domain.usecase.account

import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.fineract.api.GenericResponse
import org.mifos.mobilewallet.core.data.fineract.entity.payload.UpdateSavingsAccountPayload
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Devansh on 11/07/2020
 */
class UpdateAccount @Inject constructor(private val fineractRepository: FineractRepository) :
        UseCase<UpdateAccount.RequestValues, UpdateAccount.ResponseValue>() {

    override fun executeUseCase(requestValues: RequestValues) {

        fineractRepository.updateSavingsAccount(requestValues.accountId, requestValues.payload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<GenericResponse>() {
                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable) {
                        useCaseCallback.onError(e.message)
                    }

                    override fun onNext(t: GenericResponse) {

                    }
                })

    }


    class RequestValues(val accountId: Long, val payload: UpdateSavingsAccountPayload)
        : UseCase.RequestValues

    class ResponseValue : UseCase.ResponseValue
}