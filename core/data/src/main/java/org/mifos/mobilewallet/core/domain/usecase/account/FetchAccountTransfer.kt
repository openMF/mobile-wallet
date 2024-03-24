package org.mifos.mobilewallet.core.domain.usecase.account

import com.mifos.mobilewallet.model.entity.accounts.savings.TransferDetail
import org.mifos.mobilewallet.core.base.UseCase
import org.mifos.mobilewallet.core.data.fineract.repository.FineractRepository
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by ankur on 05/June/2018
 */
class FetchAccountTransfer @Inject constructor(
    private val mFineractRepository: FineractRepository
) : UseCase<FetchAccountTransfer.RequestValues, FetchAccountTransfer.ResponseValue?>() {
    override fun executeUseCase(requestValues: RequestValues) {
        mFineractRepository.getAccountTransfer(requestValues.transferId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<TransferDetail>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    useCaseCallback.onError(e.toString())
                }

                override fun onNext(transferDetail: TransferDetail) {
                    useCaseCallback.onSuccess(ResponseValue(transferDetail))
                }
            })
    }

    data class RequestValues(var transferId: Long) : UseCase.RequestValues
    data class ResponseValue(val transferDetail: TransferDetail) : UseCase.ResponseValue
}
