package org.mifos.mobilewallet.core.domain.usecase.account

import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import org.mifos.mobilewallet.mifospay.network.GenericResponse
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by ankur on 29/June/2018
 */
class BlockUnblockCommand @Inject constructor(private val mFineractRepository: FineractRepository) :
    UseCase<BlockUnblockCommand.RequestValues, BlockUnblockCommand.ResponseValue>() {
    protected override fun executeUseCase(requestValues: RequestValues) {
        mFineractRepository.blockUnblockAccount(requestValues.accountId, requestValues.command)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GenericResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    useCaseCallback.onError(
                        "Error " + requestValues.command + "ing account"
                    )
                }

                override fun onNext(genericResponse: GenericResponse) {
                    useCaseCallback.onSuccess(ResponseValue)
                }
            })
    }

    data class RequestValues(val accountId: Long, val command: String) : UseCase.RequestValues
    data object ResponseValue : UseCase.ResponseValue
}
